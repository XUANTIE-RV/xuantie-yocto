#!/usr/bin/env python3
"""
generate_dts.py — DTS generator for XuanTie FPGA verification.

Templates are the master dtsi files kept (near) verbatim under data/templates/
with only the varying literals turned into ${MACRO}/##IF##/##REPEAT## directives
(see generator/dtsi_engine.py). Everything else — base addresses, SoC constants,
and boot params — lives in the platform JSON (data/platforms/).

Replaces: update_dts_cpu.sh (sed + symlink assembly)
Usage:
    generate_dts.py --cpu c930 --layout 11111111
    generate_dts.py --cpu c908 --layout 1111 --memsize 2G
    generate_dts.py --cpu c907-rv32 --layout 1111
    generate_dts.py --cpu c908x --bus stlk --gmac pro
"""

import argparse
import os
import re
import sys
from fnmatch import fnmatch
from math import ceil
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import dtsi_engine
import render_intc_dtsi as intc
import render_ntrace_dts


FPGA_DIR = Path(__file__).resolve().parent.parent
DATA_DIR = FPGA_DIR / "data"
TMPL_DIR = DATA_DIR / "templates"
PLATFORMS_DIR = DATA_DIR / "platforms"
CPU_ISA_DIR = DATA_DIR / "cpu-isa-configs"

CPU_ALIASES = {
    "c930": "xt-c9301fdt",
    "c925": "xt-c9251fdt",
    "c950": "xt-c9501fdvt",
    "c9204": "xt-c9204fdt",
    "c9205": "xt-c9205fdt",
}


# ---------------------------------------------------------------------------
# ISA string parsing
# ---------------------------------------------------------------------------

def parse_isa_string(isa):
    """Split ISA string into (arch, base, extensions_list).

    rv64imac_foo → ("rv64", "rv64i", ['"i"', '"m"', '"a"', '"c"', '"foo"'])

    The base letter (e.g. "i") is kept in the extensions list to match the
    riscv,isa-extensions property emitted by the legacy generator.
    """
    m = re.match(r'^(rv\d+)([a-z]+)(.*)', isa)
    if not m:
        raise ValueError(f"Cannot parse ISA string: {isa}")

    arch = m.group(1)
    base_letters = m.group(2)
    rest = m.group(3)

    base = arch + base_letters[0]
    exts = [f'"{c}"' for c in base_letters]

    if rest.startswith('_'):
        rest = rest[1:]
    if rest:
        for ext in rest.split('_'):
            if ext:
                exts.append(f'"{ext}"')

    return arch, base, exts


# AIA (Advanced Interrupt Architecture) ISA extensions. These only make sense
# with an aplic/imsics interrupt tree, so their presence is driven by the intc
# kind rather than the CPU's raw ISA config: aplic auto-fills them (so an older
# CPU whose ISA omits them still reports AIA when wired to aplic), plic removes
# them. Svvptc is unrelated to AIA and is never touched here.
AIA_EXTS = ("smaia", "smcsrind", "ssaia", "sscsrind")


def _apply_aia_policy(isa, isa_exts, intc_kind):
    """Reconcile AIA extensions in riscv,isa with the interrupt controller.

    aplic  -> ensure every AIA extension is present (append the missing ones)
    plic   -> remove all AIA extensions

    Operates on both the underscore-joined isa string and the quoted isa_exts
    list; returns the updated (isa, isa_exts)."""
    tokens = isa.split("_")
    if intc_kind == "aplic":
        present = set(tokens)
        missing = [e for e in AIA_EXTS if e not in present]
        if missing:
            isa = "_".join(tokens + missing)
            isa_exts = isa_exts + [f'"{e}"' for e in missing]
    else:
        drop = set(AIA_EXTS)
        isa = "_".join(t for t in tokens if t not in drop)
        isa_exts = [e for e in isa_exts if e.strip('"') not in drop]
    return isa, isa_exts


# ---------------------------------------------------------------------------
# CPU ISA config loading
# ---------------------------------------------------------------------------

def load_cpu_isa_configs():
    """Parse bash conf files → {cpu_model: isa_string}."""
    configs = {}
    if not CPU_ISA_DIR.is_dir():
        return configs
    for conf in sorted(CPU_ISA_DIR.glob("*.conf")):
        for line in conf.read_text().splitlines():
            line = line.strip()
            m = re.match(r'default_isa\[(.+)\]="(.+)"', line)
            if m:
                configs[m.group(1)] = m.group(2)
    return configs


# ---------------------------------------------------------------------------
# Component selection
# ---------------------------------------------------------------------------

def cpu_match(cpu, patterns):
    return any(fnmatch(cpu, p) for p in patterns)


# Columns after cpu_pattern in data/platforms/platform-selection.csv, in order.
# That CSV is the SOLE SOURCE for the per-CPU name-family choice of platform /
# intc / pmu / gmac / link / ntrace (the ntrace column is read via
# select_trace_config; v1/v2 = version, unsupported/- = fail fast). The pcu
# column is a per-CPU override of the platform JSON's hsm.pcu.version default
# (read via select_pcu_version; '-' = keep the platform default).
_SELECTION_COLUMNS = ("platform", "intc", "pmu", "gmac", "link", "ntrace", "pcu")


def platform_selection_row(cpu):
    """Return the first platform-selection.csv row matching the alias-normalized
    cpu name, as a dict keyed by column name (platform / intc / pmu / gmac /
    link / ntrace / pcu), or None if the file is absent or no row matches. Rows
    are matched top-to-bottom with fnmatch, first hit wins. Adding a CPU family
    = add/adjust a CSV row, no Python change. arch=rv32 and the -gmac / -intc /
    --platform overrides still take precedence over this table in Python."""
    csv_path = PLATFORMS_DIR / "platform-selection.csv"
    if not csv_path.exists():
        return None
    for line in csv_path.read_text().splitlines():
        line = line.strip()
        if not line or line.startswith("#") or line.startswith("cpu_pattern"):
            continue
        parts = [p.strip() for p in line.split(",")]
        if len(parts) < 1 + len(_SELECTION_COLUMNS):
            continue
        if cpu_match(cpu, [parts[0]]):
            return dict(zip(_SELECTION_COLUMNS, parts[1:]))
    return None


def select_pmu(cpu, arch):
    # rv32 is arch-driven and cross-cuts every CPU name pattern (the CSV pmu
    # column carries the RV64 name-family value), so it wins over the CSV lookup.
    if arch == "rv32":
        return "pmu/pmu_v1_rv32.dtsi.in"
    row = platform_selection_row(cpu)
    pmu = (row or {}).get("pmu") or "pmu_v1"
    return f"pmu/{pmu}.dtsi.in"


def select_intc_kind(cpu):
    row = platform_selection_row(cpu)
    return (row or {}).get("intc") or "plic"


# Legacy -intc aplic overlay names accepted for backward compatibility. The old
# update_dts_cpu.sh took aplic2core / aplic4core / aplic16core (the master
# aplic_{2,4,16}cores.dtsi files). The new generator derives the IMSIC window /
# core count from -layout, so the <N>core suffix is informational only and these
# collapse to the canonical 'aplic'. Any OTHER aplic* value (e.g. aplic8core) is
# rejected rather than silently accepted.
_APLIC_ALIASES = frozenset({"aplic", "aplic2core", "aplic4core", "aplic16core"})


def normalize_intc_kind(kind):
    """Normalize -intc to the canonical 'plic' / 'aplic'.

    Only the whitelisted legacy aplic aliases (aplic2core/aplic4core/
    aplic16core) and the canonical 'aplic' collapse to 'aplic'; 'plic' stays
    'plic'. Everything else passes through unchanged so render_intc rejects it
    with a clear error."""
    if not kind:
        return kind
    k = kind.strip().lower()
    if k in _APLIC_ALIASES:
        return "aplic"
    if k == "plic":
        return "plic"
    return k


def _soc_family(platform):
    """SoC family = platform name with the trailing _v<N>[...] version/arch
    suffix stripped (xiaohui_v1 / xiaohui_v2 / xiaohui_v1_rv32 -> xiaohui). The
    family, not the full platform, keys the ntrace address-map file: CPUs of the
    same family share one apb map per trace version even when they sit on
    different platform jsons (e.g. c920v3 on xiaohui_v1 and c930 on xiaohui_v2
    both use ntrace_xiaohui_v2)."""
    return re.sub(r"_v\d+.*$", "", platform)


def select_trace_config(cpu, platform):
    """Resolve the N-Trace constants-json *stem* for auto-selection.

    File-name rule: ntrace_<SoC family>_v<trace version>, e.g.
    ntrace_xiaohui_v1 / ntrace_xiaohui_v2.

    The trace *version* comes from the `ntrace` column of
    platform-selection.csv (SOLE SOURCE), read via platform_selection_row (same
    fnmatch first-hit-wins table as platform/intc/pmu/gmac/link). Column values:
    `v1` / `v2` -> version; `unsupported` fails fast; `-` (or no matching row)
    means no N-Trace for this CPU and also fails fast.

    Because `dmi_apb_base` (and the other base addresses) are a per-SoC
    address-map property rather than a version property, the SoC *family* (see
    _soc_family) is baked into the file name. A new family with a different apb
    map is a pure-data change: drop in data/ntrace/ntrace_<family>_v<N>.json.
    The chosen file's topology_model drives render dispatch (topology itself
    comes from --layout)."""
    row = platform_selection_row(cpu)
    version = (row or {}).get("ntrace")
    if version not in ("v1", "v2"):
        if version == "unsupported":
            sys.exit(f"generate_dts: {cpu} 暂不支持 ntrace")
        sys.exit(f"generate_dts: 无法为 CPU '{cpu}' 确定 ntrace 版本 "
                 f"(检查 data/platforms/platform-selection.csv 的 ntrace 列)")
    return f"ntrace_{_soc_family(platform)}_{version}"


def select_link_pmu(cpu):
    # xl200 link (XuanTie Link 200 family), selected via the `link` column of
    # platform-selection.csv (SOLE SOURCE). All xl200 DTS config lives in the
    # single link/link_xl200.dtsi.in template (mirrors link/link_xl300.dtsi.in);
    # today that is the link PMU node, future xl200 blocks go in the same file.
    # The xl300 link block (PMU + CBQRI) is gated on --bus stlk3 and rendered by
    # render_xl300 from the combined link/link_xl300.dtsi.in template.
    row = platform_selection_row(cpu)
    if (row or {}).get("link") == "xl200":
        return "link/link_xl200.dtsi.in"
    return None


