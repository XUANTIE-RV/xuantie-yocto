#!/usr/bin/env python3
"""
render_ntrace_dts.py — pure-Python XuanTie Nexus Trace DTS renderer.

Topology is driven entirely by the CPU-selected trace *version* plus the
`-layout` bitstring (no external per-topology config files). All hardware
constants live in data/ntrace/{version}.json; templates under
data/templates/ntrace/ keep only structure + ${MACRO}/##REPEAT## placeholders.

8-window rule (one cluster == 8 core slots):
    global layout position p  ->  cluster = p // 8 , core-in-cluster = p % 8
Nodes are named by the *global* position p (encoder{p} / funnel1_{p} /
funnel2_{cluster}), matching the cpu-node and gdbinit reset numbering. Only
enabled cores get encoder/funnel1 nodes; a cluster owns a level-1 funnel only
when it has more than one core slot.

Each version JSON declares a "topology_model"; render() dispatches on it via the
TOPOLOGY_MODELS registry (not on the version string), so a new version that reuses
an existing model is a pure-data change — only a genuinely new topology needs a new
renderer + one registry entry. Two models ship today:
  single-l2-funnel (C907/C908/R908, v1): one level-2 funnel; each multi-core
      cluster has a single level-1 funnel collecting its enabled encoders. Byte
      offsets are direct.
  chained-dmi (C930/C925/C920, v2): per-core chained level-1 funnels feeding
      per-cluster chained level-2 funnels. Addr = dmi_apb_base + dmi_off * dmi_scale.

The trace_encoder_*/timestamp_* knobs and the sink working_mode are runtime
configuration (not hardware constants); they come from trace_env, falling back
to the values the legacy shell hardcoded.
"""

import json
import sys
from collections import namedtuple
from math import ceil
from pathlib import Path

import dtsi_engine

FPGA_DIR = Path(__file__).resolve().parent.parent
TMPL_DIR = FPGA_DIR / "data" / "templates" / "ntrace"
NTRACE_DIR = FPGA_DIR / "data" / "ntrace"
SCHEMA_PATH = FPGA_DIR / "data" / "ntrace-schema.json"

CORES_PER_CLUSTER = 8  # default window size; real value comes from the platform
                       # JSON (cores_per_cluster), passed down by generate_dts.

# Derived cluster/core structure shared by every topology model. cpc is the
# window size (cores per cluster) so the renderers no longer read a module const.
Topology = namedtuple("Topology",
                      "n_clusters cluster_slots enabled enabled_set cpc")

TRACE_ENV_DEFAULTS = {
    "TRACE_CONTEXT": "true",
    "TRACE_STALL": "true",
    "TRACE_INHIBIT_SRC": "false",
    "TRACE_TRIGGER_ENABLE": "true",
    "TRACE_TIMESTAMP_ENABLE": "true",
    "TRACE_RUN_IN_DEBUGMODE": "false",
    "TRACE_SINK_WORKING_MODE": "circulating buffer",
}


# -- helpers -----------------------------------------------------------------

def _int(v):
    """Accept int or hex/dec string from the JSON constants."""
    return v if isinstance(v, int) else int(v, 0)


def _tmpl(name):
    return (TMPL_DIR / name).read_text()


def _unit(addr):
    """Node unit-address: hex digits, uppercase (matches printf %X | cut)."""
    return f"{addr:X}"


def _reg_cells(value):
    """64-bit value as a 2-cell '0x{hi} 0x{lo}' reg pair (uppercase hex)."""
    v = value if isinstance(value, int) else int(value, 0)
    return f"0x{(v >> 32) & 0xFFFFFFFF:X} 0x{v & 0xFFFFFFFF:X}"


def _split_cfg_hex(val):
    """Split a config hex string into a 4-cell 'hi lo' reg body (2 values)."""
    digits = val.split("x", 1)[1] if "x" in val else val
    digits = digits.rjust(16, "0")
    return f"0x{digits[:8]} 0x{digits[8:]}"


def _cfg_unit(val):
    return val.split("x", 1)[1] if "x" in val else val


# -- constants loading -------------------------------------------------------

