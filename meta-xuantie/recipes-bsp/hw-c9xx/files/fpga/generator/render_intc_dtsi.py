#!/usr/bin/env python3
"""
render_intc_dtsi.py - Build intc (PLIC/CLINT or APLIC/IMSIC) macro defines
from a platform JSON, and (as a standalone CLI) render data/intc.dtsi.

The macro substitution itself is done by the shared dtsi_engine, using the
data/templates/{plic,aplic}.dtsi.in templates (master dtsi kept verbatim, with
base addresses / core-scaled fields turned into ${MACRO}/##IF##/##REPEAT##
directives). generate_dts.py imports the build_plic()/build_aplic() helpers
here directly; the CLI path is retained for standalone/legacy use.

Usage:
    render_intc_dtsi.py --platform <name> --cpu <cpu> --cores <N>
                        [--intc plic|aplic] [--output <path>]

Host requirements:  python3 only. No cpp/jq needed.
"""

import argparse
import json
import re
import sys
from pathlib import Path


FPGA_DIR = Path(__file__).resolve().parent.parent
DATA_DIR = FPGA_DIR / "data"
PLATFORMS_DIR = DATA_DIR / "platforms"
SCHEMA_PATH = DATA_DIR / "soc-schema.json"


# -- helpers -----------------------------------------------------------------

def isa_has_sstc(isa):
    """True if the ISA string declares the Sstc extension (stimecmp CSR).

    Sstc presence is read from the actual ISA string (underscore-joined tokens)
    rather than a hard-coded CPU list, so the timer wiring always follows the
    CPU's real extensions."""
    return "sstc" in (isa or "").lower().split("_")


def _validate_platform(name, plat):
    """Validate a platform dict against soc-schema.json.

    jsonschema is an optional dependency: the generator is python3-only and
    runs on FPGA verification hosts that may not have it installed. When it is
    present we hard-fail on a schema violation (catches typos / wrong fields);
    when it is absent we print a one-line notice and skip (legacy behavior)."""
    try:
        import jsonschema
    except ImportError:
        print(f"render_intc_dtsi: jsonschema not installed; skipping schema "
              f"validation of platform '{name}' "
              f"(pip install jsonschema to enable)", file=sys.stderr)
        return
    if not SCHEMA_PATH.exists():
        return
    schema = json.loads(SCHEMA_PATH.read_text())
    try:
        jsonschema.validate(plat, schema)
    except jsonschema.ValidationError as e:
        loc = "/".join(str(x) for x in e.absolute_path) or "<root>"
        sys.exit(f"render_intc_dtsi: platform '{name}' violates soc-schema at "
                 f"'{loc}': {e.message}")


def load_platform(name):
    p = PLATFORMS_DIR / f"{name}.json"
    if not p.exists():
        avail = sorted(x.stem for x in PLATFORMS_DIR.glob("*.json"))
        sys.exit(f"render_intc_dtsi: unknown platform '{name}' "
                 f"(available: {', '.join(avail)})")
    plat = json.loads(p.read_text())
    _validate_platform(name, plat)
    return plat


def phandle_list_per_core(*suffixes, ids, indent="        "):
    """Interleaved per-core phandle list (one line per core).

    Used for PLIC ("0xffffffff 9" per core) and CLINT ("3 7" per core),
    where the legacy dtsi puts both interrupt specifiers on the same line.
    Returns a string with newlines between cores so the template can
    drop it directly inside the "interrupts-extended = < ... >;" block.

    `ids` is the list of physical core indices to emit (LSB-first enabled
    set), so asymmetric layouts reference the correct &cpuN_intc phandles.

    Continuation lines are padded with `indent` (default 8 spaces) so the
    rendered list aligns with the first line, which in the templates sits
    at the 8-space indent level.
    """
    return f"\n{indent}".join(
        " ".join(f"&cpu{i}_intc {s}" for s in suffixes) for i in ids
    )


# -- per-INTC builders -------------------------------------------------------

def _hex(value):
    """Parse a decimal-or-0x-prefixed string/int and emit 0x-prefixed lowercase hex.

    JSON keeps explicit "0x..." for readability; the rendered DTS uses the
    conventional 0x-prefixed hex style (e.g. "0x8000000", "0x4000").
    """
    if isinstance(value, int):
        return f"0x{value:x}"
    return f"0x{int(value, 0):x}"