def select_pcu_version(cpu, plat):
    """Effective PCU HSM node version for (cpu, platform): 'v1' / 'v2' or None.

    The `pcu` column of platform-selection.csv is a per-CPU override: 'v1' /
    'v2' wins over the platform JSON; '-' (or no matching row) keeps the
    platform default hsm.pcu.version. Neither present -> None, and the caller
    emits no PCU node (opensbi degrades to plain wfi) — backward compatible.

    NOTE: this is the PCU HSM *node* version (xuantie,pcu / xuantie,pcu-v2),
    distinct from hsm.pcu.boot_release's v1/v2 (the boot hart-release
    generation consumed by the gdbinit renderers)."""
    row = platform_selection_row(cpu)
    ver = (row or {}).get("pcu")
    if ver in ("v1", "v2"):
        return ver
    return plat.get("hsm", {}).get("pcu", {}).get("version")


def detect_gmac_type(cpu, gmac_override):
    # -gmac overrides win; otherwise the default comes from the `gmac` column of
    # platform-selection.csv (SOLE SOURCE).
    if gmac_override == "pro":
        return "pro"
    if gmac_override == "h100":
        return "h100"
    row = platform_selection_row(cpu)
    return (row or {}).get("gmac") or "h100"


def select_gmac(gmac_type):
    return {
        "h100":     "gmac/gmac_h100.dtsi.in",
        "h100_v2":  "gmac/gmac_h100_v2.dtsi.in",
        "pro":      "gmac/gmac_pro.dtsi.in",
    }[gmac_type]


# ---------------------------------------------------------------------------
# Value helpers
# ---------------------------------------------------------------------------

def _hex_parse(v):
    return int(v, 0) if isinstance(v, str) else int(v)


def _split64(val):
    return f"0x{(val >> 32) & 0xFFFFFFFF:x}", f"0x{val & 0xFFFFFFFF:x}"


def _cells(val):
    """Format a 64-bit value as a 2-cell "hi lo" pair for devicetree reg."""
    hi, lo = _split64(_hex_parse(val))
    return f"{hi} {lo}"


def _hex(v):
    return f"0x{_hex_parse(v):x}"


def _unit_addr(v):
    return f"{_hex_parse(v):x}"


def _mmu_type(arch):
    return "riscv,sv32" if arch == "rv32" else "riscv,sv57"


# ---------------------------------------------------------------------------
# Layout helpers
# ---------------------------------------------------------------------------

def expand_layout(layout_str):
    """Normalize -layout input: a 0x-prefixed hex bitmap (e.g. bitstream
    CORE_LAYOUT 0xC3C30000) is expanded to an MSB-first binary string with
    4 bits per hex digit (width preserved, rightmost bit = hart0 — same
    convention as the binary form). Binary strings pass through unchanged.
    Raises ValueError on malformed hex."""
    if layout_str.lower().startswith("0x"):
        digits = layout_str[2:]
        if not digits or not all(c in "0123456789abcdefABCDEF" for c in digits):
            raise ValueError(f"invalid hex layout '{layout_str}'")
        return format(int(digits, 16), f"0{4 * len(digits)}b")
    return layout_str


def parse_layout(layout_str):
    # MSB-first: the leftmost bit is the highest-numbered hart, the rightmost
    # bit is hart0 (matches the legacy update_dts_cpu.sh (decimal>>i)&1 order).
    # Reversing here lets every downstream consumer keep index==hart semantics,
    # so layout_bits[i] / enabled_ids refer to physical core i.
    if not layout_str or not all(c in "01" for c in layout_str):
        return [1]
    return [int(c) for c in reversed(layout_str)]


def cores_to_layout(clusters, cores_num, cores_per_cluster):
    """Build an MSB-first -layout bitstring from a cluster count + a list of
    GLOBAL core indices. Width = clusters * cores_per_cluster; core i is enabled
    iff i in cores_num. The result is MSB-first (leftmost bit = highest hart),
    so it feeds parse_layout / render_gdbinit exactly like a -layout string.
    Equivalent: cores_to_layout(2, [4,5,6,7,12,13,14,15], 8) == '1111000011110000'."""
    width = clusters * cores_per_cluster
    invalid = sorted(c for c in cores_num if c < 0 or c >= width)
    if invalid:
        raise ValueError(
            f"core index {invalid} out of range for {clusters} clusters × "
            f"{cores_per_cluster} cores/cluster (valid 0..{width - 1})")
    enabled = set(cores_num)
    # forward bit k = core k; MSB-first reverses so the leftmost char = core width-1.
    return "".join("1" if (width - 1 - k) in enabled else "0"
                   for k in range(width))


def compute_n_clusters(layout_bits, cores_per_cluster):
    """Number of cluster windows spanned by the layout: ceil(slots / cpc).

    Uses the same 8-window (now cpc-window) rule as render_ntrace_dts._topology
    so the cpu-node/cbqri path and the ntrace path agree on cluster boundaries.
    cores_per_cluster is the platform JSON's value (single source of truth)."""
    return max(1, ceil(len(layout_bits) / cores_per_cluster))


def split_fpga_platform(name):
    """Split '<platform>-<ftype>' into (platform, ftype). The FPGA type is
    part of the platform name (e.g. minimesh-hejian selects the hejian FPGA
    type of the minimesh SoC platform). Only the last '-' is considered, and
    only when the suffixed name has no json of its own and '<platform>.json'
    exists — existing platform names use '_' and carry no FPGA type, so this
    is unambiguous. Returns (name, None) otherwise."""
    if (PLATFORMS_DIR / f"{name}.json").exists():
        return name, None
    if "-" in name:
        base, ftype = name.rsplit("-", 1)
        if (PLATFORMS_DIR / f"{base}.json").exists():
            return base, ftype
    return name, None


def resolve_platform(cpu, platform_override, arch=None):
    default = "xiaohui_v1_rv32" if arch == "rv32" else "xiaohui_v1"
    # 1. explicit override wins (must name an existing platform json; the
    #    FPGA-type suffix form '<platform>-<ftype>' resolves to its base).
    if platform_override:
        base, _ = split_fpga_platform(platform_override)
        if (PLATFORMS_DIR / f"{base}.json").exists():
            return base
    # 2. rv32 is arch-driven and cross-cuts every CPU name pattern (the CSV's
    #    name-family column maps e.g. c907-rv32 -> xiaohui_v1, so the rv32
    #    override must take precedence over the CSV lookup).
    if arch == "rv32":
        cand = "xiaohui_v1_rv32"
    else:
        row = platform_selection_row(cpu)
        cand = (row or {}).get("platform") or default
    # 3. fall back to the arch default if the candidate json is missing.
    if (PLATFORMS_DIR / f"{cand}.json").exists():
        return cand
    return default


def resolve_cpu_platform(cpu_arg, platform_override=None, isa_override=None):
    """Normalize a CPU name and resolve (canonical_cpu, arch, platform).

    Single source of truth shared by generate_dts.py and the gdbinit/socinit
    renderers so the CPU-model → platform auto-selection (xiaohui_v1 /
    xiaohui_v2 / xiaohui_v1_rv32) is identical everywhere. Without this the
    renderers fell back to a hard-coded xiaohui_v1 default, so a v2 CPU (e.g.
    xt-c9501fdvkmt) rendered a v1 gdbinit/socinit while its DTS was v2. Exits
    on an unknown/ambiguous CPU name."""
    cpu_isas = load_cpu_isa_configs()
    cpu = cpu_arg.lower()
    if cpu in CPU_ALIASES:
        cpu = CPU_ALIASES[cpu]
    if cpu not in cpu_isas:
        matches = [k for k in cpu_isas if cpu in k]
        if len(matches) == 1:
            cpu = matches[0]
        elif len(matches) > 1:
            sys.exit(f"CPU '{cpu_arg}' ambiguous, matches: {sorted(matches)}")
        else:
            sys.exit(f"CPU '{cpu_arg}' not found in cpu-isa-mapping")
    isa = isa_override or cpu_isas[cpu]
    arch, _, _ = parse_isa_string(isa)
    return cpu, arch, resolve_platform(cpu, platform_override, arch)


def _deep_merge(base, overlay):
    """Deep-merge overlay into base in place: dicts merge recursively,
    scalars/lists replace, and an explicit null (None) deletes the key —
    the delete form lets an FPGA-type overlay drop base entries (e.g. a
    PCIe RC the FPGA variant does not wire out)."""
    for k, v in overlay.items():
        if v is None:
            base.pop(k, None)
        elif isinstance(v, dict) and isinstance(base.get(k), dict):
            _deep_merge(base[k], v)
        else:
            base[k] = v
    return base


def apply_fpga_type(plat, requested, platform_name):
    """FPGA-type axis, orthogonal to the SoC platform: e.g. NGF (SoC) ships
    on haps / hejian FPGA types whose sysmaps differ. The FPGA type is part
    of the platform name ('minimesh-hejian'); the platform JSON's optional
    `fpga` block declares {"default": <type>, "types": {<type>: <overlay>}};
    the selected type's overlay is deep-merged over the base definition
    (default type = the base definition itself, no overlay), so platforms
    without the block behave exactly as before. The `fpga` key is popped
    before rendering — no template consumes it."""
    fpga = plat.pop("fpga", None)
    if fpga is None:
        if requested:
            sys.exit(f"generate_dts: platform '{platform_name}' declares no "
                     f"fpga block — '{platform_name}-{requested}' is not a "
                     f"valid platform name")
        return plat
    default = fpga.get("default")
    types = fpga.get("types", {})
    ftype = requested or default
    if ftype == default:
        return plat
    if ftype not in types:
        avail = sorted(set([default] + list(types)) - {None})
        sys.exit(f"generate_dts: unknown FPGA type '{ftype}' for platform "
                 f"'{platform_name}' (available: "
                 f"{', '.join(f'{platform_name}-{t}' for t in avail)})")
    _deep_merge(plat, types[ftype])
    # Re-validate the merged view so a bad overlay fails fast like a bad JSON.
    intc._validate_platform(f"{platform_name} (fpga={ftype})", plat)
    print(f"  fpga type '{ftype}' overlay applied (default: {default})")
    return plat