def _validate_consts(version, consts):
    """Validate a ntrace-constants dict against ntrace-schema.json.

    jsonschema is optional (the generator is python3-only and runs on FPGA
    hosts that may lack it): hard-fail on violation when present, skip with a
    notice when absent."""
    try:
        import jsonschema
    except ImportError:
        print(f"render_ntrace_dts: jsonschema not installed; skipping schema "
              f"validation of ntrace '{version}' constants "
              f"(pip install jsonschema to enable)", file=sys.stderr)
        return
    if not SCHEMA_PATH.exists():
        return
    schema = json.loads(SCHEMA_PATH.read_text())
    try:
        jsonschema.validate(consts, schema)
    except jsonschema.ValidationError as e:
        loc = "/".join(str(x) for x in e.absolute_path) or "<root>"
        sys.exit(f"render_ntrace_dts: ntrace '{version}' constants violate "
                 f"ntrace-schema at '{loc}': {e.message}")


def load_ntrace_consts(version):
    p = NTRACE_DIR / f"{version}.json"
    if not p.exists():
        avail = sorted(x.stem for x in NTRACE_DIR.glob("*.json"))
        sys.exit(f"render_ntrace_dts: unknown trace config '{version}' "
                 f"(no data/ntrace/{version}.json; available: "
                 f"{', '.join(avail)})")
    consts = json.loads(p.read_text())
    _validate_consts(version, consts)
    return consts


# -- layout -> topology ------------------------------------------------------

def _topology(layout_bits, cores_per_cluster=CORES_PER_CLUSTER):
    """Derive cluster/core structure from the layout bit list.

    cores_per_cluster is the window size (cluster = pos // cores_per_cluster).
    Returns (n_clusters, cluster_slots, enabled, enabled_set, cpc) where
    cluster_slots[c] is the number of core slots (<=cpc) in cluster c and
    enabled is the sorted list of enabled global positions."""
    n_slots = len(layout_bits)
    if n_slots == 0:
        sys.exit("render_ntrace_dts: empty layout")
    cpc = cores_per_cluster
    n_clusters = ceil(n_slots / cpc)
    cluster_slots = [
        min(cpc, n_slots - c * cpc)
        for c in range(n_clusters)
    ]
    enabled = [p for p, b in enumerate(layout_bits) if b]
    if not enabled:
        sys.exit("render_ntrace_dts: no core enabled in layout")
    return n_clusters, cluster_slots, enabled, set(enabled), cpc


# -- node renderers ----------------------------------------------------------

def _render_encoder(cpu_id, addr, node_size, out_ep, env):
    d = {
        "ENC_NAME": f"encoder{cpu_id}",
        "ENC_UNIT": _unit(addr),
        "ENC_REG": _reg_cells(addr),
        "ENC_SIZE": _reg_cells(node_size),
        "ENC_CPU": cpu_id,
        "ENC_OUT": out_ep,
    }
    d.update(env)
    return dtsi_engine.render(_tmpl("encoder.dtsi.in"), d)


def _render_funnel(name, addr, node_size, level, port_eps, out_ep):
    ports = [{"PORT_IDX": i, "PORT_EP": ep} for i, ep in enumerate(port_eps)]
    return dtsi_engine.render(_tmpl("funnel.dtsi.in"), {
        "FN_NAME": name,
        "FN_UNIT": _unit(addr),
        "FN_REG": _reg_cells(addr),
        "FN_SIZE": _reg_cells(node_size),
        "FN_LEVEL": level,
        "FN_OUT": out_ep,
    }, {"PORTS": ports})


def _render_sink(sink_type, addr, node_size, in_ep, env):
    return dtsi_engine.render(_tmpl("sink.dtsi.in"), {
        "SINK_NAME": "sink0",
        "SINK_TYPE": sink_type,
        "SINK_UNIT": _unit(addr),
        "SINK_REG": _reg_cells(addr),
        "SINK_SIZE": _reg_cells(node_size),
        "SINK_WORKING_MODE": env["TRACE_SINK_WORKING_MODE"],
        "SINK_IN": in_ep,
    })


def render_memory(consts):
    return dtsi_engine.render(_tmpl("reserved_memory.dtsi.in"), {
        "RESV_UNIT": _cfg_unit(str(consts["reserved_mem_start"])),
        "RESV_BASE": _split_cfg_hex(str(consts["reserved_mem_start"])),
        "RESV_SIZE": _split_cfg_hex(str(consts["reserved_mem_size"])),
    })


# -- v1 ----------------------------------------------------------------------

