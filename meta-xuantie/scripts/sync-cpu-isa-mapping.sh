#!/bin/bash
# sync-cpu-isa-mapping.sh
#
# Refresh cpu-isa-mapping.conf from the current GCC toolchain.
#
# Modes
#   additive (default): preserve manual edits.
#     - Per-CPU ISA: union of existing tokens and GCC-reported tokens
#       (extensions only added, never removed).
#     - CPU set: keep CPUs already in the mapping even if absent from GCC.
#   strict (--strict):  full rebuild — produce mapping purely from GCC.
#     - Equivalent to the legacy behavior. May DROP manual edits.
#
# Workflow
#   1. Query GCC for the supported -mcpu= list (parses `gcc --target-help`)
#   2. For each CPU, derive the versioned -march via a probe compile +
#      readelf Tag_RISCV_arch, then strip version suffixes so the result
#      matches the format already stored in cpu-isa-mapping.conf
#   3. Merge with existing mapping (additive) or replace (strict)
#   4. Diff against the existing mapping file (unified diff)
#   5. If different, prompt the caller before replacing it
#
# Heavy lifting (query_gcc_march, strip_arch_versions, CPU filter rule) is
# shared with gen-cpu-confs.sh through lib/cpu-confs-lib.sh so the two stay
# in sync.
#
# Usage
#   bash meta-xuantie/scripts/sync-cpu-isa-mapping.sh
#   bash meta-xuantie/scripts/sync-cpu-isa-mapping.sh --gcc /path/to/gcc \
#       --mapping /path/to/cpu-isa-mapping.conf
#   bash meta-xuantie/scripts/sync-cpu-isa-mapping.sh --yes      # non-interactive
#   bash meta-xuantie/scripts/sync-cpu-isa-mapping.sh --dry-run  # diff only
#   bash meta-xuantie/scripts/sync-cpu-isa-mapping.sh --strict   # full rebuild
#                                                                  (drops manual edits)

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOP_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
META_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

DEFAULT_MAPPING="$META_DIR/recipes-bsp/hw-c9xx/files/fpga/data/cpu-isa-configs/cpu-isa-mapping.conf"

GCC_BIN_ARG=""
MAPPING_FILE="$DEFAULT_MAPPING"
ASSUME_YES=0
DRY_RUN=0
STRICT_MODE=0

usage() {
    cat <<EOF
Usage: $0 [OPTIONS]

Options:
  --gcc PATH         Path to riscv*-gcc binary. Auto-detected if omitted.
  --mapping PATH     cpu-isa-mapping.conf to diff against / replace.
                     Default: $DEFAULT_MAPPING
  --yes              Replace without prompting if diff is non-empty.
  --dry-run          Print diff and exit; never modify the mapping file.
  --strict           Full rebuild from GCC alone; may drop manual edits.
                     Default mode is additive (merge with existing entries).
  -h, --help         Show this help.
EOF
    exit "${1:-0}"
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --gcc)     GCC_BIN_ARG="$2";  shift 2 ;;
        --mapping) MAPPING_FILE="$2"; shift 2 ;;
        --yes)     ASSUME_YES=1;      shift ;;
        --dry-run) DRY_RUN=1;         shift ;;
        --strict)  STRICT_MODE=1;     shift ;;
        -h|--help) usage 0 ;;
        *) echo "Unknown option: $1" >&2; usage 1 ;;
    esac
done

# --- Locate GCC -------------------------------------------------------------

# Auto-detect a usable GCC when --gcc is not given. The XuanTie GCC bundle
# (xuantie-toolchain-gcc/, kernel 6.6) is required for the XuanTie -mcpu names
# (c906, c907, xt-c930, ...) — the upstream riscv-collab bundles do not know
# them and would produce an empty CPU list. k5.10 sits behind 6.6 because
# newer GCCs report richer per-CPU ISA descriptions.
auto_detect_gcc() {
    local candidates=(
        "$TOP_DIR/xuantie-toolchain-gcc/bin/riscv64-unknown-linux-gnu-gcc"
        "$TOP_DIR/xuantie-toolchain-gcc-k5.10/bin/riscv64-unknown-linux-gnu-gcc"
    )
    for c in "${candidates[@]}"; do
        if [ -x "$c" ]; then
            echo "$c"
            return 0
        fi
    done
    return 1
}

if [ -n "$GCC_BIN_ARG" ]; then
    GCC_BIN="$GCC_BIN_ARG"
else
    GCC_BIN="$(auto_detect_gcc || true)"
fi

