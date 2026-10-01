#!/usr/bin/env python3
"""
render_gdbinit.py — render gdbinit/zsb.gdbinit from its template.

Templates the master zsb.gdbinit verbatim (gdbinit/zsb.gdbinit.in) with the
DDR memory layout, reset controller addresses, release/reset values, and
per-core reset/config lines turned into ${MACRO}/##REPEAT## directives.
Addresses come from the platform JSON's "images_layout" block and the
"hsm.pcu.boot_release" block (boot hart-release timing).
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
DEFAULT_OUT = GDBINIT_DIR / "zsb.gdbinit"
DEFAULT_TMPL = GDBINIT_DIR / "zsb.gdbinit.in"
DEFAULT_OPTEE_OUT = GDBINIT_DIR / "zsb_rv64_optee.gdbinit"
DEFAULT_OPTEE_TMPL = GDBINIT_DIR / "zsb_rv64_optee.gdbinit.in"


def _parse(v):
    return v if isinstance(v, int) else int(v, 0)


def _hex(v):
    if isinstance(v, str):
        return v
    return f"0x{v:x}"


def load_platform(platform_name):
    p = PLATFORMS_DIR / f"{platform_name}.json"
    if not p.exists():
        sys.exit(f"render_gdbinit: unknown platform '{platform_name}'")
    return json.loads(p.read_text())


def _boot_release(plat, platform_name):
    """The boot hart-release config (hsm.pcu.boot_release, formerly the
    top-level pcu_config block). Required to render a gdbinit: a platform
    without it cannot describe its reset/release sequence."""
    br = plat.get("hsm", {}).get("pcu", {}).get("boot_release")
    if not br:
        sys.exit(f"render_gdbinit: platform '{platform_name}' lacks "
                 "hsm.pcu.boot_release (no reset/release config)")
    return br


def _release_line(release_addr, default_value, layout_value, n_cores):
    """Global release-register write. cores==1 → commented baseline (narrow
    `release_default` mask); cores>8 → the platform's wide `release_layout`
    mask; otherwise the narrow `release_default` mask."""
    if n_cores == 1:
        return f"\t# set *{release_addr} = {default_value}"
    value = layout_value if n_cores > 8 else default_value
    return f"\tset *{release_addr} = {value}"


def build_defines(layout, cpu, plat, platform_name):
    layout = layout or "1"
    n_cores = len(layout)
    # MSB-first: leftmost bit = highest hart, rightmost = hart0 (legacy
    # update_dts_cpu.sh order). Reverse so layout[i] indexes physical core i.
    layout = layout[::-1]

    if "-cp-xt" in cpu:
        flag = "0x0 | 0x2 | 0x4"
    elif "-cp" in cpu:
        flag = "0x0 | 0x2"
    else:
        flag = "0x0"

    # images_layout groups the boot image addresses per kernel. Addresses
    # shared by both kernels (opensbi/uboot/vmlinux/flag/rootfs) are read from
    # kernel_6_6; the gdbinit template selects dtb/zsb at runtime, so it needs
    # both the 6.6 (${DTB_ADDR}/${ZSB_ADDR}) and 5.10 (${*_5_10}) variants.
    imgs = plat["images_layout"]
    mem = imgs["kernel_6_6"]
    mem_5_10 = imgs["kernel_5_10"]
    pcu = _boot_release(plat, platform_name)
    # release_addrs (per-hart release/config writes) exist only on xiaohui_v2;
    # v1 SoCs omit them. HAS_CONFIG gates the template's per-hart config block
    # and is the sole v1/v2 marker: v2 additionally emits the per-hart config
    # block. Otherwise v1 and v2 share the SAME layout-aware reset/release
    # logic — only -layout-populated harts are active, the rest commented out.
    has_config = "release_addrs" in pcu

    opensbi = _hex(mem["opensbi_addr"])

    # Global hart-release write, layout-aware on both v1 and v2: n_cores==1 ->
    # commented baseline, >8 cores -> wide release_layout mask, otherwise the
    # narrow release_default mask.
    release = _release_line(_hex(pcu["release_addr"]),
                            _hex(pcu["release_default"]),
                            _hex(pcu["release_layout"]),
                            n_cores)

    defines = {
        "OPENSBI_ADDR":         opensbi,
        "UBOOT_ADDR":           _hex(mem["uboot_addr"]),
        "VMLINUX_ADDR":         _hex(mem["vmlinux_addr"]["no-uboot"]),
        "VMLINUX_ADDR_UBOOT":   _hex(mem["vmlinux_addr"]["u-boot"]),
        "DTB_ADDR":             _hex(mem["dtb_addr"]),
        "DTB_ADDR_5_10":        _hex(mem_5_10["dtb_addr"]),
        "ZSB_ADDR":             _hex(mem["zsb_addr"]),
        "ZSB_ADDR_5_10":        _hex(mem_5_10["zsb_addr"]),
        "FLAG_ADDR":            _hex(mem["flag_addr"]),
        "ROOTFS_ADDR":          _hex(mem["rootfs_addr"]),
        "PLIC_DELEGATE_5_10_ADDR": _hex(mem_5_10["plic_delegate_addr"]),
        "FLAG_VALUE":           flag,
        "HAS_CONFIG":           "1" if has_config else "",
        "RELEASE": release,
    }

    if has_config:
        defines["CFG_VALUE"] = _hex(pcu["release_value"])

    reset_addrs = [_hex(a) for a in pcu["reset_addrs"]]
    release_addrs = ([_hex(a) for a in pcu["release_addrs"]]
                     if has_config else [])

    # Per-hart reset (v1 + v2) and per-hart config (v2 only) are layout-gated:
    # only -layout-populated harts are active; the rest are commented with
    # "# ".
    cores = []
    for i, addr in enumerate(reset_addrs):
        enabled = int(layout[i]) if i < len(layout) else 0
        prefix = "" if enabled else "# "
        cores.append({"RESET_ADDR": addr, "RESET_PREFIX": prefix})

    config = []
    for i, addr in enumerate(release_addrs):
        enabled = int(layout[i]) if i < len(layout) else 0
        prefix = "" if enabled else "# "
        config.append({"CFG_ADDR": addr, "CFG_PREFIX": prefix})

    repeats = {
        "CORES": cores,
        "CONFIG": config,
    }
    return defines, repeats


def build_optee_defines(layout, cpu, plat, platform_name):
    """OP-TEE variant defines. Boot images live in the 0x8xxxxxxx window
    (kernel/rootfs) plus a secure bl31 + tee-os; addresses come from the
    platform JSON's images_layout.optee block. Per-hart reset/release reuse
    the same layout-aware CORES/RELEASE machinery as the generic gdbinit."""
    layout = layout or "1"
    n_cores = len(layout)
    # MSB-first (see build_defines): reverse so layout[i] indexes physical core i.
    layout = layout[::-1]

    imgs = plat["images_layout"]
    if "optee" not in imgs:
        sys.exit("render_gdbinit: platform lacks images_layout.optee "
                 "(OP-TEE unsupported on this platform)")
    mem = imgs["optee"]
    pcu = _boot_release(plat, platform_name)

    defines = {
        "VMLINUX_ADDR": _hex(mem["vmlinux_addr"]),
        "ROOTFS_ADDR":  _hex(mem["rootfs_addr"]),
        "ZSB_ADDR":     _hex(mem["zsb_addr"]),
        "DTB_ADDR":     _hex(mem["dtb_addr"]),
        "BL31_ADDR":    _hex(mem["bl31_addr"]),
        "TEEOS_ADDR":   _hex(mem["teeos_addr"]),
        "FLAG_ADDR":    _hex(mem["flag_addr"]),
        "RELEASE": _release_line(_hex(pcu["release_addr"]),
                                 _hex(pcu["release_default"]),
                                 _hex(pcu["release_layout"]),
                                 n_cores),
    }

    cores = []
    for i, addr in enumerate(_hex(a) for a in pcu["reset_addrs"]):
        enabled = int(layout[i]) if i < len(layout) else 0
        cores.append({"RESET_ADDR": addr, "RESET_PREFIX": "" if enabled else "# "})
    return defines, {"CORES": cores}


def render(template_text, layout, cpu, platform="xiaohui_v1",
           optee=False):
    plat = load_platform(platform)
    if optee:
        defines, repeats = build_optee_defines(layout, cpu, plat, platform)
    else:
        defines, repeats = build_defines(layout, cpu, plat, platform)
    return dtsi_engine.render(template_text, defines, repeats)


def main():
    ap = argparse.ArgumentParser(description="Render zsb.gdbinit from template")
    ap.add_argument("--layout", default="1",
                    help="core layout bitstring, MSB-first (leftmost bit = "
                         "highest hart, rightmost = hart0); a 0x-prefixed "
                         "hex bitmap (e.g. 0xC3C30000) is also accepted")
    ap.add_argument("--clusters", type=int, default=None,
                    help="cluster count; with --cores-num builds the layout "
                         "(width = clusters × cores_per_cluster)")
    ap.add_argument("--cores-num", default=None,
                    help="comma-separated GLOBAL core indices to enable "
                         "(equivalent alternative to --layout; overrides it)")
    ap.add_argument("--complexes", default=None,
                    help="fixed-SoC platforms only (cpu_block): enabled "
                         "complex structure, same syntax as generate_dts.py "
                         "--complexes. Harts renumber 0..N-1, so the layout "
                         "becomes N enabled bits; without it the platform's "
                         "non-skip cpu_block.cores count is used.")
    ap.add_argument("--cpu", required=True, help="CPU model")
    ap.add_argument("--platform", default=None,
                    help="platform name (used to load gdbinit addresses); "
                         "auto-resolved from --cpu when omitted")
    ap.add_argument("--optee", action="store_true",
                    help="render the OP-TEE variant (bl31 + tee-os, "
                         "kernel/rootfs in the 0x8xxxxxxx window)")
    ap.add_argument("--gdbinit", default=None,
                    help="output path (default: gdbinit/zsb.gdbinit, "
                         "or zsb_rv64_optee.gdbinit with --optee)")
    ap.add_argument("--template", default=None,
                    help="template path (default: gdbinit/zsb.gdbinit.in, "
                         "or zsb_rv64_optee.gdbinit.in with --optee)")
    args = ap.parse_args()
    # --layout 接受 0x 十六进制位图(与 generate_dts 一致), 统一展成二进制
    try:
        args.layout = generate_dts.expand_layout(args.layout)
    except ValueError as e:
        sys.exit(f"render_gdbinit: {e}")

    out = args.gdbinit or str(DEFAULT_OPTEE_OUT if args.optee else DEFAULT_OUT)
    tmpl = Path(args.template or (DEFAULT_OPTEE_TMPL if args.optee
                                  else DEFAULT_TMPL))
    if not tmpl.exists():
        sys.exit(f"render_gdbinit: template not found: {tmpl}")
    # Auto-resolve the platform from the CPU model when not given explicitly,
    # matching generate_dts.py — otherwise a v2 CPU would render a v1 gdbinit.
    cpu, _, platform = generate_dts.resolve_cpu_platform(args.cpu, args.platform)

    # Effective layout. Fixed-SoC platforms (cpu_block, NGF minimesh/fullmesh):
    # the core set comes from the complex structure — harts renumber 0..N-1,
    # so the layout is N enabled bits (pcu_config supplies the addresses).
    # Otherwise --cores-num (+ --clusters) is an equivalent alternative to
    # --layout (mirrors generate_dts.py); cores_per_cluster comes from the
    # resolved platform JSON so the derived width matches the DTS side.
    layout = args.layout
    plat = load_platform(platform)
    cb = plat.get("cpu_block")
    if cb:
        if args.complexes is not None and layout and layout != "1":
            sys.exit("render_gdbinit: --complexes 与 --layout 二选一, "
                     "不能同时使用")
        if args.complexes is not None:
            cpc = (plat.get("intc", {}).get("aplic", {})
                   .get("mtimer_percore", {}).get("complex", {})
                   .get("cores_per_complex", 2))
            n = len(generate_dts.parse_complexes(args.complexes, cpc))
        elif layout and layout != "1":
            if not all(c in "01" for c in layout):
                sys.exit(f"render_gdbinit: on fixed-SoC platforms --layout "
                         f"must be a binary bitmap (got '{layout}')")
            n = layout.count("1")
        else:
            n = sum(1 for c in cb["cores"] if c.get("status") != "skip")
        layout = "1" * n
    elif args.cores_num is not None:
        cpc = plat["cores_per_cluster"]
        cores_num = [int(x) for x in args.cores_num.replace(" ", "").split(",")
                     if x != ""]
        clusters = (args.clusters if args.clusters is not None
                    else (max(1, generate_dts.compute_n_clusters(
                        [0] * (max(cores_num) + 1), cpc)) if cores_num else 1))
        try:
            layout = generate_dts.cores_to_layout(clusters, cores_num, cpc)
        except ValueError as e:
            sys.exit(f"render_gdbinit: {e}")

    rendered = render(tmpl.read_text(), layout, cpu, platform, args.optee)
    Path(out).write_text(rendered)
    print(f"rendered {out} (layout={layout}, cpu={cpu}, "
          f"platform={platform}, optee={args.optee})")


if __name__ == "__main__":
    main()