def _hex_noprefix(value):
    """Like _hex but without 0x prefix — for DTS node unit-addresses."""
    if isinstance(value, int):
        return f"{value:x}"
    return f"{int(value, 0):x}"


def _parse(value):
    return value if isinstance(value, int) else int(value, 0)


def _cells(val):
    """Format a 64-bit value as a 2-cell "hi lo" pair."""
    v = _parse(val)
    return f"0x{(v >> 32) & 0xFFFFFFFF:x} 0x{v & 0xFFFFFFFF:x}"


def _imsic_slots(num_cores):
    """Round num_cores up to the smallest legacy overlay boundary {2,4,16,32}.

    Master ships aplic_{2,4,16}cores.dtsi; the IMSIC window per overlay is
    sized for that exact core count, so 5-8 cores share the aplic16core layout
    (16 slots), 3-4 share aplic4core (4 slots), etc.
    """
    for boundary in (2, 4, 16, 32):
        if num_cores <= boundary:
            return boundary
    return num_cores


def build_aplic(plat, core_ids):
    a = plat["intc"]["aplic"]
    s = a["aplic_s"]; m = a["aplic_m"]; im = a["imsics_m"]; is_ = a["imsics_s"]
    num_cores = len(core_ids)
    slots = _imsic_slots(num_cores)
    imsics_m_size = _parse(im["size_per_core"]) * slots
    imsics_s_size = _parse(is_["size_per_core"]) * slots
    return {
        "APLIC_S_UNIT_ADDR":     _hex_noprefix(s["base"]),
        "APLIC_S_BASE":          _cells(s["base"]),
        "APLIC_S_SIZE":          _cells(s["size"]),
        "APLIC_M_UNIT_ADDR":     _hex_noprefix(m["base"]),
        "APLIC_M_BASE":          _cells(m["base"]),
        "APLIC_M_SIZE":          _cells(m["size"]),
        "NUM_SOURCES":           _hex(m["num_sources"]),
        "DELEGATE_RANGE":        " ".join(_hex(x) for x in m["delegate_range"].split()),
        "IMSICS_M_UNIT_ADDR":    _hex_noprefix(im["base"]),
        "IMSICS_M_BASE":         _cells(im["base"]),
        "IMSICS_M_SIZE":         _cells(imsics_m_size),
        "IMSICS_S_UNIT_ADDR":    _hex_noprefix(is_["base"]),
        "IMSICS_S_BASE":         _cells(is_["base"]),
        "IMSICS_S_SIZE":         _cells(imsics_s_size),
        "IMSICS_NUM_IDS":        _hex(is_["num_ids"]),
        "IMSICS_GUEST_IDX_BITS": _hex(is_["guest_index_bits"]),
        "IMSICS_M_PARENTS":      phandle_list_per_core("11", ids=core_ids, indent="\t\t\t\t"),
        "IMSICS_S_PARENTS":      phandle_list_per_core("9",  ids=core_ids, indent="\t\t\t\t"),
    }


def _apply_mtimer(defines, plat, has_sstc):
    """Emit an ACLINT mtimer node only when the CPU lacks Sstc.

    The mtimer node lives in the aplic (AIA) path: an AIA CPU with Sstc uses
    stimecmp and omits it, while an AIA CPU without Sstc needs the explicit
    ACLINT mtimer node for timer interrupts. Extension presence drives the node
    — there is no static enable flag."""
    if has_sstc:
        return
    a = plat.get("intc", {}).get("aplic", {})
    m = a.get("mtimer")
    if not m:
        # Fixed SoCs with a per-core mtimer array (mtimer_percore) serve the
        # Sstc-less case through render_mtimer_percore; no single node needed.
        if a.get("mtimer_percore"):
            return
        sys.exit("render_intc_dtsi: CPU lacks Sstc but platform has "
                 "no 'intc.aplic.mtimer' config for the APLIC timer node")
    defines["HAS_MTIMER"]           = "1"
    defines["MTIMER_UNIT_ADDR"]     = _hex_noprefix(m["base"])
    defines["MTIMER_MTIMECMP_BASE"] = _cells(m["mtimecmp_addr"])
    defines["MTIMER_MTIMECMP_SIZE"] = _cells("0x08")
    defines["MTIMER_MTIME_BASE"]    = _cells(m["base"])
    defines["MTIMER_MTIME_SIZE"]    = _cells(m["mtime_size"])