if [ -z "${GCC_BIN:-}" ] || [ ! -x "$GCC_BIN" ]; then
    echo "ERROR: GCC not found. Pass --gcc <path>, or install a toolchain via" >&2
    echo "       meta-xuantie/scripts/toolchain-init.sh --vendor riscv-gnu ..." >&2
    exit 1
fi

# Resolve the matching readelf next to GCC (Tag_RISCV_arch lookup).
GCC_READELF_BIN="${GCC_BIN%-gcc}-readelf"
if [ ! -x "$GCC_READELF_BIN" ]; then
    echo "ERROR: readelf not found alongside GCC ($GCC_READELF_BIN)" >&2
    exit 1
fi

# Expose env vars expected by cpu-confs-lib.sh helpers and source the library.
export GCC_BIN GCC_READELF_BIN
export GCC_AVAILABLE=1
export CLANG_AVAILABLE=0
export CLANG_TOOLCHAIN_PATH=""
# shellcheck disable=SC1091
. "$SCRIPT_DIR/lib/cpu-confs-lib.sh"

# --- Locate mapping file ---------------------------------------------------

if [ ! -f "$MAPPING_FILE" ]; then
    echo "ERROR: mapping file not found: $MAPPING_FILE" >&2
    exit 1
fi

echo "GCC:     $GCC_BIN"
echo "Mapping: $MAPPING_FILE"
echo

# --- Step 1: GCC -> CPU list ----------------------------------------------

# Mirrors the filter from gen-cpu-confs.sh so the comparison is apples-to-apples.
extract_filtered_cpu_list() {
    "$GCC_BIN" --target-help 2>&1 \
        | grep -A1 'Known valid arguments for -mcpu=' \
        | tail -1 \
        | tr ' ' '\n' \
        | sort -u \
        | grep -v '^$' \
        | grep -E '^(c|r|xt-)[cr]?[0-9]' \
        | grep -v -E '^(sifive|thead|xiangshan)' \
        | grep -v -E '^e[0-9]' \
        | grep -v -E '^(zhijiang|c920v2\.c908v)' \
        | grep -v -E '^r908a' || true
}

FILTERED_CPUS="$(extract_filtered_cpu_list)"
if [ -z "$FILTERED_CPUS" ]; then
    echo "ERROR: GCC --target-help returned no XuanTie CPUs." >&2
    echo "       The selected toolchain is probably the open-source riscv-collab" >&2
    echo "       build that does not know XuanTie -mcpu names. Use the XuanTie" >&2
    echo "       GCC bundle (TOOLCHAIN_VENDOR=xuantie) instead." >&2
    exit 1
fi

CPU_COUNT=$(echo "$FILTERED_CPUS" | wc -l)
echo "Discovered $CPU_COUNT CPU models from GCC --target-help"

# --- Step 2: per-CPU ISA via readelf probe --------------------------------

declare -A NEW_ISA
SKIPPED=()
for cpu in $FILTERED_CPUS; do
    raw="$(query_gcc_march "$cpu" || true)"
    if [ -z "$raw" ]; then
        SKIPPED+=("$cpu")
        continue
    fi
    # Versioned strings (rv64i2p1_..._xthead..1p0) -> compact form to match
    # the canonical layout already stored in cpu-isa-mapping.conf.
    arch="$(strip_arch_versions "$raw")"
    NEW_ISA["$cpu"]="$arch"
done

