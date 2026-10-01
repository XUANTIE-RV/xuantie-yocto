#!/usr/bin/env python3
"""
render_socinit.py — render gdbinit/zsb_socinit.gdbinit from its template.

Templates the master zsb_socinit.gdbinit verbatim (zsb_socinit.gdbinit.in),
with the SoC-init knobs ($net_mode / $link_mode / $pmc_init / $stl3_base) plus
all hard-coded MMIO addresses (gmac init, ncore link, stl3 base, per-core
release) turned into ${MACRO} placeholders sourced from the platform JSON
(peripherals.gmac.init, hsm.pmc.ipmc or the hsm.pcu.boot_release fallback,
link_ip), then renders via the shared dtsi_engine.
"""

import argparse
import json
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import dtsi_engine
import generate_dts

FPGA_DIR = Path(__file__).resolve().parent.parent
GDBINIT_DIR = FPGA_DIR / "gdbinit"
PLATFORMS_DIR = FPGA_DIR / "data" / "platforms"
DEFAULT_OUT = GDBINIT_DIR / "zsb_socinit.gdbinit"
DEFAULT_TMPL = GDBINIT_DIR / "zsb_socinit.gdbinit.in"


def _parse(v):
    return v if isinstance(v, int) else int(v, 0)


def _hex(v):
    if isinstance(v, str):
        return v
    return f"0x{v:x}"


def load_platform(platform_name):
    p = PLATFORMS_DIR / f"{platform_name}.json"
    if not p.exists():
        sys.exit(f"render_socinit: unknown platform '{platform_name}'")
    return json.loads(p.read_text())


def build_defines(cpu, bus, gmac, pmc, plat, emu=False):
    link = plat.get("link_ip", {})
    net_mode = "2" if emu else ("1" if gmac == "h100_v2" else "0")

    if "c906" in cpu:
        link_mode = "0"
    elif bus == "ncore":
        link_mode = "1"
    elif bus in ("stlk", "stlk2"):
        link_mode = "2"
    elif bus == "none":
        link_mode = "0"
    elif bus == "stlk3":
        # stlk3 (XuanTie Link 300) is a DTS-only knob that enables the xl300
        # PMU/CBQRI block; it requires no gdbinit link initialization.
        link_mode = "0"
    else:
        sys.exit(f"render_socinit: unknown bus '{bus}' "
                 "(expected none/stlk/stlk2/stlk3/ncore)")

    # STL3_BASE is runtime-consumed only under link_mode 2 (stlk/stlk2), but
    # the template substitutes the macro unconditionally. Platforms without
    # xl200 (e.g. NGF) get a dead 0x0 placeholder; selecting stlk/stlk2 on
    # such a platform fails fast.
    xl200 = link.get("xl200")
    if xl200:
        stl3 = _hex(xl200["stl3_base_stlk2"] if bus == "stlk2"
                    else xl200["stl3_base"])
    elif bus in ("stlk", "stlk2"):
        sys.exit(f"render_socinit: --bus {bus} requires link_ip.xl200, "
                 "which this platform does not declare")
    else:
        stl3 = "0x0"
    pmc_init = "1" if pmc else "0"

    gm = plat["peripherals"]["gmac"]["init"]
    gm_base = _parse(gm["base"])
    def _gmac_writes(mode):
        """Ordered {addr, value} write sequence for a gmac mode list.

        The JSON keeps the mode as an explicit list so the register write
        order is authoritative (the same offset may appear twice, e.g. 0x0c
        written pre- then post-config)."""
        return [{"GMAC_WR_ADDR": _hex(gm_base + _parse(w["offset"])),
                 "GMAC_WR_VAL":  _hex(w["value"])} for w in gm[mode]]

    # ncore: snoop/dvm regs derived from per-cluster base + offset. Consumed
    # only under link_mode 1 (--bus ncore); platforms without ncore (e.g.
    # NGF) get dead 0x0 defines + empty snoop repeats, and selecting
    # --bus ncore on such a platform fails fast.
    nc = link.get("ncore")
    if bus == "ncore" and not nc:
        sys.exit("render_socinit: --bus ncore requires link_ip.ncore, "
                 "which this platform does not declare")
    if nc:
        snoop_off = _parse(nc["snoop_offset"])
        snoop_stride = _parse(nc["snoop_stride"])
        dvm_off = _parse(nc["dvm_offset"])
        c0_base = _parse(nc["cluster0_base"])
        c1_base = _parse(nc["cluster1_base"])

        def _snoops(base, count):
            return [{"NCORE_SNOOP_ADDR": _hex(base + i * snoop_stride + snoop_off)}
                    for i in range(int(count))]

        ncore_defines = {
            "NCORE_C0_DVM": _hex(c0_base + dvm_off),
            "NCORE_C1_DVM": _hex(c1_base + dvm_off),
            "NCORE_ENABLE": _hex(nc["enable_value"]),
        }
        ncore_c0_snoop = _snoops(c0_base, nc["cluster0_snoop_count"])
        ncore_c1_snoop = _snoops(c1_base, nc["cluster1_snoop_count"])
    else:
        ncore_defines = {
            "NCORE_C0_DVM": "0x0",
            "NCORE_C1_DVM": "0x0",
            "NCORE_ENABLE": "0x0",
        }
        ncore_c0_snoop = []
        ncore_c1_snoop = []

    defines = {
        "NET_MODE": net_mode,
        "LINK_MODE": link_mode,
        "PMC_INIT": pmc_init,
        "STL3_BASE": stl3,
        **ncore_defines,
    }

    # Per-core release: platforms with hsm.pmc.ipmc use the IPMC regs (release +
    # clock writes, gated by $pmc_init); platforms without pmc/ipmc (NGF
    # minimesh/fullmesh) use the hsm.pcu.boot_release per-hart release regs,
    # emitted unconditionally via PCU_RELEASE — NGF 没有 pmc,不走 $pmc_init 门控。
    ipmc = plat.get("hsm", {}).get("pmc", {}).get("ipmc")
    ipmc_cores = []
    pcu_cores = []
    if ipmc:
        base = _parse(ipmc["base_addr"])
        stride = _parse(ipmc["release_stride"])
        rel_off = _parse(ipmc["release_offset"])
        clk_off = _parse(ipmc["clock_offset"])
        rel_val = _hex(ipmc["release_value"])
        clk_val = _hex(ipmc["clock_value"])
        for i in range(int(ipmc["n_cores"])):
            ipmc_cores.append({
                "IPMC_CORE_ID": i,
                "IPMC_RELEASE_ADDR": _hex(base + i * stride + rel_off),
                "IPMC_RELEASE_VAL":  rel_val,
                "IPMC_CLOCK_ADDR":   _hex(base + i * stride + clk_off),
                "IPMC_CLOCK_VAL":    clk_val,
                "HAS_CLOCK": "1",
            })
    else:
        if pmc:
            sys.exit("render_socinit: --pmc requires hsm.pmc.ipmc in the "
                     "platform JSON, which this platform does not declare")
        pcu = plat.get("hsm", {}).get("pcu", {}).get("boot_release", {})
        rel_val = pcu.get("release_value")
        for i, addr in enumerate(pcu.get("release_addrs", [])):
            pcu_cores.append({
                "IPMC_CORE_ID": i,
                "IPMC_RELEASE_ADDR": _hex(addr),
                "IPMC_RELEASE_VAL":  _hex(rel_val),
            })
    return defines, {
        "IPMC_CORES": ipmc_cores,
        "PCU_RELEASE": pcu_cores,
        "GMAC_H100_WRITES": [] if emu else _gmac_writes("mode_h100"),
        "GMAC_C930_WRITES": [] if emu else _gmac_writes("mode_haps_v2"),
        "NCORE_C0_SNOOP": ncore_c0_snoop,
        "NCORE_C1_SNOOP": ncore_c1_snoop,
    }


