#!/bin/bash
set -e

SCRIPT_DIR="$(cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
cd $SCRIPT_DIR

declare -A default_isa

# Load CPU-ISA mapping from gen-cpu-confs.sh generated files
# Format: default_isa[cpu]="isa_string" (bash associative array assignments)
# Source all *.conf files under cpu-isa-configs/ so that every CPU model
# (including standalone confs such as xt-c9204.conf) is recognized.
CPU_ISA_CONFIGS_DIR="$(cd "$SCRIPT_DIR/data/cpu-isa-configs" 2>/dev/null && pwd)"

if [ -z "$CPU_ISA_CONFIGS_DIR" ] || [ ! -d "$CPU_ISA_CONFIGS_DIR" ]; then
    echo "ERROR: CPU-ISA configs directory not found: $SCRIPT_DIR/data/cpu-isa-configs"
    echo "Please run toolchain-init.sh first to generate the mapping files."
    exit 1
fi

echo "Loading CPU-ISA mappings from: $CPU_ISA_CONFIGS_DIR"
shopt -s nullglob
for conf_file in "$CPU_ISA_CONFIGS_DIR"/*.conf; do
    [ -f "$conf_file" ] || continue
    echo "  - sourcing $(basename "$conf_file")"
    # Later files may override earlier same-named keys (last-wins is expected).
    source "$conf_file"
done
shopt -u nullglob
echo "Loaded ${#default_isa[@]} CPU models"

function print_help() {
	echo "Usage:  update_dts_cpu.sh <soc_platform>  <-cpu>  [-layout 1]  [-isa default]"
	echo "        (options are single-dash, e.g. -cpu; help: -h | -help | --help | help)"
	echo
	echo "<soc_platform>:    Choose one from [ xiaohui_v1 | xiaohui_v2 | xiaohui_v1_rv32 ] etc."
	echo "                   (must match a data/platforms/<name>.json; otherwise the"
	echo "                    platform is auto-selected from the CPU model)."
	echo "                   FPGA type is part of the name: '<platform>-<ftype>',"
	echo "                   e.g. minimesh-hejian (NGF: haps default | hejian);"
	echo "                   the bare name selects the default FPGA type."
	echo "<-cpu your_cpu>:   Choose one from [ c910 | c920v2 | c908 | c907 ] etc."
	echo "[-layout 1]:       Core layout bitmap, MSB-first (leftmost bit=highest hart,"
	echo "                   rightmost=hart0). Choose one from [ 11 | 110011 | 11111111 ]"
	echo "                   etc.; a 0x-prefixed hex bitmap is also accepted and expanded"
	echo "                   4 bits per hex digit (e.g. 0xC3C30000)."
	echo "[-clusters N]:     Cluster count; with -cores_num sets the layout width"
	echo "                   (= clusters × cores_per_cluster). Omit to derive from -cores_num."
	echo "[-cores_num list]: Comma-separated GLOBAL core indices to enable, e.g."
	echo "                   4,5,6,7,12,13,14,15. Equivalent alternative to -layout"
	echo "                   (overrides it). Example: -clusters 2 -cores_num 4,5,6,7,12,13,14,15"
	echo "                   == -layout 1111000011110000."
	echo "[-isa default]:    Choose one from [ default | your_customized_isa ] etc."
	echo "[-timer 25000000]: Input time frequency, default value is 25000000."
	echo "                   Ignored on fixed-SoC platforms (JSON with cpu_block):"
	echo "                   there timebase-frequency comes from"
	echo "                   cpu_block.timebase (1000000000 with -emu)."
	echo "[-bus none]:       Choose one from [ none | stlk | stlk2 | stlk3 | ncore ] (unknown values are rejected)."
	echo "[-intc plic]:      Choose one from [ plic | aplic ] etc."
	echo "[-sv 48]:          Choose one from [ 39 | 48 | others(do nothing) ] etc."
	echo "[-gmac h100]:      xiaohui gmac controller type: h100 | pro (selects the"
	echo "                   gmac dtsi template). Not a NIC switch — on NGF platforms"
	echo "                   it is ignored and the gmac NIC comes from -nic gmac."
	echo "[-ntrace false]:   false | true | <ntrace-json-name>. 'true' auto-selects"
	echo "                   ntrace_<家族>_v<版本> from -cpu; a name uses"
	echo "                   data/ntrace/<name>.json directly (e.g. ntrace_xiaohui_v1 |"
	echo "                   ntrace_xiaohui_v2; an apb/base change => add a new json)."
	echo "                   Topology is derived from -layout (8-window)."
	echo "[-trace-sba]:      v2 sink system-bus-access addressing (flag, no value)."
	echo "[-memsize 1G]      Input memory size [ 1G, 2G, 3G, 4G ] etc."
	echo "[-optee false]:    Choose one from [ false | true ] etc."
	echo "[-emu false]:      false | true. true applies the selected NGF platform's"
	echo "                   EMU timing/UART, disabled-PCIe, and no-GMAC overlay without changing its name."
	echo "[-pmc false]:      Choose one from [ false | true | <base-addr> ] (e.g. 0x26900000)."
	echo "                   Rejected on platforms without pmc.ipmc (e.g. NGF"
	echo "                   minimesh/fullmesh — per-core release uses pcu_config)."
	echo "[-complexes list]: Fixed-SoC platforms only (platform JSON with cpu_block):"
	echo "                   the complex structure to enable — global complex ids,"
	echo "                   e.g. 0,3,4,7 or 8:11 (slot mask). Mutually exclusive"
	echo "                   with -layout, which is the equivalent binary bitmap"
	echo "                   (bit i = core i = complex i//2 slot i%2)."
	echo "                   IMSIC nodes are rendered inline in the output DTS and"
	echo "                   scale to the selected logical core count."
	echo "[-nic pcie]:       Network selection: pcie (default) | pcie<rc> |"
	echo "                   pcie<rc>-rp<n> (e.g. pcie1-rp2) | gmac, comma-combinable"
	echo "                   (pcie,gmac = dual-NIC coexist). Plain pcie keeps the"
	echo "                   platform's default RC (net.pcie.enable) okay; naming a"
	echo "                   host (pcie0 / pcie1 / pcie0,pcie1) overrides that and"
	echo "                   puts exactly those declared hosts in okay state — every"
	echo "                   other declared RC still renders, status disabled."
	echo "                   RP selection enables INTx/APLIC only, not the physical"
	echo "                   link; plain pcie leaves RP/RCiEP disabled. gmac = SoC"
	echo "                   peripheral-bus gmac NIC, interrupt via the RCiEP APLIC"
	echo "                   to IMSIC_S (AIA), enabling that pair."
	echo "[-iommu false]:    false | true | all | <instance,...> (e.g. die0-cu1)."
	echo "                   true/all enables every platform-declared IOMMU instance;"
	echo "                   names enable selected instances only (validated against"
	echo "                   the platform). Never enables undeclared instances or RP INTx."
	echo "[-output file]:    Output dts path (default ./.hw.dts)."
	echo "leave the optional args blank, as skip the updates"
	echo
	echo "Platforms (data/platforms/*.json; append -<fpga type> to pick one):"
	# One line per platform json: name + its fpga.types keys and the default,
	# read through the same json structure the generators use (jq first, python3
	# as fallback; without either the section just stays empty).
	pf_list() {
		if command -v jq >/dev/null 2>&1; then
			jq -r '(.fpga.types // {} | keys | join(" ")) as $t |
				(.fpga.default // "") as $d |
				if $t == "" then "" else ($t + "|" + $d) end' "$1" 2>/dev/null || true
		else
			python3 -c 'import json,sys
d=json.load(open(sys.argv[1]))
f=d.get("fpga") or {}
t=f.get("types") or {}
print((" ".join(t)+"|"+(f.get("default") or "")) if t else "")' "$1" 2>/dev/null || true
		fi
	}
	for pf in "${SCRIPT_DIR}"/data/platforms/*.json; do
		[ -f "$pf" ] || continue
		pf_name="$(basename "$pf" .json)"
		pf_info="$(pf_list "$pf")"
		if [ -n "$pf_info" ]; then
			printf '  %-18s fpga types: %s (default: %s)\n' "$pf_name" \
				"${pf_info%|*}" "${pf_info#*|}"
		else
			printf '  %-18s\n' "$pf_name"
		fi
	done
	printf '  %-18s %d CPU models known (pass a wrong -cpu to list them)\n' \
		"" "${#default_isa[@]}"
	echo
	echo "Examples:"
	echo "  # NGF hejian: enable complexes 8,11 -> PCIe rc1 okay with the full"
	echo "  # interrupt-map (rp0-rp3 + RCiEP) and iommu-map, dependencies disabled"
	echo "  ./update_dts_cpu.sh minimesh-hejian -cpu xt-c9251fdvkt -intc aplic \\"
	echo "      -complexes 8,11 -output .hw.dts"
	echo "  # same board, but actually bring up RP0 INTx and the CU1 IOMMU"
	echo "  ./update_dts_cpu.sh minimesh-hejian -cpu xt-c9251fdvkt -intc aplic \\"
	echo "      -complexes 8,11 -nic pcie1-rp0 -iommu die0-cu1"
	echo "  # hejian rc0 is rendered as a disabled, MSI-only host; bring it up too"
	echo "  ./update_dts_cpu.sh minimesh-hejian -cpu xt-c9251fdvkt -intc aplic \\"
	echo "      -complexes 8,11 -nic pcie0,pcie1"
	echo "  # legacy xiaohui SoC: layout bitmap + -timer still applies"
	echo "  ./update_dts_cpu.sh xiaohui_v2 -cpu c908v-cp-xt_v2 -intc aplic \\"
	echo "      -layout 1111 -timer 30000000"
	exit 1
}

LINK_MODE=${LINK_MODE:-none}

# ntrace configs, get value from env
TRACE_CONTEXT=${TRACE_CONTEXT:-"true"}
TRACE_STALL=${TRACE_STALL:-"true"}
TRACE_INHIBIT_SRC=${TRACE_INHIBIT_SRC:-"false"}
TRACE_TRIGGER_ENABLE=${TRACE_TRIGGER_ENABLE:-"true"}
TRACE_TIMESTAMP_ENABLE=${TRACE_TIMESTAMP_ENABLE:-"true"}
TRACE_RUN_IN_DEBUGMODE=${TRACE_RUN_IN_DEBUGMODE:-"false"}
TRACE_SINK_WORKING_MODE=${TRACE_SINK_WORKING_MODE:-"circulating buffer"}

PLATFORM=zsb_rv64
CPU=${2,}
CORE_LAYOUT=1
LAYOUT_SET=0	# -layout 显式传入才置 1; NGF 缺省 layout=1 不透传给生成器
CLUSTERS=
CORES_NUM=
ISA_STRING=default
TIME_FREQ=25000000
INTC=plic
GMAC=h100
NTRACE=false
TRACE_SBA=false
SV=
tmp=1
MEMORY_SIZE=
OPTEE=false
EMU=false
PMC=false
COMPLEXES=
NIC=
NIC_SET=false
IOMMU=false
OUT_DTS=

function update_c930
{
    case "$CPU" in
        *c930*|*c925*)
            echo ">> updating c930/c925"
            GMAC=h100_v2
            ;;
    esac
}

function parse_old_args(){
	if [ $tmp -gt 5 ] ; then
		print_help
	fi
	case $tmp in
	1)
		PLATFORM=$1
	;;
	2)
		CPU=$1
		update_c930
	;;
	3)
		CORE_LAYOUT=$1
		LAYOUT_SET=1
	;;
	4)
		ISA_STRING=$1
	;;
	5)
		TIME_FREQ=$1
	;;
	esac
	tmp=$((tmp + 1))
}

# getopts "hm:p:" opt
while [ "$#" -gt 0 ] ; do
	# Tolerate the GNU-ish --long spelling by normalising --cpu → -cpu etc.
	# before the case runs (in place, so the option is still parsed below).
	if [[ "$1" == --?* ]]; then
		set -- "-${1#--}" "${@:2}"
	fi
	case $1 in
	-h|-help|--help|help)
		print_help
	;;
	-bus)
		LINK_MODE=$2
		echo "LINK_MODE: $LINK_MODE"
		shift
	;;
	-cpu)
		CPU=$2
		echo "CPU: $CPU"
		update_c930
		shift
	;;
	-layout)
		CORE_LAYOUT=$2
		LAYOUT_SET=1
		echo "CORE_LAYOUT: $CORE_LAYOUT"
		shift
	;;
	-clusters)
		CLUSTERS=$2
		echo "CLUSTERS: $CLUSTERS"
		shift
	;;
	-cores_num)
		CORES_NUM=$2
		echo "CORES_NUM: $CORES_NUM"
		shift
	;;
	-isa)
		ISA_STRING=$2
		echo "ISA_STRING: $ISA_STRING"
		shift
	;;
	-timer)
		TIME_FREQ=$2
		echo "TIME_FREQ: $TIME_FREQ"
		shift
	;;
	-intc)
		INTC=$2
		echo "INTC: $INTC"
		shift
	;;
	-sv)
		SV=$2
		echo "SV: $SV"
		shift
	;;
	-gmac)
		if [ "$#" -lt 2 ] || [[ "$2" == -* ]]; then
			echo "Error: -gmac requires h100 | pro (xiaohui gmac controller type);"
			echo "       the gmac NIC on NGF platforms is selected by -nic gmac."
			exit 1
		fi
		GMAC=$2
		echo "GMAC: $GMAC"
		shift
	;;
	-ntrace)
		NTRACE=$2
		echo "NTRACE: $NTRACE"
		case "$NTRACE" in
			true|false) ;;
			*)
				if [ ! -f "$SCRIPT_DIR/data/ntrace/$NTRACE.json" ]; then
					echo "Error: -ntrace accepts false | true | <ntrace-json-name>."
					echo "       'true' auto-selects the config from -cpu; a name must"
					echo "       match data/ntrace/<name>.json used directly (an apb/base"
					echo "       change => add a new json there). Topology comes from"
					echo "       -layout. Available configs:"
					for f in "$SCRIPT_DIR"/data/ntrace/*.json; do
						[ -f "$f" ] && echo "         - $(basename "$f" .json)"
					done
					exit 1
				fi
			;;
		esac
		shift
	;;
	-trace-sba)
		TRACE_SBA=true
		echo "TRACE_SBA: $TRACE_SBA"
	;;
	-memsize)
		MEMORY_SIZE=$2
		echo "MEMORY_SIZE: $MEMORY_SIZE"
		shift
	;;
	-optee)
		OPTEE=$2
		echo "OPTEE: $OPTEE"
		shift
	;;
	-emu)
		EMU=$2
		case "$EMU" in
			true|false) echo "EMU: $EMU" ;;
			*)
				echo "Error: -emu accepts false | true."
				exit 1
			;;
		esac
		shift
	;;
	-pmc)
		PMC=$2
		echo "PMC: $PMC"
		shift
	;;
	-complexes)
		COMPLEXES=$2
		echo "COMPLEXES: $COMPLEXES"
		shift
	;;
	-nic)
		if [ "$#" -lt 2 ] || [[ "$2" == -* ]]; then
			echo "Error: -nic requires pcie | pcie<rc> | pcie<rc>-rp<n> | gmac or a comma-separated combination."
			exit 1
		fi
		NIC=$2
		NIC_SET=true
		echo "NIC: $NIC"
		shift
	;;
	-iommu)
		if [ "$#" -lt 2 ] || [[ "$2" == -* ]]; then
			echo "Error: -iommu requires false | true | all | <instance,...> (e.g. die0-cu1)."
			exit 1
		fi
		IOMMU=$2
		echo "IOMMU: $IOMMU"
		shift
	;;
	-output)
		OUT_DTS=$2
		echo "OUT_DTS: $OUT_DTS"
		shift
	;;
	*)
		# Positional / legacy argument path. Anything that still looks like an
		# option must not silently flow into it: the legacy parser would treat
		# it as <soc_platform>/<cpu>/… and die later with a confusing
		# "key '' does not exist in the list".
		if [[ "$1" == -* ]]; then
			echo "Error: unknown option '$1' (use -h to list all options)"
			print_help
		fi
		parse_old_args $1
	;;
	esac
	shift
done

TMP_DTS=.hw.dts
[ -n "$OUT_DTS" ] && TMP_DTS="$OUT_DTS"

# check if CPU supported
if [ -z ${CPU} ] || [[ -z ${default_isa[$CPU]+_} ]]; then
	echo "Error: The key '$CPU' does not exist in the list"
	echo "suported CPU:"
	for key in "${!default_isa[@]}"; do
		echo $key
	done
	exit 1
fi

if [ "${OPTEE}" = "true" ] ; then
	ZSB_GDBINIT=gdbinit/zsb_rv64_optee.gdbinit
else
	ZSB_GDBINIT=gdbinit/zsb.gdbinit
fi

# Resolve a <soc_platform> name (with or without the -<fpga type> suffix, e.g.
# minimesh-hejian) to the platform JSON that actually carries its blocks —
# the base <platform>.json, since the fpga overlay lives inside it. Silent:
# callers decide what to print. Empty when the name matches no json (the
# generators then auto-select the platform from the CPU model).
resolve_platform_json() {
	local name="$1"
	[ -z "$name" ] && return 0
	if [ -f "${SCRIPT_DIR}/data/platforms/${name}.json" ]; then
		printf '%s\n' "${SCRIPT_DIR}/data/platforms/${name}.json"
	elif [[ "$name" == *-* ]] && \
	     [ -f "${SCRIPT_DIR}/data/platforms/${name%-*}.json" ]; then
		printf '%s\n' "${SCRIPT_DIR}/data/platforms/${name%-*}.json"
	fi
}

# Fixed-SoC platforms (JSON containing a "cpu_block" key; see the schema's
# cpu_block property) take their timebase-frequency from that JSON: generate_dts.py overrides --timer with
# cpu_block.timebase (and with emu.cpu_block.timebase under -emu), so -timer
# has no effect there. Probe the effective value up front to keep the banner
# and the generated DTS telling the same story.
# Probe cpu_block.timebase. Nested-merge semantics (generate_dts.apply_fpga_type
# + the emu overlay) apply the same fields on top of the base block and scalar
# replace wins, so mirroring that with jq --slurp deep-merge is exact. Prefer
# jq; without it fall back to python3; if both are missing the banner just
# prints the -timer value as before (behaviour unchanged, only less chatty).
probe_cpu_timebase() {
	local json="$1" emu="$2"
	if command -v jq >/dev/null 2>&1; then
		jq -r --argjson emu "$([ "$emu" = "true" ] && echo true || echo false)" \
			'[.cpu_block // {}, (.emu.cpu_block // {} | if $emu then . else {} end)]
			 | add | .timebase // empty' \
			"$json" 2>/dev/null || true
	else
		python3 -c 'import json,sys
d=json.load(open(sys.argv[1]))
b=dict(d.get("cpu_block") or {})
if sys.argv[2]=="true": b.update((d.get("emu") or {}).get("cpu_block") or {})
print(b.get("timebase",""))' "$json" "$emu" 2>/dev/null || true
	fi
}

PLAT_JSON_PROBE="$(resolve_platform_json "${PLATFORM_CONFIG:-$PLATFORM}")"
FIXED_SOC=false
if [ -n "$PLAT_JSON_PROBE" ] && grep -q '"cpu_block"' "$PLAT_JSON_PROBE"; then
	FIXED_SOC=true
fi
PLAT_TIMEBASE=
if [ "$FIXED_SOC" = "true" ]; then
	PLAT_TIMEBASE="$(probe_cpu_timebase "$PLAT_JSON_PROBE" "$EMU")"
fi

echo "==== update dts CPU settings ===="
echo "PLATFORM : ${PLATFORM}"
echo "CPU  : ${CPU}"
echo "CORE_LAYOUT : ${CORE_LAYOUT}"
echo "ISA_STRING  : ${ISA_STRING}"
if [ "$FIXED_SOC" = "true" ]; then
	echo "TIME-FREQUENCY  : ${PLAT_TIMEBASE:-$TIME_FREQ}  (platform JSON cpu_block.timebase; -timer not applicable)"
else
	echo "TIME-FREQUENCY  : ${TIME_FREQ}"
fi
echo "LINK_MODE  : ${LINK_MODE}"
echo "OPTEE  : ${OPTEE}"
echo "EMU  : ${EMU}"
echo "==== ==== ==== ==== ==== ===="
echo

# ---------------------------------------------------------------------------
# Thin entry: delegate to the template-driven generators.
#   1. generator/generate_dts.py    — render the full .hw.dts from templates
#   2. generator/render_gdbinit.py  — render gdbinit/zsb.gdbinit for the layout
#   3. generator/render_socinit.py  — render gdbinit/zsb_socinit.gdbinit
# ---------------------------------------------------------------------------

# Resolve the platform JSON override. Precedence: PLATFORM_CONFIG env wins;
# otherwise the <soc_platform> positional is used when it names an existing
# data/platforms/<name>.json (e.g. xiaohui_v1 / xiaohui_v2 / xiaohui_v1_rv32).
# If neither matches a real json, leave it empty so the generators auto-select
# the platform from the CPU model (backward compatible with legacy "xiaohui").
PLATFORM_OVERRIDE="${PLATFORM_CONFIG:-}"
if [ "$PLATFORM" = "emu-hejian" ] || [ "$PLATFORM_OVERRIDE" = "emu-hejian" ]; then
	echo "Error: emu-hejian was removed; select minimesh/fullmesh and pass -emu true."
	exit 1
fi
if [ -z "$PLATFORM_OVERRIDE" ] && [ -n "$PLATFORM" ]; then
	if [ -f "${SCRIPT_DIR}/data/platforms/${PLATFORM}.json" ]; then
		PLATFORM_OVERRIDE="$PLATFORM"
		echo "PLATFORM (json): ${PLATFORM_OVERRIDE}"
	# FPGA-type suffix form <platform>-<ftype> (e.g. minimesh-hejian):
	# accepted when the base <platform>.json exists. The sysmap overlay
	# is applied inside generate_dts.py; gdbinit/socinit read the base
	# json only (their fields carry no FPGA-type differences).
	elif [[ "$PLATFORM" == *-* ]] && \
	     [ -f "${SCRIPT_DIR}/data/platforms/${PLATFORM%-*}.json" ]; then
		PLATFORM_OVERRIDE="$PLATFORM"
		echo "PLATFORM (json): ${PLATFORM_OVERRIDE}"
	fi
fi

# Feature-probe json (cpu_block / ipmc): a suffixed name points at the
# base platform json where all the blocks (incl. the fpga overlays) live.
PLAT_JSON="$(resolve_platform_json "$PLATFORM_OVERRIDE")"

# Build generate_dts.py args from the parsed options.
# --timer is only meaningful on platforms that take the timebase from the CLI;
# fixed-SoC platforms (cpu_block) override it from cpu_block.timebase inside
# generate_dts.py, so not passing it keeps one obvious source of truth (the
# JSON) instead of a silently discarded CLI value.
GEN_ARGS=(--cpu "$CPU"
          --bus "$LINK_MODE" --intc "$INTC" --gmac "$GMAC" --output "$TMP_DTS")
[ "$FIXED_SOC" != "true" ] && GEN_ARGS+=(--timer "$TIME_FREQ")
# -layout 只在显式传入时透传(生成器缺省 "1"); NGF 固定 SoC 平台收到后
# 会把位串换算成 complex/slot 集合(与 -complexes 二选一)。
[ "$LAYOUT_SET" = 1 ] && GEN_ARGS+=(--layout "$CORE_LAYOUT")
# -clusters/-cores_num is an equivalent alternative to -layout; when -cores_num
# is set the generators derive the layout from it (overriding --layout).
[ -n "$CLUSTERS" ] && GEN_ARGS+=(--clusters "$CLUSTERS")
[ -n "$CORES_NUM" ] && GEN_ARGS+=(--cores-num "$CORES_NUM")
[ "$ISA_STRING" != "default" ] && GEN_ARGS+=(--isa "$ISA_STRING")
[ -n "$SV" ] && GEN_ARGS+=(--sv "$SV")
[ "$NTRACE" != "false" ] && GEN_ARGS+=(--ntrace "$NTRACE")
[ "$TRACE_SBA" = "true" ] && GEN_ARGS+=(--trace-sba)
[ -n "$MEMORY_SIZE" ] && GEN_ARGS+=(--memsize "$MEMORY_SIZE")
[ "$OPTEE" = "true" ] && GEN_ARGS+=(--optee)
[ "$EMU" = "true" ] && GEN_ARGS+=(--emu)
[ "$PMC" != "false" ] && GEN_ARGS+=(--pmc "$PMC")
[ -n "$PLATFORM_OVERRIDE" ] && GEN_ARGS+=(--platform "$PLATFORM_OVERRIDE")
[ -n "$COMPLEXES" ] && GEN_ARGS+=(--complexes "$COMPLEXES")
[ "$NIC_SET" = "true" ] && GEN_ARGS+=(--nic "$NIC")
[ "$IOMMU" != "false" ] && GEN_ARGS+=(--iommu "$IOMMU")

# Fixed-SoC platforms (cpu_block): -complexes is passed to the DTS and
# gdbinit renderers; enabled cores and inline IMSIC windows are derived from
# the complex structure/pcu_config without an auxiliary output directory.

# 无 pmc/ipmc 的平台(NGF minimesh/fullmesh)不接受 -pmc:
# 没有 pmc 硬件,per-core release 一律走 pcu_config(socinit 无条件输出)。
if [ "$PMC" != "false" ] && [ -n "$PLAT_JSON" ] && \
   ! grep -q '"ipmc"' "$PLAT_JSON"; then
	echo "Error: platform '${PLATFORM_OVERRIDE}' 无 pmc/ipmc,不支持 -pmc"
	echo "       (per-core release 走 pcu_config,无需 -pmc 门控)"
	exit 1
fi

python3 "${SCRIPT_DIR}/generator/generate_dts.py" "${GEN_ARGS[@]}" || exit 1

# gdbinit: core layout + flag (--optee selects the OP-TEE template);
# 固定 SoC 的核使能由 -complexes/-layout/pcu_config 推导
GDB_ARGS=(--cpu "$CPU" --gdbinit "$ZSB_GDBINIT")
[ "$LAYOUT_SET" = 1 ] && GDB_ARGS+=(--layout "$CORE_LAYOUT")
[ -n "$CLUSTERS" ] && GDB_ARGS+=(--clusters "$CLUSTERS")
[ -n "$CORES_NUM" ] && GDB_ARGS+=(--cores-num "$CORES_NUM")
[ -n "$COMPLEXES" ] && GDB_ARGS+=(--complexes "$COMPLEXES")
[ "$OPTEE" = "true" ] && GDB_ARGS+=(--optee)
[ -n "$PLATFORM_OVERRIDE" ] && GDB_ARGS+=(--platform "$PLATFORM_OVERRIDE")
python3 "${SCRIPT_DIR}/generator/render_gdbinit.py" "${GDB_ARGS[@]}" || exit 1

# socinit: link_mode / net_mode / pmc_init
SOC_ARGS=(--cpu "$CPU" --bus "$LINK_MODE" --gmac "$GMAC")
[ "$EMU" = "true" ] && SOC_ARGS+=(--emu)
[ "$PMC" != "false" ] && SOC_ARGS+=(--pmc)
[ -n "$PLATFORM_OVERRIDE" ] && SOC_ARGS+=(--platform "$PLATFORM_OVERRIDE")
python3 "${SCRIPT_DIR}/generator/render_socinit.py" "${SOC_ARGS[@]}" || exit 1

echo
echo ">> done: ${TMP_DTS}, ${ZSB_GDBINIT}, gdbinit/zsb_socinit.gdbinit"