if [ ${#SKIPPED[@]} -gt 0 ]; then
    echo "WARNING: skipped ${#SKIPPED[@]} CPU(s) (probe compile / readelf failed):"
    printf '  - %s\n' "${SKIPPED[@]}"
fi

# --- Step 3: merge with existing mapping ----------------------------------

# Token-level union of two ISA strings.
# Format is `<base>_<ext1>_<ext2>_...` where <base> is the architecture
# letters (rv64imafdcb...) and the rest are independent extension tokens.
# Returns: existing string verbatim + any GCC-reported tokens not already
# present, appended in the order they appear in the GCC string. This keeps
# manual edits stable and only ever adds tokens.
merge_isa_tokens() {
    local existing="$1"
    local incoming="$2"
    local -a existing_tokens incoming_tokens
    IFS='_' read -ra existing_tokens <<< "$existing"
    IFS='_' read -ra incoming_tokens <<< "$incoming"

    # Warn (not fail) if the base ISA differs — extremely rare for stable CPUs;
    # keep existing base since manual edits may have introduced a deliberate change.
    if [[ "${existing_tokens[0]}" != "${incoming_tokens[0]}" ]]; then
        echo "  [warn] base ISA differs (existing='${existing_tokens[0]}' incoming='${incoming_tokens[0]}'); keeping existing" >&2
    fi

    local -A seen=()
    local t
    for t in "${existing_tokens[@]:1}"; do
        seen["$t"]=1
    done

    local merged="$existing"
    for t in "${incoming_tokens[@]:1}"; do
        if [[ -z "${seen[$t]:-}" ]]; then
            merged="${merged}_${t}"
            seen["$t"]=1
        fi
    done
    printf '%s' "$merged"
}

# Build MERGED_ISA according to mode:
#   strict   → MERGED_ISA = NEW_ISA (legacy behavior; may drop manual edits)
#   additive → MERGED_ISA = existing ∪ NEW_ISA, per-CPU token-union for shared CPUs
declare -A MERGED_ISA=()

if [[ "$STRICT_MODE" -eq 1 ]]; then
    echo "Mode:    strict (full rebuild from GCC; manual edits will be dropped)"
    for cpu in "${!NEW_ISA[@]}"; do
        MERGED_ISA["$cpu"]="${NEW_ISA[$cpu]}"
    done
else
    echo "Mode:    additive (preserve manual edits; pass --strict for full rebuild)"
    # Source existing mapping into a temporary array.
    declare -A default_isa=()
    # shellcheck disable=SC1090
    . "$MAPPING_FILE"
    for cpu in "${!default_isa[@]}"; do
        MERGED_ISA["$cpu"]="${default_isa[$cpu]}"
    done
    # Merge GCC-reported entries on top.
    for cpu in "${!NEW_ISA[@]}"; do
        if [[ -n "${MERGED_ISA[$cpu]:-}" ]]; then
            MERGED_ISA["$cpu"]="$(merge_isa_tokens "${MERGED_ISA[$cpu]}" "${NEW_ISA[$cpu]}")"
        else
            MERGED_ISA["$cpu"]="${NEW_ISA[$cpu]}"
        fi
    done
    # Free the sourced array so it can't be confused with anything later.
    unset default_isa
fi

# --- Step 3b: render proposed mapping file --------------------------------

TMP_NEW="$(mktemp)"
trap 'rm -f "$TMP_NEW"' EXIT

if [[ "$STRICT_MODE" -eq 1 ]]; then
    HEADER_NOTE="# Auto-generated by sync-cpu-isa-mapping.sh (--strict) — do not edit manually"
else
    HEADER_NOTE="# Auto-merged by sync-cpu-isa-mapping.sh (additive mode) — manual edits preserved"
fi

{
    echo "# CPU model to ISA string mapping for XuanTie processors."
    echo "$HEADER_NOTE"
    echo "# Generated from toolchain: GCC: $GCC_BIN"
    echo "# Format: default_isa[cpu]=\"isa_string\" (bash associative array, can be sourced directly)"
    echo
    for cpu in $(printf '%s\n' "${!MERGED_ISA[@]}" | sort); do
        echo "default_isa[${cpu}]=\"${MERGED_ISA[$cpu]}\""
    done
} > "$TMP_NEW"

# --- Step 4: diff and prompt ----------------------------------------------

# Only compare the actual default_isa[...] entries. Comment headers
# (generator name, toolchain path, format note) and blank separators are
# intentionally ignored — otherwise a fresh run looks "different" just
# because the generator field changed.
diff_payload() {
    grep '^default_isa\[' "$1" | sort
}

if diff -q <(diff_payload "$MAPPING_FILE") <(diff_payload "$TMP_NEW") > /dev/null; then
    echo
    echo "No changes — mapping file already in sync with GCC."
    exit 0
fi

echo
echo "=== Diff (existing -> proposed) ==="
diff -u <(diff_payload "$MAPPING_FILE") <(diff_payload "$TMP_NEW") || true
echo "=== end diff ==="

if [ "$DRY_RUN" -eq 1 ]; then
    echo
    echo "--dry-run set; not modifying $MAPPING_FILE"
    exit 0
fi

if [ "$ASSUME_YES" -ne 1 ]; then
    echo
    read -r -p "Replace $MAPPING_FILE with the proposed mapping? [y/N] " reply
    case "$reply" in
        y|Y|yes|YES) ;;
        *) echo "Aborted. No changes written."; exit 0 ;;
    esac
fi

# Backup the previous mapping so an accidental sync can be rolled back.
backup="${MAPPING_FILE}.bak.$(date +%Y%m%d-%H%M%S)"
cp -a "$MAPPING_FILE" "$backup"
cp "$TMP_NEW" "$MAPPING_FILE"
echo "Replaced $MAPPING_FILE"
echo "Backup:   $backup"