def apply_emu_overlay(plat, enabled, platform_name):
    """Apply an optional EMU overlay after the selected FPGA type."""
    overlay = plat.pop("emu", None)
    if not enabled:
        return plat
    if overlay is None:
        sys.exit(f"generate_dts: platform '{platform_name}' does not support --emu")
    _deep_merge(plat, overlay)
    intc._validate_platform(f"{platform_name} (emu)", plat)
    print("  emu overlay applied")
    return plat


# ---------------------------------------------------------------------------
# Block renderers (each returns a rendered dtsi chunk string)
# ---------------------------------------------------------------------------

def _render_file(name, defines=None, repeats=None):
    text = (TMPL_DIR / name).read_text()
    return dtsi_engine.render(text, defines or {}, repeats or {})


def render_cpu(isa, layout_bits, timebase, intc_kind, bus, link_ip,
               cores_per_cluster):
    arch, isa_base, isa_exts = parse_isa_string(isa)
    # Reconcile AIA extensions in riscv,isa / riscv,isa-extensions with the
    # interrupt controller: aplic auto-fills them, plic removes them.
    isa, isa_exts = _apply_aia_policy(isa, isa_exts, intc_kind)
    # L3 cache + next-level-cache is emitted ONLY on xl300 (--bus stlk3, which
    # is xiaohui_v2/RV64-only). The per-cluster L3 nodes are derived from the
    # platform's link_ip.xl300.l3_cache prototype (one node per physical
    # cluster); non-xl300 RV64 builds carry no L3 and no next-level-cache. The
    # cbqri nodes in the same xl300 block reference these &l3_cache_<i>
    # phandles, so the node count tracks xl300.clusters (physical), not the
    # -layout-derived populated-cluster count.
    xl300 = link_ip.get("xl300", {})
    l3_proto = xl300.get("l3_cache")
    has_l3 = bus == "stlk3" and arch != "rv32" and bool(l3_proto)
    clusters = xl300.get("clusters", 1) if has_l3 else 1
    # Layout bitstring is MSB-first: the leftmost char is the highest hart and
    # the rightmost is core 0 (parse_layout reversed it, so layout_bits[i] maps
    # to physical core i). An asymmetric layout like 1111000011110000 therefore
    # enables cores {4,5,6,7,12,13,14,15}.
    # next-level-cache: core i → l3_cache_<i // cores_per_cluster> (clamped to
    # the last physical cluster).
    cores = [
        {"CORE_ID": i,
         "CORE_STATUS": "okay" if bit else "disabled",
         "CORE_L3": f"l3_cache_{min(i // cores_per_cluster, clusters - 1)}"}
        for i, bit in enumerate(layout_bits)
    ]
    l3cache = []
    if has_l3:
        for i in range(clusters):
            l3cache.append({
                "L3_LABEL": f"l3_cache_{i}",
                "L3_NODE": f"l3-cache-{i}",
                "L3_LEVEL": l3_proto["level"],
                "L3_BLOCK_SIZE": _hex(l3_proto["block-size"]),
                "L3_SETS": _hex(l3_proto["sets"]),
                "L3_SIZE": _hex(l3_proto["size"]),
            })
    defines = {
        "TIMEBASE": timebase,
        "ISA": isa,
        "ISA_BASE": isa_base,
        "ISA_EXT": ", ".join(isa_exts),
        "MMU_TYPE": _mmu_type(arch),
        "HAS_L3": "1" if has_l3 else "",
    }
    return _render_file("cpu/cpu.dtsi.in", defines,
                        {"CORES": cores, "L3CACHE": l3cache})