def render(template_text, cpu, bus, gmac, pmc, platform="xiaohui_v1", emu=False):
    plat = load_platform(platform)
    defines, repeats = build_defines(cpu, bus, gmac, pmc, plat, emu)
    return dtsi_engine.render(template_text, defines, repeats)


def main():
    ap = argparse.ArgumentParser(
        description="Render zsb_socinit.gdbinit from template")
    ap.add_argument("--cpu", required=True, help="CPU model")
    ap.add_argument("--bus", default="none",
                    help="link mode: ncore/stlk/stlk2/stlk3/none")
    ap.add_argument("--gmac", default="h100", help="gmac type: h100/h100_v2/pro")
    ap.add_argument("--pmc", action="store_true", help="enable PMC init")
    ap.add_argument("--emu", action="store_true", help="disable GMAC initialization")
    ap.add_argument("--platform", default=None,
                    help="platform name (used to load gdbinit addresses); "
                         "auto-resolved from --cpu when omitted")
    ap.add_argument("--socinit", default=str(DEFAULT_OUT),
                    help="output path (default: gdbinit/zsb_socinit.gdbinit)")
    ap.add_argument("--template", default=str(DEFAULT_TMPL),
                    help="template path (default: gdbinit/zsb_socinit.gdbinit.in)")
    args = ap.parse_args()

    tmpl = Path(args.template)
    if not tmpl.exists():
        sys.exit(f"render_socinit: template not found: {tmpl}")
    # Auto-resolve the platform from the CPU model when not given explicitly,
    # matching generate_dts.py — otherwise a v2 CPU would render a v1 socinit.
    cpu, _, platform = generate_dts.resolve_cpu_platform(args.cpu, args.platform)
    rendered = render(tmpl.read_text(), cpu, args.bus, args.gmac,
                      args.pmc, platform, args.emu)
    Path(args.socinit).write_text(rendered)
    print(f"rendered {args.socinit} (cpu={cpu}, bus={args.bus}, "
          f"gmac={args.gmac}, pmc={args.pmc}, emu={args.emu}, platform={platform})")


if __name__ == "__main__":
    main()