def _render_v1(consts, topo, sink_type, sba, env):
    n_clusters, cluster_slots, enabled, enabled_set, cpc = topo
    base = _int(consts["dmi_apb_base"])
    stride = _int(consts["cluster_stride"])
    enc_off = _int(consts["encoder_offset"])
    f1_off = _int(consts["funnel1_offset"])
    f2_off = _int(consts["funnel2_offset"])
    node_size = _int(consts["node_size"])
    sink_off = _int(consts["sink_offset"][sink_type])

    def cl_of(p):
        return p // cpc

    def co_of(p):
        return p % cpc

    cluster_enabled = {
        c: any(cl_of(p) == c for p in enabled) for c in range(n_clusters)
    }

    funnel2_addr = base + f2_off
    sink_addr = base + sink_off
    nodes = []

    # encoders (enabled only)
    for p in enabled:
        c = cl_of(p)
        addr = base + c * stride + (co_of(p) + 1) * enc_off
        out = "funnel2" if cluster_slots[c] == 1 else f"funnel1_{c}"
        nodes.append(_render_encoder(p, addr, node_size, out, env))

    # level-1 funnels: one per multi-slot cluster that has >=1 enabled core.
    # FIX vs legacy shell: input_port lists every enabled encoder in the
    # cluster (the shell's pre-scan underflowed CPU_ID and emitted none).
    for c in range(n_clusters):
        if cluster_slots[c] <= 1 or not cluster_enabled[c]:
            continue
        addr = base + c * stride + f1_off
        ports = [f"encoder{p}" for p in enabled if cl_of(p) == c]
        nodes.append(_render_funnel(f"funnel1_{c}", addr, node_size, 1,
                                    ports, "funnel2"))

    # level-2 funnel (single)
    f2_ports = []
    for c in range(n_clusters):
        if not cluster_enabled[c]:
            continue
        if cluster_slots[c] == 1:
            p = next(p for p in enabled if cl_of(p) == c)
            f2_ports.append(f"encoder{p}")
        else:
            f2_ports.append(f"funnel1_{c}")
    nodes.append(_render_funnel("funnel2", funnel2_addr, node_size, 2,
                                f2_ports, "sink0"))

    # sink
    nodes.append(_render_sink(sink_type, sink_addr, node_size, "funnel2", env))
    return "\n".join(nodes)


# -- v2 ----------------------------------------------------------------------

def _render_v2(consts, topo, sink_type, sba, env):
    n_clusters, cluster_slots, enabled, enabled_set, cpc = topo
    base = _int(consts["dmi_apb_base"])
    scale = _int(consts["dmi_scale"])
    cl_stride = _int(consts["cluster_dmi_stride"])
    f2_dmi = _int(consts["funnel2_dmi_offset"])
    f1_base = _int(consts["funnel1_dmi_base"])
    f1_stride = _int(consts["funnel1_dmi_core_stride"])
    enc_base = _int(consts["encoder_dmi_base"])
    enc_stride = _int(consts["encoder_dmi_core_stride"])
    node_size = _int(consts["node_size"])
    sink_dmi_off = _int(consts["sink_dmi_offset"][sink_type])
    sba_off = _int(consts["sink_sba_dmi_offset"])
    partial_base = _int(consts["sink_partial_dmi_base"])

    def calc(dmi):
        return base + dmi * scale

    def cl_of(p):
        return p // cpc

    def co_of(p):
        return p % cpc

    # enabled global positions grouped per cluster, in ascending order.
    cluster_cores = {c: [] for c in range(n_clusters)}
    for p in enabled:
        cluster_cores[cl_of(p)].append(p)
    active = [c for c in range(n_clusters) if cluster_cores[c]]
    first_enabled, last_enabled = active[0], active[-1]
    partial = len(active) != n_clusters

    funnel2_addr = {c: calc(c * cl_stride + f2_dmi) for c in active}
    funnel1_addr = {}
    cluster_last_funnel1 = {}
    for c in active:
        if cluster_slots[c] > 1:
            for p in cluster_cores[c]:
                funnel1_addr[p] = calc(c * cl_stride + f1_base
                                       + f1_stride * co_of(p))
            cluster_last_funnel1[c] = f"funnel1_{cluster_cores[c][-1]}"

    nodes = []

    # encoders
    for c in active:
        for p in cluster_cores[c]:
            addr = calc(c * cl_stride + enc_base + enc_stride * co_of(p))
            out = f"funnel2_{c}" if cluster_slots[c] == 1 else f"funnel1_{p}"
            nodes.append(_render_encoder(p, addr, node_size, out, env))

    # level-1 funnels (enabled cores of a multi-slot cluster, chained)
    for c in active:
        if cluster_slots[c] <= 1:
            continue
        cores = cluster_cores[c]
        for k, p in enumerate(cores):
            ports = [f"encoder{p}"]
            if k > 0:
                ports.append(f"funnel1_{cores[k - 1]}")
            if k == len(cores) - 1:
                out = f"funnel2_{c}"
            else:
                out = f"funnel1_{cores[k + 1]}"
            nodes.append(_render_funnel(f"funnel1_{p}", funnel1_addr[p],
                                        node_size, 1, ports, out))

    # level-2 funnels (per enabled cluster, chained)
    for c in active:
        if cluster_slots[c] == 1:
            local_input = f"encoder{cluster_cores[c][0]}"
        else:
            local_input = cluster_last_funnel1[c]

        if c == first_enabled:
            ports = [local_input]
        else:
            prev = max(x for x in active if x < c)
            ports = [local_input, f"funnel2_{prev}"]

        if c == last_enabled:
            out = "sink0"
        else:
            nxt = [x for x in active if x > c]
            out = f"funnel2_{nxt[0]}" if nxt else "sink0"
        nodes.append(_render_funnel(f"funnel2_{c}", funnel2_addr[c],
                                    node_size, 2, ports, out))

    # sink
    if sba:
        sink_dmi = cl_stride * (n_clusters + 1) + sba_off
    elif partial:
        sink_dmi = partial_base + sink_dmi_off
    else:
        sink_dmi = cl_stride * n_clusters + sink_dmi_off
    nodes.append(_render_sink(sink_type, calc(sink_dmi), node_size,
                              f"funnel2_{last_enabled}", env))
    return "\n".join(nodes)