def _cluster_populated(enabled_ids, cores_per_cluster, i):
    """True when -layout enables any core belonging to cluster i."""
    return any(c // cores_per_cluster == i for c in enabled_ids)


def render_xl300(enabled_ids, arch, bus, link_ip, cores_per_cluster):
    """Combined XuanTie Link 300 block — PMU instances + CBQRI capacity
    controllers — rendered from link/link_xl300.dtsi.in. Selected only by
    --bus stlk3, which is xiaohui_v2-only (RV64): the platform JSON declares a
    single-cluster prototype (link_ip.xl300.pmu / .cbqri) plus `clusters` and
    `cluster_stride`; instance i is derived at base + i*cluster_stride, irq at
    irq_base + i*irq_stride, cache phandle &l3_cache_<i> (prefix lives in the
    template). Only xiaohui_v2 declares
    xl300, so requesting --bus stlk3 on any other platform (v1 family / rv32)
    fails fast. cbqri is likewise v2-only (declared alongside pmu).

    Per-node status: okay iff cluster i is populated by -layout."""
    if bus != "stlk3":
        return ""
    xl300 = link_ip.get("xl300", {})
    pmu = xl300.get("pmu")
    if arch == "rv32" or not pmu:
        sys.exit("generate_dts: --bus stlk3 (xl300) is only supported on "
                 "xiaohui_v2 (rv64); this platform declares no link_ip.xl300")
    # clusters/cluster_stride are schema-required alongside pmu (validated at
    # load), so read them directly rather than defaulting a missing field.
    clusters = xl300["clusters"]
    stride = _hex_parse(xl300["cluster_stride"])
    cbqri = xl300.get("cbqri")  # None on v1 -> no cbqri nodes (v2-only)

    pmu_base = _hex_parse(pmu["base"])
    pmu_instances = []
    for i in range(clusters):
        base = pmu_base + i * stride
        pmu_instances.append({
            "XL300_NODE_NAME": f"xl300_pmu_{i}@{base:x}",
            "XL300_BASE": _cells(base),
            "XL300_SIZE": _cells(pmu["size"]),
            "XL300_IRQ": pmu["irq_base"] + i * pmu["irq_stride"],
            "XL300_IRQ_TYPE": pmu["irq_type"],
            "XL300_STATUS": "okay" if _cluster_populated(enabled_ids, cores_per_cluster, i) else "disabled",
        })

    cbqri_instances = []
    if cbqri:
        cbqri_base = _hex_parse(cbqri["base"])
        for i in range(clusters):
            base = cbqri_base + i * stride
            cbqri_instances.append({
                "CBQRI_INDEX": i,
                "CBQRI_UNIT": f"{base:x}",
                "CBQRI_BASE": _cells(base),
                "CBQRI_SIZE": _cells(cbqri["size"]),
                "CBQRI_RCID": cbqri["rcid"],
                "CBQRI_MCID": cbqri["mcid"],
                "CBQRI_STATUS": "okay" if _cluster_populated(enabled_ids, cores_per_cluster, i) else "disabled",
            })

    return _render_file("link/link_xl300.dtsi.in", {},
                        {"XL300_PMU": pmu_instances, "XL300_CBQRI": cbqri_instances})


def render_intc(plat, platform_name, cpu, core_ids, intc_kind, isa):
    kind = normalize_intc_kind(intc_kind or intc.detect_intc_kind(plat))
    if kind not in ("plic", "aplic"):
        sys.exit(f"generate_dts: unsupported intc '{intc_kind}' — valid: "
                 "plic | aplic | aplic2core | aplic4core | aplic16core")

    n_cores = len(core_ids)
    max_cores = int(plat.get("max_cores", 1))
    if not (1 <= n_cores <= max_cores):
        sys.exit(f"generate_dts: cores {n_cores} out of range for "
                 f"{platform_name} (max_cores={max_cores})")

    if kind == "aplic":
        defines = intc.build_aplic(plat, core_ids)
        intc._apply_mtimer(defines, plat, intc.isa_has_sstc(isa))
        return _render_file("intc/aplic.dtsi.in", defines)

    defines = intc.build_plic(plat, core_ids)
    cores = [{"CORE_ID": i} for i in core_ids]
    return _render_file("intc/plic.dtsi.in", defines, {"CORES": cores})


def render_pmu(pmu_file):
    return _render_file(pmu_file)


# ---------------------------------------------------------------------------
# Fixed-SoC renderers (NGF minimesh/fullmesh: golden-aligned handwritten DTS
# taken over by the generator). They are opt-in via platform JSON keys
# (cpu_block / intc.aplic.mtimer_percore) and render to "" or no-op when the
# keys are absent, so existing platforms are unaffected.
# ---------------------------------------------------------------------------

def render_cpu_fixed(plat, isa, enabled_pairs=None):
    """CPU core nodes driven by an explicit platform cpu_block.cores list.

    Unlike render_cpu, the ISA string is emitted verbatim (no AIA policy) and
    each core's status comes straight from the JSON — including the
    non-standard "skip" that minimesh's build_dtb.sh flips via sed. The
    skeleton template owns the cpus{} wrapper / l2_cache / cpu-map, so this
    renders core nodes only.

    enabled_pairs: --complexes derived [(complex, slot), ...] list. Logical
    harts are renumbered 0..N-1 over the enabled cores (all status "okay");
    the per-complex peripheral addresses shift instead (see
    render_mtimer_percore / render_imsics_block)."""
    cb = plat["cpu_block"]
    if enabled_pairs:
        src = [{"id": i, "status": "okay"} for i in range(len(enabled_pairs))]
    else:
        src = cb["cores"]
    cores = []
    for c in src:
        cid = c["id"]
        cores.append({
            "CORE_ID": cid,
            "CORE_REG": f"0x{cid:02x}",
            "CORE_STATUS": c["status"],
            # Fixed SoC: numa-node-id = logical hart id (bit 包同款)。
            "CORE_NUMA": "1",
            "CORE_NUMA_ID": f"0x{cid:02x}",
        })
    defines = {
        "ISA": isa,
        "MMU_TYPE": cb["mmu_type"],
    }
    return _render_file(cb.get("template", "cpu/cpu_ngf.dtsi.in"),
                        defines, {"CORES": cores})


def parse_complexes(arg, cpc):
    """Parse --complexes into [(complex_id, slot), ...] in listed order.

    Entry syntax: <complex> enables every slot; <complex>:<mask> enables a
    per-slot subset — mask is an MSB-first bit string like --layout (rightmost
    char = slot0), e.g. 8:01 -> slot0 only, 8:10 -> slot1 only."""
    pairs = []
    for ent in arg.replace(" ", "").split(","):
        if not ent:
            continue
        if ":" in ent:
            c, mask = ent.split(":", 1)
            if not mask or not all(b in "01" for b in mask) or len(mask) > cpc:
                sys.exit(f"generate_dts: bad --complexes mask '{ent}' "
                         f"(expect <=%d bits of 0/1, rightmost=slot0)" % cpc)
            slots = [j for j, b in enumerate(reversed(mask)) if b == "1"]
        else:
            c = ent
            slots = list(range(cpc))
        if not c.isdigit():
            sys.exit(f"generate_dts: bad --complexes entry '{ent}'")
        pairs.extend((int(c), j) for j in slots)
    if not pairs:
        sys.exit("generate_dts: --complexes enables no core")
    return pairs


def _complex_addrs(spec, enabled_pairs):
    """Derive per-core addresses from a complex-addressing spec.

    Global complex id decomposes into (die, in-die complex) when the spec
    declares complexes_per_die (+ die_stride, default contiguous =
    complexes_per_die * complex_stride):
      addr = base + die*die_stride + cin*complex_stride + slot*core_stride"""
    base = _hex_parse(spec["base"])
    cstride = _hex_parse(spec["complex_stride"])
    kstride = _hex_parse(spec["core_stride"])
    cpd = spec.get("complexes_per_die")
    dstride = (_hex_parse(spec["die_stride"]) if "die_stride" in spec
               else (cpd * cstride if cpd else None))
    addrs = []
    for c, j in enabled_pairs:
        if cpd:
            die, cin = divmod(c, cpd)
            addrs.append(base + die * dstride + cin * cstride + j * kstride)
        else:
            addrs.append(base + c * cstride + j * kstride)
    return addrs


def _default_pairs(spec):
    """default_complexes expanded to all-slot (complex, slot) pairs."""
    cpc = spec.get("cores_per_complex", 2)
    return [(c, j) for c in spec["default_complexes"] for j in range(cpc)]


def render_mtimer_percore(plat, isa, enabled_pairs=None):
    """Per-core ACLINT mtimer array (intc.aplic.mtimer_percore).

    The single-mtimer path (_apply_mtimer) covers the aplic-without-Sstc HAPS
    case; NGF minimesh/fullmesh instead carry one mtimer node per core at explicit
    per-core addresses with a split reg (mtimecmp window + mtime window).
    reg = <base a_size> <base+a_size b_size> in 2-cell pairs.

    Sstc gating reuses the xiaohui rule (render_intc_dtsi.isa_has_sstc): an ISA
    declaring Sstc uses stimecmp and emits no mtimer nodes, exactly like the
    single-mtimer path; only Sstc-less CPUs get the per-core mtimer array.

    Addresses come from either the literal `addrs` list or a `complex` spec
    (base/complex_stride/core_stride[/complexes_per_die/die_stride]);
    --complexes overrides the enabled set, shifting every mtimer to the
    enabled cores' complex windows."""
    mt = plat.get("intc", {}).get("aplic", {}).get("mtimer_percore")
    if not mt:
        return ""
    if intc.isa_has_sstc(isa):
        return ""
    cplx = mt.get("complex")
    if cplx:
        addrs = _complex_addrs(cplx, enabled_pairs or _default_pairs(cplx))
    elif enabled_pairs:
        sys.exit("generate_dts: --complexes requires mtimer_percore.complex "
                 "(addressing spec) in the platform JSON")
    else:
        addrs = mt["addrs"]
    a_size = _hex_parse(mt["reg_split"][0])
    b_size = _hex_parse(mt["reg_split"][1])
    irq = mt.get("irq", 7)
    labels = mt.get("labels", False)
    rows = []
    for i, addr in enumerate(addrs):
        base = _hex_parse(addr)
        rows.append({
            "MT_LABEL": f"mtimer_c{i}: " if labels else "",
            "MT_UNIT": f"{base:x}",
            "MT_CPU": i,
            "MT_IRQ": irq,
            "MT_REG": (f"<{_cells(base)} {_cells(a_size)} "
                       f"{_cells(base + a_size)} {_cells(b_size)}>"),
        })
    return _render_file(mt.get("template", "mtimer/mtimer_percore.dtsi.in"),
                        {}, {"MTIMERS": rows})


def _port_rc(port, default="rc1"):
    """RC a declared RP INTx APLIC pair belongs to. Ports live in one flat
    list (the -nic pcie<rc>-rp<n> syntax addresses them per RC), so an
    untagged port belongs to the RC the platform enables by default."""
    return port.get("rc", default)


def _extra_rc(extra, default="rc1"):
    """RC a map_extra interrupt-map row belongs to (same default as _port_rc)."""
    return extra.get("rc", default)


def _default_port_rc(pcie):
    """RC an untagged intx port / map_extra row belongs to: the platform's
    default-enabled host when declared, else its first declared host."""
    enabled = _pcie_enable_set(pcie)
    for rc in ("rc0", "rc1"):
        if rc in enabled and pcie.get(rc):
            return rc
    for rc in ("rc0", "rc1"):
        if pcie.get(rc):
            return rc
    return "rc1"


def _pcie_enable_set(pcie):
    """net.pcie.enable -> set of RC names. Accepts the historical single
    string ("rc1" | "rc0" | "disabled") and an explicit list (["rc0", "rc1"])
    for boards whose bitstream powers both hosts."""
    enable = pcie.get("enable", "rc1")
    items = enable if isinstance(enable, list) else [enable]
    return {item for item in items if item.startswith("rc")}


def parse_net_selection(plat, nic, iommu_value, gmac_type=None, intc_kind=None):
    """Resolve NIC modes, the enabled PCIe RCs, RP INTx ports and IOMMUs.

    nic tokens: pcie | pcie<rc> | pcie<rc>-rp<n> | gmac, comma-combinable.
    Plain `pcie` keeps the platform's declared default (net.pcie.enable);
    naming a host (pcie0 / pcie1, combinable) overrides that default and
    puts exactly the named RC(s) in okay state, so a disabled-but-declared
    host can be brought up without touching the platform JSON. gmac selects
    the SoC peripheral-bus gmac NIC (fixed-SoC/NGF: its interrupt goes
    through the RCiEP APLIC to IMSIC_S, i.e. AIA), and pcie+gmac coexist on
    dual-NIC boards. --gmac is unrelated: it only picks the xiaohui gmac
    controller template (h100 | pro).
    iommu_value: None (off) | "true"/"all" (every declared instance) |
    comma-separated declared instance names (e.g. "die0-cu1"). Only instances
    declared in the platform's net.iommu.instances can be enabled.
    """
    if nic is not None and not (plat.get("cpu_block")
                                or plat.get("net", {}).get("pcie")):
        sys.exit("generate_dts: --nic requires a platform declaring "
                 "cpu_block (fixed-SoC) or net.pcie")
    if gmac_type and plat.get("cpu_block"):
        # update_dts_cpu.sh 无条件透传缺省 --gmac 类型, 固定 SoC 平台不据此
        # 选网卡: 忽略并提示改用 --nic gmac。
        print(f"note: --gmac {gmac_type} only selects the xiaohui gmac "
              "controller template and is ignored on this platform; "
              "select the gmac NIC with --nic gmac", file=sys.stderr)
    tokens = [item.strip() for item in nic.split(",")] if nic is not None else ["pcie"]
    nics, rps = [], set()
    rc_sel = set()
    for token in tokens:
        if token == "gmac":
            if "gmac" not in nics:
                nics.append("gmac")
            continue
        m = re.fullmatch(r"pcie([01])?(?:-rp([0-3]))?", token)
        if not m:
            sys.exit("generate_dts: --nic accepts pcie | pcie<rc> | "
                     "pcie<rc>-rp<n> | gmac or a comma-separated "
                     f"combination (got '{nic}')")
        if m.group(1) is not None:
            rc_sel.add(f"rc{m.group(1)}")
        if m.group(2) is not None:
            rps.add(int(m.group(2)))
        if "pcie" not in nics:
            nics.append("pcie")

    net = plat.get("net", {})
    net_modes = net.get("modes")
    if nic is not None:
        if not net_modes:
            sys.exit("generate_dts: this platform offers no network devices "
                     "(no net.modes declared)")
        missing = [mode for mode in nics if mode not in net_modes]
        if missing:
            sys.exit(f"generate_dts: --nic {','.join(missing)} not "
                     f"offered by this platform (net.modes = {net_modes})")
    pcie = net.get("pcie", {})
    declared = {rc for rc in ("rc0", "rc1") if pcie.get(rc)}
    if rc_sel - declared:
        sys.exit(f"generate_dts: --nic selects "
                 f"{','.join(sorted(rc_sel - declared))} but this platform "
                 f"declares {','.join(sorted(declared)) or 'no'} PCIe RC "
                 "(net.pcie.rc<id>)")
    enabled_rcs = rc_sel or (_pcie_enable_set(pcie) & declared)
    default_rc = _default_port_rc(pcie)
    intx = pcie.get("intx", {})
    port_ids = {port["id"] for port in intx.get("ports", [])
                if _port_rc(port, default_rc) in enabled_rcs}
    if rps and (not rps.issubset(port_ids) or not port_ids):
        sys.exit("generate_dts: selected PCIe RP INTx configuration is not "
                 "offered by this platform")
    if rps and intc_kind != "aplic":
        sys.exit("generate_dts: RP INTx APLIC requires -intc aplic "
                 "(RP APLIC MSI hangs off IMSIC)")

    iommu_cfg = net.get("iommu") or {}
    instances = iommu_cfg.get("instances", {})
    cu_tops = iommu_cfg.get("cu_tops", {})
    for name, inst in instances.items():
        if inst["cu_top"] not in cu_tops:
            sys.exit(f"generate_dts: iommu instance '{name}' references "
                     f"undeclared cu_top '{inst['cu_top']}' "
                     f"(declared: {','.join(cu_tops)})")
    if iommu_value is None:
        enabled_instances = []
    else:
        if not plat.get("cpu_block") or not instances:
            sys.exit("generate_dts: --iommu is not offered by this platform")
        if iommu_value in ("true", "all"):
            enabled_instances = list(instances)
        else:
            names = [name.strip() for name in iommu_value.split(",")]
            unknown = [name for name in names if not name or name not in instances]
            if unknown:
                sys.exit(f"generate_dts: --iommu unknown instance(s) "
                         f"{','.join(unknown)} (declared: {','.join(instances)})")
            enabled_instances = list(dict.fromkeys(names))
        if "pcie" not in nics:
            sys.exit("generate_dts: --iommu requires PCIe mode")
        for name in enabled_instances:
            if instances[name]["rc"] not in enabled_rcs:
                sys.exit(f"generate_dts: --iommu instance '{name}' requires its "
                         f"PCIe host ({instances[name]['rc']}) to be enabled")
    return nics, sorted(rps), enabled_instances, sorted(enabled_rcs)


def _mmio_defines(prefix, node):
    """Platform-owned MMIO node -> unit-address/reg/label template defines."""
    base = _hex_parse(node["base"])
    return {
        f"{prefix}_LABEL": node["label"],
        f"{prefix}_UNIT": _unit_addr(base),
        f"{prefix}_REG": f"{_cells(base)} {_cells(node['size'])}",
    }


def render_net_blocks(plat, nics, pcie_rps, iommu_instances, enabled_rcs):
    """All physical bases/labels/NUMA/RID windows come from the merged platform
    JSON (SoC baseline + FPGA-type overlay); templates only carry node
    structure, binding constants and enable-state placeholders.

    `enabled_rcs` is the resolved set of PCIe hosts to put in okay state
    (net.pcie.enable, or the -nic pcie<rc> override); every *declared* RC
    still renders, the rest stay disabled."""
    net = plat.get("net")
    if not net:
        return "", ""
    aplic_rc1 = net.get("aplic_rc1", {})
    intx = net.get("pcie", {}).get("intx")
    rciep_disabled = "" if "gmac" in nics else "1"

    def rciep_defines(src, mk, sk):
        d = {"RCIEP_DISABLED": rciep_disabled}
        d.update(_mmio_defines("RCIEP_M", src[mk]))
        d.update(_mmio_defines("RCIEP_S", src[sk]))
        d["RCIEP_M_SOURCES"] = src[mk].get("num_sources", "0x60")
        d["RCIEP_S_SOURCES"] = src[sk].get("num_sources", "0x60")
        d["RCIEP_DELEGATE_RANGE"] = src[mk].get("delegate_range", "0x01 0x60")
        return d

    tmpl_rc1 = net.get("aplic_rc1_template", "net/aplic_rciep_pair.dtsi.in")
    if not tmpl_rc1:
        # 平台以空串声明置空 ${APLIC_RC1_BLOCK} (hejian: RCiEP 对由共享
        # 模板与 RP 对一起渲染进 ${NET_BLOCK})。
        rc1 = ""
    elif intx and "rciep_m" in intx and "rciep_s" in intx:
        rc1 = _render_file(tmpl_rc1, {"RCIEP_DISABLED": rciep_disabled})
    elif aplic_rc1:
        rc1 = _render_file(tmpl_rc1, rciep_defines(aplic_rc1, "m", "s"))
    else:
        rc1 = ""
    pcie = net.get("pcie", {})
    groups = []
    if intx:
        if "pcie" in nics:
            port_rows = []
            for port in intx["ports"]:
                # Declared RP APLIC pairs always render: a map_keep RC has a
                # permanent interrupt-map row pointing at them, so dropping
                # them on an -nic selection would leave a dangling phandle.
                # Their status still follows -nic pcie<rc>-rp<n> only.
                row = {"RP_STATUS": "okay" if port["id"] in pcie_rps else "disabled"}
                row.update(_mmio_defines("RP_M", port["m"]))
                row.update(_mmio_defines("RP_S", port["s"]))
                port_rows.append(row)
            groups.append(_render_file(intx["template"], {}, {"PORTS": port_rows}))
        if ("pcie" in nics or "gmac" in nics) \
                and "rciep_m" in intx and "rciep_s" in intx:
            groups.append(_render_file("net/aplic_rciep_pair.dtsi.in",
                                       rciep_defines(intx, "rciep_m", "rciep_s")))
    if "pcie" in nics:
        tmpl = pcie.get("template", "net/pcie_rc.dtsi.in")
        iommu = net.get("iommu")
        if iommu:
            cu_tops = iommu["cu_tops"]
            rendered_tops = set()
            for inst in iommu["instances"].values():
                top = inst["cu_top"]
                if top in rendered_tops:
                    continue
                rendered_tops.add(top)
                pair = cu_tops[top]
                cu_defines = {
                    "CU_TOP_STATUS": ("okay" if any(
                        iommu["instances"][n]["cu_top"] == top
                        for n in iommu_instances) else "disabled"),
                    "CU_TOP_M_SOURCES": pair["m"].get("num_sources", "0x60"),
                    "CU_TOP_S_SOURCES": pair["s"].get("num_sources", "0x60"),
                    "CU_TOP_DELEGATE_RANGE": pair["m"].get("delegate_range",
                                                           "0x0a 0x29"),
                }
                cu_defines.update(_mmio_defines("CU_TOP_M", pair["m"]))
                cu_defines.update(_mmio_defines("CU_TOP_S", pair["s"]))
                groups.append(_render_file(iommu["cu_top_template"], cu_defines))
            inst_rows = []
            for name, inst in iommu["instances"].items():
                row = {
                    "IOMMU_NUMA": inst["numa_node_id"],
                    "IOMMU_INTERRUPTS": inst["interrupts"],
                    "IOMMU_STATUS": "okay" if name in iommu_instances else "disabled",
                    "CU_TOP_S_LABEL": cu_tops[inst["cu_top"]]["s"]["label"],
                }
                row.update(_mmio_defines("IOMMU", inst))
                inst_rows.append(row)
            groups.append(_render_file(iommu["instance_template"], {},
                                       {"INSTANCES": inst_rows}))
        # map_keep: the RC routing properties are rendered from the platform's
        # *declaration* (every intx.ports RP + every intx.map_extra row, and
        # every iommu.instances entry of this RC) instead of the -nic/--iommu
        # selection, via net/pcie_rc_keep_map.dtsi.in. The referenced RP /
        # RCiEP / CU-top / IOMMU nodes keep the same status rules as before, so
        # enabling one is a status flip in an already-routed tree.
        keep_map = bool(pcie.get("map_keep"))
        if keep_map and tmpl == "net/pcie_rc.dtsi.in":
            tmpl = "net/pcie_rc_keep_map.dtsi.in"
        if keep_map and not intx:
            sys.exit("generate_dts: net.pcie.map_keep requires net.pcie.intx "
                     "(the kept interrupt-map needs declared RP APLICs)")
        default_rc = _default_port_rc(pcie)
        parts = []
        for rc in ("rc0", "rc1"):
            spec = pcie.get(rc)
            if not spec:
                continue
            intx_rows = []
            iommu_rows = []
            if keep_map:
                # map_keep: the rows come from *this RC's* declaration, never
                # from the -nic/--iommu selection, so a host that declares no
                # RP INTx APLIC of its own (hejian's MSI-only rc0) simply gets
                # no map, and enabling a different host cannot drop them.
                for port in intx["ports"]:
                    if _port_rc(port, default_rc) != rc:
                        continue
                    for pin in range(1, 5):
                        intx_rows.append({
                            "INTX_MAP_ADDR": f"0x{port['device'] << 11:04x}",
                            "INTX_MAP_PIN": f"0x{pin}",
                            "INTX_MAP_PARENT": port["s"]["label"],
                            "INTX_MAP_IRQ": str(pin),
                            "INTX_MAP_TYPE": "0x4",
                        })
                for extra in intx.get("map_extra", []):
                    if _extra_rc(extra, default_rc) != rc:
                        continue
                    target = intx[extra["target"]]
                    intx_rows.append({
                        "INTX_MAP_ADDR": f"0x{extra['device'] << 11:04x}",
                        "INTX_MAP_PIN": f"0x{extra['pin']}",
                        "INTX_MAP_PARENT": target["label"],
                        "INTX_MAP_IRQ": str(extra["irq"]),
                        "INTX_MAP_TYPE": extra.get("type", "0x4"),
                    })
                for name, inst in (iommu or {}).get("instances", {}).items():
                    if inst["rc"] == rc:
                        iommu_rows.append({
                            "IOMMU_MAP_PARENT": inst["label"],
                            "IOMMU_MAP_RID_BASE": inst["rid_base"],
                            "IOMMU_MAP_RID_COUNT": inst["rid_count"],
                        })
                for rows, sep_key in ((intx_rows, "INTX_MAP_SEP"),
                                      (iommu_rows, "IOMMU_MAP_SEP")):
                    for i, row in enumerate(rows):
                        row[sep_key] = "," if i < len(rows) - 1 else ""
            intx_map = ""
            if intx and rc in enabled_rcs and pcie_rps:
                entries = [
                    f"<0x{port['device'] << 11:04x} 0x0 0x0 0x{pin:x} "
                    f"&{port['s']['label']} {pin} 0x4>"
                    for port in intx["ports"]
                    if port["id"] in pcie_rps
                    and _port_rc(port, default_rc) == rc
                    for pin in range(1, 5)
                ]
                intx_map = ("interrupt-map = " + ",\n\t\t\t\t".join(entries) + ";\n"
                            "\t\t\tinterrupt-map-mask = <0xf800 0x00 0x00 0x07>;\n"
                            "\t\t\t#interrupt-cells = <0x01>;")
            iommu_map = ""
            if iommu:
                map_entries = [
                    f"<0x00 &{iommu['instances'][name]['label']} "
                    f"{iommu['instances'][name]['rid_base']} "
                    f"{iommu['instances'][name]['rid_count']}>"
                    for name in iommu_instances
                    if iommu["instances"][name]["rc"] == rc
                ]
                if map_entries:
                    iommu_map = "iommu-map = " + ", ".join(map_entries) + ";"
            base = _hex_parse(spec["base"])
            defines = {
                "PCIE_LABEL": f"pcie_{rc}",
                "PCIE_NODE": spec.get("node_name", "pcie"),
                "PCIE_UNIT": f"{base:x}",
                "PCIE_DOMAIN": f"0x{int(spec['domain']):02x}",
                "PCIE_STATUS": "okay" if rc in enabled_rcs else "disabled",
                "PCIE_REG": f"{_cells(base)} {_cells(spec['size'])}",
                "PCIE_RANGES": spec["ranges"],
                "PCIE_INTX_MAP": intx_map,
                "PCIE_IOMMU_MAP": iommu_map,
                "PCIE_INTX_MAP_KEEP": "1" if intx_rows else "",
                "PCIE_IOMMU_MAP_KEEP": "1" if iommu_rows else "",
            }
            if "numa_node_id" in spec:
                # hex, so numa_node_id 0 stays truthy for ##IF## (a bare "0"
                # would suppress the whole block and drop the property).
                defines["PCIE_NUMA"] = f"0x{int(spec['numa_node_id']):02x}"
            if not intx:
                defines["PCIE_INTX_STATIC"] = "1"
            parts.append(_render_file(tmpl, defines, {
                "PCIE_INTX_ROWS": intx_rows, "PCIE_IOMMU_ROWS": iommu_rows}))
        groups.append("\n".join(parts))
    if "gmac" in nics:
        g = net["gmac"]
        base = _hex_parse(g["base"])
        # RCiEP 对可能声明在 intx (随 RP 对渲染进 ${NET_BLOCK}) 或 aplic_rc1
        # (渲染进 ${APLIC_RC1_BLOCK}), 两处取其一。
        rciep_s = (intx or {}).get("rciep_s") or aplic_rc1.get("s")
        if not rciep_s:
            sys.exit("generate_dts: -gmac requires net.pcie.intx.rciep_s or "
                     "net.aplic_rc1.s (label/base/size) to declare the RCiEP "
                     "APLIC that carries the gmac interrupt")
        irq_parent = rciep_s["label"]
        groups.append(_render_file(g.get("template", "net/gmac.dtsi.in"), {
            "GMAC_UNIT_ADDR": f"{base:x}",
            "GMAC_BASE": _cells(base),
            "GMAC_SIZE": _cells(g["size"]),
            "GMAC_IRQ": g["irq"],
            "GMAC_IRQ_TYPE": g["irq_type"],
            "GMAC_IRQ_PARENT": irq_parent,
        }))
    return rc1, "\n\n".join(groups)


def render_imsics_block(plat, core_count):
    """Render fixed-SoC IMSIC nodes directly into the generated DTS.

    IMSIC windows are indexed by logical harts, not by the selected physical
    complex IDs. `core_count` therefore follows the same compact cpu0..cpuN-1
    numbering emitted by render_cpu_fixed(). The physical topology remains
    relevant to per-core devices such as mtimer and is handled separately.
    """
    ap = plat.get("intc", {}).get("aplic", {})
    if not ap or not core_count:
        return ""

    m_base = _hex_parse(ap["imsics_m"]["base"])
    m_per = _hex_parse(ap["imsics_m"]["size_per_core"])
    s_base = _hex_parse(ap["imsics_s"]["base"])
    s_per = _hex_parse(ap["imsics_s"]["size_per_core"])
    defines = {
        "IMSICS_M_UNIT": f"{m_base:x}",
        "IMSICS_M_REG": f"{_cells(m_base)} {_cells(m_per * core_count)}",
        "IMSICS_M_EXT": " ".join(
            f"&cpu{i}_intc 11" for i in range(core_count)),
        "IMSICS_S_UNIT": f"{s_base:x}",
        "IMSICS_S_REG": f"{_cells(s_base)} {_cells(s_per * core_count)}",
        "IMSICS_S_EXT": " ".join(
            f"&cpu{i}_intc 9" for i in range(core_count)),
        "IMSICS_NUM_IDS": _hex(ap["imsics_s"]["num_ids"]),
        "IMSICS_GUEST_BITS": _hex(ap["imsics_s"]["guest_index_bits"]),
    }
    return _render_file("imsics/imsics_inc.dtsi.in", defines)


def render_pmc(pmc, pmc_enabled, base_override=None):
    if not pmc:
        if pmc_enabled:
            sys.exit("generate_dts: --pmc requires hsm.pmc in the platform "
                     "JSON, which this platform does not declare")
        return ""
    base = _hex_parse(base_override) if base_override else _hex_parse(pmc["base"])
    return _render_file("pmc/pmc_v1.dtsi.in", {
        "PMC_UNIT_ADDR": f"{base:x}",
        "PMC_BASE": _cells(base),
        "PMC_SIZE": _cells(pmc["size"]),
        "PMC_STATUS": "okay" if pmc_enabled else "disabled",
    })


# NGF structural constant: 2 cores share one complex, and pcu-v2 hardwires the
# group size to 2 (opensbi xuantie_pcu.c: group[hartid & ~1], XT_GROUP(hartid)
# = hartid >> 1), so this is not a platform-JSON knob.
_CORES_PER_COMPLEX = 2


def _pcu_base(nodes, hartid):
    """pcu_base = complex_base + (hartid/2)*complex_stride +
    core{0|1}_pcu_offset (complex = 2 cores; the position inside the complex
    selects the core0/core1 offset; pcu-v2 group masters are even hartids and
    thus always use core0_pcu_offset)."""
    complex_idx = hartid // _CORES_PER_COMPLEX
    off = nodes[f"core{hartid % _CORES_PER_COMPLEX}_pcu_offset"]
    return (_hex_parse(nodes["complex_base"])
            + complex_idx * _hex_parse(nodes["complex_stride"])
            + _hex_parse(off))


def render_pcu(pcu_version, nodes, enabled_ids, max_cores):
    """PCU HSM block (xuantie,pcu / xuantie,pcu-v2), layout-driven.

    version missing or hsm.pcu.nodes unregistered -> "" (no PCU node; opensbi
    degrades to plain wfi) — backward compatible. v1: one child per active
    hart. v2: adjacent active pairs form a group (even-aligned master = the
    smaller hartid; a hart whose buddy hart^1 is not active is a layout error),
    one child per group from the master's core0 offset. hartids are
    cross-checked against the platform's max_cores and for uniqueness."""
    if not pcu_version or not nodes:
        return ""
    seen = set()
    for hart in enabled_ids:
        if hart in seen:
            sys.exit(f"generate_dts: duplicate hartid {hart} in the layout")
        seen.add(hart)
        if hart >= max_cores:
            sys.exit(f"generate_dts: PCU hartid {hart} out of range "
                     f"(platform max_cores = {max_cores})")
    entries = []  # (hartid, pcu_base)
    if pcu_version == "v1":
        for hart in enabled_ids:
            entries.append((hart, _pcu_base(nodes, hart)))
    elif pcu_version == "v2":
        active = set(enabled_ids)
        for hart in enabled_ids:
            buddy = hart ^ 1
            if hart & 1:
                # odd hart joins its even master's group node; a complete
                # pair is validated by the master below, so the odd hart
                # only reports a missing master.
                if buddy in active:
                    continue
                sys.exit(f"generate_dts: pcu-v2 layout error: hart {hart} is "
                         f"active but its group master hart {buddy} is not "
                         "(pcu-v2 groups adjacent core pairs; the group "
                         "master must be even-aligned)")
            if buddy not in active:
                sys.exit(f"generate_dts: pcu-v2 layout error: hart {hart} is "
                         f"active but its pair buddy hart {buddy} is not "
                         "(pcu-v2 groups adjacent core pairs; the group "
                         "master must be even-aligned)")
            entries.append((hart, _pcu_base(nodes, hart)))
    else:
        sys.exit(f"generate_dts: unknown PCU HSM version '{pcu_version}' "
                 "(expected v1 or v2)")
    # Node-count contract: v1 one child per active core, v2 one per group.
    expect = len(enabled_ids) if pcu_version == "v1" else len(enabled_ids) // 2
    if len(entries) != expect:
        sys.exit(f"generate_dts: internal PCU node-count mismatch: "
                 f"{len(entries)} entries, expected {expect}")
    if not entries:
        return ""
    repeats = [{
        "PCU_HARTID": hart,
        "PCU_GROUP_ID": hart // _CORES_PER_COMPLEX,
        "PCU_UNIT_ADDR": _unit_addr(base),
        "PCU_BASE": _cells(base),
        "PCU_SIZE": _cells(nodes["pcu_size"]),
    } for hart, base in entries]
    tmpl = "pcu/pcu.dtsi.in" if pcu_version == "v1" else "pcu/pcu-v2.dtsi.in"
    return _render_file(tmpl, {}, {"PCU_CORES": repeats})


def render_gmac(gmac_file, peripherals):
    base = _hex_parse(peripherals["gmac"]["base"])
    return _render_file(gmac_file, {
        "GMAC_UNIT_ADDR": f"{base:x}",
        "GMAC_BASE": _cells(base),
        "GMAC_SIZE": _cells(peripherals["gmac"]["size"]),
    })


def render_link_pmu(link_pmu_file, link_mode, link_ip):
    """xl200 link PMU only (xl300 moved to render_xl300). Instances are enabled
    when the SoC-link bus is active (stlk/stlk2)."""
    if not link_pmu_file:
        return ""
    enabled = link_mode in ("stlk", "stlk2")
    instances = []
    for inst in link_ip.get("xl200", {}).get("pmu", []):
        base = _hex_parse(inst["base"])
        size = inst["size"]  # schema-required on pmuEntry
        instances.append({
            "XL200_NODE_NAME": f"xl200_pmu_{inst['index']}@{base:x}",
            "XL200_BASE": _cells(base),
            "XL200_SIZE": _cells(size),
            "XL200_IRQ": inst["irq"],
            "XL200_IRQ_TYPE": inst["irq_type"],
            "XL200_STATUS": "okay" if enabled else "disabled",
        })
    return _render_file(link_pmu_file, {}, {"XL200": instances})


def render_ntrace(enable, cpu, platform, layout_bits, sba, trace_env,
                  cores_per_cluster, config_override=None):
    """Render ntrace via the pure-Python template renderer.

    enable=False -> ("", ""). Otherwise the trace-constants file is
    config_override when given (a data/ntrace/<stem>.json name, forced
    regardless of CPU — bypasses the CSV and its fail-fast; this is how an
    apb/base-address variant is applied manually: drop in a new json and pass
    its stem), else auto-selected from (CPU, platform) by select_trace_config
    (SoC-aware: builds ntrace_<family>_v<version>.json from the CSV version +
    the platform's SoC family; fail-fast on unsupported/unknown). The chosen json supplies the hardware
    constants and declares its topology_model (which drives render dispatch);
    the encoder/funnel/sink *topology* is always derived from layout_bits via
    the 8-window rule (cluster = pos//8). trace_env supplies the runtime
    encoder/timestamp knobs + sink working mode (empty values fall back to
    render_ntrace_dts' defaults). Returns (ntrace_memory_str, ntrace_str)."""
    if not enable:
        return "", ""
    config = config_override or select_trace_config(cpu, platform)
    return render_ntrace_dts.render(config, layout_bits, sba=sba,
                                    trace_env=trace_env,
                                    cores_per_cluster=cores_per_cluster)


# ---------------------------------------------------------------------------
# Top-level context
# ---------------------------------------------------------------------------

def build_memory_defines(memory, memsize):
    if memsize:
        n = int(memsize.rstrip("gG"))
        size_bytes = n * (1024 ** 3)
    else:
        size_bytes = _hex_parse(memory["size"])

    base = _hex_parse(memory["base"])
    return {
        "MEMORY_UNIT_ADDR": f"{base:x}",
        "MEMORY_BASE": _cells(base),
        "MEMORY_SIZE": _cells(size_bytes),
    }


def build_serial_defines(peripherals):
    s = peripherals["serial"]
    base_int = _hex_parse(s["base"])
    return {
        "SERIAL_UNIT_ADDR": f"{base_int:x}",
        "SERIAL_BASE": _cells(base_int),
        "SERIAL_SIZE": _cells(s["size"]),
        "SERIAL_IRQ": s["irq"],
        "SERIAL_IRQ_TYPE": s["irq_type"],
        "SERIAL_CLOCK_FREQ": s["clock_frequency"],
        "SERIAL_CURRENT_SPEED": s.get("current_speed", 9600),
    }


def _split_cells(value):
    """Split a single hex address into the DTS `<high low>` 32-bit cell pair
    that linux,initrd-* expects. e.g. "0x84000000" -> "0x0 0x84000000"."""
    v = value if isinstance(value, int) else int(value, 0)
    return f"0x{v >> 32:x} 0x{v & 0xffffffff:x}"


def build_chosen_defines(plat, arch, optee, sv=None):
    ch = plat["chosen"]
    bootargs = ch["bootargs"]
    initrd_s = ch["initrd"]["start"]
    initrd_e = ch["initrd"]["end"]

    # -sv: append the paging-level kernel arg (legacy update_dts_mmu).
    if sv == "39":
        bootargs = bootargs.replace("norandmaps", "norandmaps no4lvl")
    elif sv == "48":
        bootargs = bootargs.replace("norandmaps", "norandmaps no5lvl")
    # sv57 / None: no paging-level arg

    if optee and arch != "rv32":
        # OP-TEE initrd-start = where the rootfs image is loaded; keep it a
        # single source of truth (images_layout.optee.rootfs_addr) instead of a
        # hardcoded literal. OP-TEE is xiaohui_v1-only, so the block must exist.
        optee_layout = plat.get("images_layout", {}).get("optee")
        if not optee_layout:
            sys.exit("generate_dts: --optee requires images_layout.optee in the "
                     "platform JSON (OP-TEE is xiaohui_v1-only)")
        initrd_s = optee_layout["rootfs_addr"]
    return {
        "BOOTARGS": bootargs,
        "INITRD_START": _split_cells(initrd_s),
        "INITRD_END": _split_cells(initrd_e),
        "STDOUT_PATH": ch["stdout_path"],
    }


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    ap = argparse.ArgumentParser(
        description="Generate DTS for XuanTie FPGA verification")
    ap.add_argument("--cpu", required=True, help="CPU model (e.g. c930, c908)")
    ap.add_argument("--layout", default="1",
                    help="Core layout bitmap, MSB-first (leftmost bit = highest "
                         "hart, rightmost = hart0). E.g. 1111000011110000 "
                         "enables cores 4-7,12-15. A 0x-prefixed hex bitmap "
                         "(e.g. 0xC3C30000) is accepted and expanded 4 bits "
                         "per hex digit. On fixed-SoC platforms it is "
                         "converted to a complex/slot set (bit i = core i), "
                         "mutually exclusive with --complexes.")
    ap.add_argument("--clusters", type=int, default=None,
                    help="Cluster count; with --cores-num builds the layout "
                         "(width = clusters × cores_per_cluster). When omitted "
                         "it is derived from the largest --cores-num index.")
    ap.add_argument("--cores-num", default=None,
                    help="Comma-separated GLOBAL core indices to enable, e.g. "
                         "4,5,6,7,12,13,14,15. Equivalent alternative to "
                         "--layout; when given it overrides --layout.")
    ap.add_argument("--isa", default=None,
                    help="ISA string (default: from cpu-isa-configs)")
    ap.add_argument("--timer", type=int, default=25000000,
                    help="Timebase frequency (default: 25000000)")
    ap.add_argument("--sv", default=None, help="MMU paging level: 39 | 48 | 57")
    ap.add_argument("--bus", default="none",
                    help="Link mode: ncore, stlk, stlk2, stlk3, none")
    ap.add_argument("--intc", default=None, help="Override: plic | aplic")
    ap.add_argument("--gmac", default=None,
                    help="xiaohui gmac controller type: h100 | pro (selects "
                         "the gmac dtsi template). Not a NIC switch: on "
                         "fixed-SoC (NGF) platforms it is ignored and the "
                         "gmac NIC is selected by --nic gmac.")
    ap.add_argument("--ntrace", nargs="?", const="true", default="false",
                    help="N-Trace: false (off) | true (on, config auto-selected "
                         "from --cpu/platform via the CSV as "
                         "ntrace_<family>_v<version>) | <name> (on, use "
                         "data/ntrace/<name>.json directly, bypassing the CSV — "
                         "e.g. ntrace_xiaohui_v1 / ntrace_xiaohui_v2, or a new "
                         "apb/base-address variant). Topology is always derived "
                         "from --layout (8-window).")
    ap.add_argument("--trace-sba", action="store_true",
                    help="v2 sink system-bus-access addressing")
    ap.add_argument("--memsize", default=None, help="Memory size (e.g. 1G, 2G)")
    ap.add_argument("--optee", action="store_true", help="OP-TEE mode")
    ap.add_argument("--emu", action="store_true",
                    help="Apply the selected platform's EMU timing/UART, disabled-PCIe, and no-GMAC overlay")
    ap.add_argument("--pmc", nargs="?", const="true", default="false",
                    help="PMC: false (off) | true (on, reg base from platform "
                         "JSON pmc.base) | <base-addr> (on, override reg base, "
                         "e.g. 0x26900000)")
    ap.add_argument("--platform", default=None,
                    help="Platform name (xiaohui_v1, xiaohui_v2, …). On "
                         "platforms declaring an `fpga` block the FPGA type "
                         "is part of the name: '<platform>-<ftype>' (e.g. "
                         "minimesh-hejian); the bare name selects the "
                         "default type (haps).")
    ap.add_argument("--output", default=str(FPGA_DIR / ".hw.dts"),
                    help="Output DTS path")
    ap.add_argument("--complexes", default=None,
                    help="Enabled complexes for fixed-SoC platforms, comma-"
                         "separated global complex ids (die = id // "
                         "complexes_per_die), each optionally with a per-slot "
                         "mask: 8,11:01,12:10,15 (mask rightmost bit = "
                         "slot0). Logical harts are renumbered 0..N-1 with "
                         "status okay; per-complex peripherals (mtimer) "
                         "shift to the enabled complexes' address windows and "
                         "the inline IMSIC nodes scale to the selected core "
                         "count. Requires a complex addressing spec in the "
                         "platform JSON (mtimer_percore.complex).")
    ap.add_argument("--nic", default=None,
                    help="Network selection: pcie (default) | pcie<rc> | "
                         "pcie<rc>-rp<n> (e.g. pcie1-rp2) | gmac, "
                         "comma-combinable (pcie,gmac = dual-NIC coexist). "
                         "Plain pcie keeps the platform's default host "
                         "(net.pcie.enable); naming a host (pcie0 / pcie1 / "
                         "pcie0,pcie1) overrides that and puts exactly the "
                         "named RC(s) in okay state, every other declared RC "
                         "still rendering disabled. RP "
                         "selectors enable only the corresponding INTx APLIC "
                         "pairs, not physical PCIe links; plain pcie leaves "
                         "RP/RCiEP disabled. gmac = the SoC peripheral-bus "
                         "gmac NIC, whose interrupt goes through the RCiEP "
                         "APLIC to IMSIC_S (AIA), enabling that pair.")
    ap.add_argument("--iommu", nargs="?", const="all", default=None,
                    help="Enable IOMMU instances declared by the platform: "
                         "bare --iommu or --iommu all|true enables every "
                         "declared instance; --iommu die0-cu1[,...] enables "
                         "named instances only; omit to disable all. Never "
                         "enables undeclared instances or RP INTx.")
    args = ap.parse_args()

    # --layout 接受 0x 十六进制位图(如 CORE_LAYOUT 0xC3C30000), 统一展成二进制
    try:
        args.layout = expand_layout(args.layout)
    except ValueError as e:
        sys.exit(f"generate_dts: {e}")

    cpu_isas = load_cpu_isa_configs()

    cpu = args.cpu.lower()
    if cpu in CPU_ALIASES:
        cpu = CPU_ALIASES[cpu]
    if cpu not in cpu_isas:
        matches = [k for k in cpu_isas if cpu in k]
        if len(matches) == 1:
            cpu = matches[0]
            print(f"  resolved '{args.cpu}' → '{cpu}'")
        elif len(matches) > 1:
            print(f"ERROR: CPU '{cpu}' ambiguous. Matches:", file=sys.stderr)
            for k in sorted(matches):
                print(f"  {k}", file=sys.stderr)
            sys.exit(1)
        else:
            print(f"ERROR: CPU '{cpu}' not found. Available:", file=sys.stderr)
            for k in sorted(cpu_isas):
                print(f"  {k}", file=sys.stderr)
            sys.exit(1)

    isa = args.isa or cpu_isas[cpu]
    arch, _, _ = parse_isa_string(isa)

    pmu_file = select_pmu(cpu, arch)
    intc_kind = normalize_intc_kind(args.intc or select_intc_kind(cpu))
    link_pmu_file = select_link_pmu(cpu)
    gmac_type = detect_gmac_type(cpu, args.gmac)
    gmac_file = select_gmac(gmac_type)
    platform = resolve_platform(cpu, args.platform, arch)
    # FPGA type is encoded in the platform name ('minimesh-hejian'); the bare
    # name selects the platform's default FPGA type (haps for NGF platforms).
    requested_ftype = (split_fpga_platform(args.platform)[1]
                       if args.platform else None)
    plat = intc.load_platform(platform)
    plat = apply_fpga_type(plat, requested_ftype, platform)
    plat = apply_emu_overlay(plat, args.emu, platform)
    nics, pcie_rps, iommu_instances, enabled_rcs = parse_net_selection(
        plat, args.nic, args.iommu, args.gmac, intc_kind)
    memory = plat["memory"]
    peripherals = plat["peripherals"]
    link_ip = plat.get("link_ip", {})
    # HSM block: pmc drives the DTS pmc node + socinit IPMC writes. Optional —
    # platforms without a PMC (e.g. ngf, PCU-only) omit it and get no pmc node.
    pmc = plat.get("hsm", {}).get("pmc")
    # hsm.pcu.nodes = the PCU address constants driving the PCU HSM DTS node;
    # the effective version comes from select_pcu_version (CSV pcu column
    # override > platform hsm.pcu.version). Unregistered -> no PCU node.
    pcu_nodes = plat.get("hsm", {}).get("pcu", {}).get("nodes")
    # cluster window (cores per cluster) is a SoC fact: the single source of
    # truth shared by the cpu-node/cbqri path (compute_n_clusters, render_xl300)
    # and the ntrace path (render_ntrace_dts._topology).
    cores_per_cluster = plat["cores_per_cluster"]

    # Effective layout: --cores-num (+ --clusters) is an equivalent alternative
    # to --layout. When --cores-num is given it wins; --clusters (or, if omitted,
    # the smallest count spanning the indices) sets the total width, then
    # cores_to_layout emits the same MSB-first bitstring --layout would take.
    if args.cores_num is not None:
        cores_num = [int(x) for x in args.cores_num.replace(" ", "").split(",")
                     if x != ""]
        clusters = (args.clusters if args.clusters is not None
                    else (max(1, compute_n_clusters(
                        [0] * (max(cores_num) + 1), cores_per_cluster))
                        if cores_num else 1))
        try:
            layout_str = cores_to_layout(clusters, cores_num, cores_per_cluster)
        except ValueError as e:
            sys.exit(f"ERROR: {e}")
    else:
        layout_str = args.layout
    layout_bits = parse_layout(layout_str)
    # MSB-first: parse_layout reversed layout_str so layout_bits[i] maps to
    # physical core i. enabled_ids holds the core indices turned on, so an
    # asymmetric layout (e.g. 1111000011110000) enables cores {4,5,6,7,12,13,14,15}.
    enabled_ids = [i for i, bit in enumerate(layout_bits) if bit]
    if not enabled_ids:
        enabled_ids = [0]

    n_clusters = compute_n_clusters(layout_bits, cores_per_cluster)

    trace_env = {}
    for envvar in ("TRACE_CONTEXT", "TRACE_STALL", "TRACE_INHIBIT_SRC",
                   "TRACE_TRIGGER_ENABLE", "TRACE_TIMESTAMP_ENABLE",
                   "TRACE_RUN_IN_DEBUGMODE", "TRACE_SINK_WORKING_MODE"):
        val = os.environ.get(envvar)
        if val:
            trace_env[envvar] = val
    # --ntrace: false = off; true (or bare) = auto-select the config from the
    # CPU via the CSV; any other value names a data/ntrace/<stem>.json used
    # directly (bypassing the CSV). An apb/base-address change is applied by
    # adding a new json under data/ntrace/ and passing its stem here. Unknown
    # stems fail fast in render_ntrace_dts.load_ntrace_consts (lists available).
    ntrace_enable = args.ntrace != "false"
    ntrace_config_override = None if args.ntrace in ("false", "true") else args.ntrace
    # --pmc: false = off; true (or bare) = on with the platform's default reg
    # base (pmc.base); any other value is a reg base-address override.
    pmc_enabled = args.pmc != "false"
    pmc_base_override = None if args.pmc in ("false", "true") else args.pmc
    ntrace_memory, ntrace = render_ntrace(ntrace_enable, cpu, platform,
                                          layout_bits, args.trace_sba, trace_env,
                                          cores_per_cluster,
                                          ntrace_config_override)

    # --complexes: enabled-core set for fixed-SoC platforms, listed by
    # complex with optional per-slot masks (e.g. 8,11:01,12:10,15). Harts are
    # renumbered 0..N-1; only per-complex peripheral addresses shift.
    # --layout 在固定 SoC 平台等价可用: 位串换算成 complex/slot 集合
    # (bit i = 核 i ↔ complex i//cpc 的 slot i%cpc, 与 CORE_LAYOUT 位图同序);
    # 与 --complexes 二选一, 同传报错。
    pairs = None
    mt_cplx = (plat.get("intc", {}).get("aplic", {})
               .get("mtimer_percore", {}).get("complex"))
    if args.complexes and args.layout != "1":
        sys.exit("generate_dts: --complexes 与 --layout 二选一, 不能同时使用")
    # parse_layout 把非法位串静默折成 [1], 固定 SoC 路径必须校验原始串。
    if args.complexes or (plat.get("cpu_block") and args.layout != "1"):
        cb = plat.get("cpu_block")
        if not cb:
            sys.exit("generate_dts: --complexes requires a fixed-SoC platform "
                     "(cpu_block in the platform JSON)")
        if not mt_cplx:
            sys.exit("generate_dts: --complexes/--layout requires mtimer_percore."
                     "complex (addressing spec) in the platform JSON")
        cpc = mt_cplx.get("cores_per_complex", 2)
        if args.complexes:
            pairs = parse_complexes(args.complexes, cpc)
        else:
            if not all(c in "01" for c in args.layout):
                sys.exit(f"generate_dts: on fixed-SoC platforms --layout must "
                         f"be a binary bitmap (got '{args.layout}')")
            bits = parse_layout(args.layout)
            pairs = [(i // cpc, i % cpc) for i, bit in enumerate(bits) if bit]
            if not pairs:
                sys.exit("generate_dts: --layout enables no core")
        cpd = mt_cplx.get("complexes_per_die")
        dies = mt_cplx.get("dies")
        if cpd and dies:
            bad = sorted({c for c, _ in pairs if c >= cpd * dies})
            if bad:
                sys.exit(f"generate_dts: complex {bad} out of range "
                         f"({dies} dies x {cpd} complexes/die)")
        max_cores_total = int(plat.get("max_cores", len(pairs)))
        if len(pairs) > max_cores_total:
            sys.exit(f"generate_dts: --complexes enables {len(pairs)} cores, "
                     f"platform max_cores={max_cores_total}")

    # Fixed-SoC default IMSIC size follows the non-skip cpu_block core count
    # (minimesh = 2 with the skip-flip flow, fullmesh = all cores).
    fixed_default_n = None
    if plat.get("cpu_block"):
        fixed_default_n = sum(1 for c in plat["cpu_block"]["cores"]
                              if c.get("status") != "skip")
    imsics_core_count = len(pairs) if pairs else (fixed_default_n or 2)
    net_blocks = render_net_blocks(plat, nics, pcie_rps, iommu_instances,
                                   enabled_rcs)

    defines = {
        "MODEL": f"Xuantie {cpu}",
        "CPU_BLOCK": render_cpu(isa, layout_bits, args.timer, intc_kind,
                                args.bus, link_ip, cores_per_cluster),
        "PMU_BLOCK": render_pmu(pmu_file),
        "PMC_BLOCK": render_pmc(pmc, pmc_enabled, pmc_base_override),
        "PCU_BLOCK": render_pcu(select_pcu_version(cpu, plat), pcu_nodes,
                                enabled_ids, plat["max_cores"]),
        "INTC_BLOCK": render_intc(plat, platform, cpu, enabled_ids,
                                  intc_kind, isa),
        "GMAC_BLOCK": render_gmac(gmac_file, peripherals),
        "LINK_PMU_BLOCK": render_link_pmu(link_pmu_file, args.bus, link_ip),
        "XL300_BLOCK": render_xl300(enabled_ids, arch, args.bus, link_ip,
                                    cores_per_cluster),
        "NTRACE_MEMORY_BLOCK": ntrace_memory,
        "NTRACE_BLOCK": ntrace,
        # Fixed-SoC blocks (NGF minimesh/fullmesh skeletons); empty and unused
        # on platforms without the opt-in JSON keys.
        "CPU_CORES_BLOCK": (render_cpu_fixed(plat, isa, pairs)
                            if plat.get("cpu_block") else ""),
        "MTIMER_BLOCK": render_mtimer_percore(plat, isa, pairs),
        # Network selection (--nic, pcie default): rc1 rciep aplic pair +
        # pcie_rc0/rc1 or gmac1 (fullsoc skeleton ${APLIC_RC1_BLOCK}/
        # ${NET_BLOCK}); empty on platforms without a `net` JSON section.
        "APLIC_RC1_BLOCK": net_blocks[0],
        "NET_BLOCK": net_blocks[1],
        "IMSICS_BLOCK": (render_imsics_block(plat, imsics_core_count)
                          if plat.get("cpu_block") else ""),
    }
    if plat.get("cpu_block"):
        defines["TIMEBASE"] = plat["cpu_block"]["timebase"]
    defines.update(build_memory_defines(memory, args.memsize))
    defines.update(build_serial_defines(peripherals))
    defines.update(build_chosen_defines(plat, arch, args.optee, args.sv))

    tmpl_name = plat.get("dts_template", "zsb.dts.in")
    output = dtsi_engine.render((TMPL_DIR / tmpl_name).read_text(), defines)
    Path(args.output).parent.mkdir(parents=True, exist_ok=True)
    Path(args.output).write_text(output)

    print(f"Generated: {args.output}")
    print(f"  cpu={cpu}  arch={arch}  layout={args.layout}  clusters={n_clusters}")
    print(f"  pmu={pmu_file}  intc={intc_kind}  gmac={gmac_file}")
    if not ntrace_enable:
        ntrace_desc = "no"
    elif ntrace_config_override:
        ntrace_desc = f"{ntrace_config_override} (json)"
    else:
        ntrace_desc = f"{select_trace_config(cpu, platform)} (auto)"
    print(f"  link-xl200={link_pmu_file or '—'}  ntrace={ntrace_desc}")


if __name__ == "__main__":
    main()