def build_plic(plat, core_ids):
    p = plat["intc"]["plic"]
    clint = p["clint"]; sswi = p["sswi"]; ctrl = p["controller"]
    defines = {
        "PLIC_UNIT_ADDR": _hex_noprefix(ctrl["base"]),
        "PLIC_BASE":      _cells(ctrl["base"]),
        "PLIC_SIZE":      _cells(ctrl["size"]),
        "PLIC_NDEV":      _hex(ctrl["ndev"]),
        "PLIC_MAX_PRIO":  _hex(ctrl["max_priority"]),
        "CLINT_UNIT_ADDR": _hex_noprefix(clint["base"]),
        "CLINT_BASE":      _cells(clint["base"]),
        "CLINT_SIZE":      _cells(clint["size"]),
        "SSWI_UNIT_ADDR": _hex_noprefix(sswi["base"]),
        "SSWI_BASE":       _cells(sswi["base"]),
        "SSWI_SIZE":       _cells(sswi["size"]),
        "CLINT_PARENTS":  phandle_list_per_core("3", "7", ids=core_ids),
        "PLIC_PARENTS":   phandle_list_per_core("0xffffffff", "9", ids=core_ids),
    }
    if not clint.get("has_64bit_mmio", True):
        defines["CLINT_NO_64BIT_MMIO"] = "1"
    return defines


def detect_intc_kind(plat):
    """Infer intc kind from the intc sub-blocks (plic wins if both present).

    This is only the standalone-CLI fallback; the main pipeline picks the kind
    per-CPU via generate_dts.select_intc_kind (and --intc overrides both)."""
    intc = plat.get("intc", {})
    if "plic" in intc:
        return "plic"
    if "aplic" in intc:
        return "aplic"
    sys.exit("render_intc_dtsi: cannot detect intc kind from platform "
             "(expected 'plic' or 'aplic' sub-block under intc)")


# -- main --------------------------------------------------------------------

def main():
    ap = argparse.ArgumentParser(description="Render dts/intc.dtsi")
    ap.add_argument("--platform", required=True,
                    help="platform config basename (no .json)")
    ap.add_argument("--cpu",      required=True,
                    help="CPU model (e.g. c930, c925, c908)")
    ap.add_argument("--cores",    required=True, type=int,
                    help="number of enabled cores")
    ap.add_argument("--intc",
                    help="override intc kind (plic|aplic); "
                         "default = detected from platform intc block")
    ap.add_argument("--output",   default=str(DATA_DIR / "intc.dtsi"),
                    help="output path (default: data/intc.dtsi)")
    args = ap.parse_args()

    plat = load_platform(args.platform)
    intc = (args.intc or detect_intc_kind(plat)).lower()
    if intc not in ("plic", "aplic"):
        sys.exit(f"render_intc_dtsi: unsupported intc '{intc}' "
                 f"(want plic|aplic)")

    max_cores = int(plat.get("max_cores", 1))
    if not (1 <= args.cores <= max_cores):
        sys.exit(f"render_intc_dtsi: --cores {args.cores} out of range for "
                 f"platform {args.platform} (max_cores={max_cores})")

    # Standalone CLI enables a contiguous core range 0..N-1.
    core_ids = list(range(args.cores))
    if intc == "aplic":
        defines = build_aplic(plat, core_ids)
        # Resolve the CPU's ISA to detect Sstc (lazy import avoids a cycle with
        # generate_dts, which imports this module).
        import generate_dts
        isa = generate_dts.load_cpu_isa_configs().get(args.cpu, "")
        _apply_mtimer(defines, plat, isa_has_sstc(isa))
    else:
        defines = build_plic(plat, core_ids)

    template = DATA_DIR / "templates" / "intc" / f"{intc}.dtsi.in"
    if not template.exists():
        sys.exit(f"render_intc_dtsi: template not found: {template}")

    import dtsi_engine
    if intc == "plic":
        repeats = {"CORES": [{"CORE_ID": i} for i in core_ids]}
    else:
        repeats = {}
    rendered = dtsi_engine.render(template.read_text(), defines, repeats)
    Path(args.output).write_text(rendered)

    mtimer_note = " +mtimer" if defines.get("HAS_MTIMER") else ""
    print(f"rendered {args.output}  "
          f"(platform={args.platform}, cpu={args.cpu}, "
          f"cores={args.cores}, intc={intc}{mtimer_note})")


if __name__ == "__main__":
    main()