# -- model registry ----------------------------------------------------------

# topology_model (declared in data/ntrace/{version}.json) -> renderer.
# A new trace version that reuses one of these models is a pure-data change;
# a genuinely new topology adds one renderer plus one entry here (and its
# constant keys to ntrace-schema.json's per-model branch).
TOPOLOGY_MODELS = {
    "single-l2-funnel": _render_v1,
    "chained-dmi": _render_v2,
}


# -- entry -------------------------------------------------------------------

def render(version, layout_bits, sink_type=None, sba=False, trace_env=None,
           cores_per_cluster=CORES_PER_CLUSTER):
    """Return (ntrace_memory_str, ntrace_str).

    version      : version identifier == data/ntrace/<version>.json stem
                   (chosen by generate_dts.select_trace_config)
    layout_bits  : list of 0/1, index == global core slot position
    sink_type    : "smem" | "sram" | "pib" (default from version JSON)
    sba          : system-bus-access sink addressing (chained-dmi only)
    trace_env    : runtime knob overrides (TRACE_* keys)
    cores_per_cluster : window size (cluster = pos // cpc); generate_dts passes
                   the platform JSON's value, single source of truth for the
                   cluster boundary shared with the cpu-node/cbqri path.

    The topology algorithm is selected by the JSON's "topology_model" field via
    the TOPOLOGY_MODELS registry, not by the version string.
    """
    consts = load_ntrace_consts(version)
    env = dict(TRACE_ENV_DEFAULTS)
    if trace_env:
        env.update({k: v for k, v in trace_env.items() if v})
    if sink_type is None:
        sink_type = consts["default_sink_type"]

    topo = Topology(*_topology(layout_bits, cores_per_cluster))
    memory = render_memory(consts)

    model = consts["topology_model"]
    renderer = TOPOLOGY_MODELS.get(model)
    if renderer is None:
        sys.exit(f"render_ntrace_dts: unknown topology_model {model!r} in "
                 f"'{version}.json' (known: {', '.join(sorted(TOPOLOGY_MODELS))})")
    body = renderer(consts, topo, sink_type, sba, env)
    return memory, body


if __name__ == "__main__":
    import argparse
    ap = argparse.ArgumentParser(description="render ntrace dtsi (debug)")
    ap.add_argument("--version", required=True,
                    help="trace-constants json stem under data/ntrace/ "
                         "(e.g. ntrace_xiaohui_v1, ntrace_xiaohui_v2, or an "
                         "apb/base variant)")
    ap.add_argument("--layout", required=True,
                    help="core enable bitstring, e.g. 1111 / 1110000011100000")
    ap.add_argument("--sink", default=None, choices=["smem", "sram", "pib"])
    ap.add_argument("--sba", action="store_true")
    ap.add_argument("--cores-per-cluster", type=int, default=CORES_PER_CLUSTER,
                    help="window size, cluster = pos // N (default %(default)s; "
                         "generate_dts passes the platform JSON value)")
    args = ap.parse_args()
    bits = [int(ch) for ch in args.layout]
    m, b = render(args.version, bits, sink_type=args.sink, sba=args.sba,
                  cores_per_cluster=args.cores_per_cluster)
    print("=== ntrace-memory.dtsi ===")
    print(m)
    print("=== ntrace.dtsi ===")
    print(b)
