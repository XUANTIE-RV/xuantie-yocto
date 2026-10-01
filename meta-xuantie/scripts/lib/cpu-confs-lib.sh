# Shared helpers for gen-cpu-confs.sh and audit-cpu-mapping.sh
#
# This file is meant to be sourced, not executed directly.
#
# Provided functions:
#   query_gcc_march      <cpu>                 -> versioned march from GCC (via readelf Tag_RISCV_arch)
#   query_gcc_mtune      <cpu>                 -> mtune used by GCC
#   query_clang_march    <cpu>                 -> march from Clang RISCVTargetParserDef.inc
#   query_clang_cpu_list                       -> all CPU names known to Clang
#   query_march          <cpu>                 -> GCC preferred, Clang fallback (both versioned)
#   strip_arch_versions  <march>               -> drop NpM version suffixes, GCC-style
#   normalize_march      <march> <mode>        -> mode=xthead|gnu|gnu_strict
#   derive_mabi          <march> <arch>        -> arch=rv32|rv64; returns ilp32(d)/lp64(d)
#   detect_gnu_blocklist <gcc_path> <clang_path> <probe_march>
#                                              -> extensions either tool rejects
#
# Required env vars (set by the sourcing script):
#   GCC_BIN                  Path to xuantie GCC binary
#   GCC_READELF_BIN          Path to readelf matching GCC_BIN (same toolchain prefix)
#   GCC_AVAILABLE            0 or 1
#   CLANG_TOOLCHAIN_PATH     Path to xuantie Clang root (for RISCVTargetParserDef.inc)
#   CLANG_AVAILABLE          0 or 1
#   GNU_BLOCKLIST            (optional) space-separated list, set by detect_gnu_blocklist
#                            and consumed by normalize_march mode=gnu_strict

# Static fallback used when neither open-source GCC nor Clang is available
# at detection time. Keep in sync with extensions known to be xuantie-only.
KNOWN_GNU_BLOCKLIST_FALLBACK="smmtt smsdid xvqdotq zalasr ziccamoc"

# Helper: query GCC for the versioned march string used with a given -mcpu.
# Compiles a tiny probe to .o, then reads Tag_RISCV_arch from .riscv.attributes
# via readelf. Produces version-suffixed form (e.g. rv64i2p1_m2p0_..._xthead..1p0),
# which -S -v parsing alone cannot recover.
query_gcc_march() {
    local model="$1"
    if [ "${GCC_AVAILABLE:-0}" -eq 0 ]; then return; fi
    if [ -z "${GCC_READELF_BIN:-}" ] || [ ! -x "$GCC_READELF_BIN" ]; then return; fi
    local probe_o
    probe_o=$(mktemp --suffix=.o) || return
    if ! echo 'int x;' | "$GCC_BIN" -mcpu="$model" -c -x c - -o "$probe_o" 2>/dev/null; then
        rm -f "$probe_o"
        return
    fi
    "$GCC_READELF_BIN" -A "$probe_o" 2>/dev/null \
        | awk -F'"' '/Tag_RISCV_arch:/ {print $2; exit}'
    rm -f "$probe_o"
}

# Helper: decide whether GCC outright rejects a -march/-mabi pair.
# Returns 0 (and prints the comma-separated offending extension names) only when
# a trial compile fails with a -march diagnostic. Detection must use a compile:
# `gcc -march=<bad> -print-sysroot` exits 0 and writes the error to stderr, which
# is exactly how a bad march leaks into meta-external-toolchain as a bogus sysroot.
# Fail-open (non-zero) when GCC is unavailable, the probe compiles, or the failure
# is unrelated to -march.
gcc_rejects_march() {
    local march="$1" mabi="$2"
    if [ "${GCC_AVAILABLE:-0}" -eq 0 ] || [ -z "$march" ]; then return 1; fi
    local src err
    src=$(mktemp --suffix=.c) || return 1
    err="${src}.err"
    echo 'int x;' > "$src"
    if "$GCC_BIN" -march="$march" -mabi="$mabi" -c "$src" -o /dev/null 2>"$err"; then
        rm -f "$src" "$err"
        return 1
    fi
    # Only an "unsupported extension inside -march" diagnostic counts. An ABI
    # mismatch also mentions -march (e.g. "error: ABI requires '-march=rv32'")
    # but names no extension, so requiring both the `error: '-march=` prefix and
    # an extractable extension name keeps this from firing on the wrong thing.
    local exts=""
    if grep -q "error: '-march=" "$err"; then
        exts=$(sed -n "s/.*extension '\([^']*\)'.*/\1/p" "$err" | sort -u | paste -sd, -)
    fi
    rm -f "$src" "$err"
    [ -z "$exts" ] && return 1
    echo "$exts"
    return 0
}

# Helper: query GCC for the mtune used with a given -mcpu
query_gcc_mtune() {
    local model="$1"
    if [ "${GCC_AVAILABLE:-0}" -eq 0 ]; then return; fi
    local output
    output=$(echo 'int x;' | "$GCC_BIN" -mcpu="$model" -S -v -o /dev/null -x c - 2>&1) || true
    echo "$output" | awk '/-mtune=/ {match($0, /-mtune=[^ '"'"'"]*/, a); if(a[0]) {print substr(a[0], 8); exit}}'
}

# Helper: query Clang for the march string used with a given -mcpu
# Parses RISCVTargetParserDef.inc to extract DEFAULT_MARCH for a CPU name.
query_clang_march() {
    local model="$1"
    local inc_file="$CLANG_TOOLCHAIN_PATH/include/llvm/TargetParser/RISCVTargetParserDef.inc"
    if [ ! -f "$inc_file" ]; then
        return
    fi
    grep -oP 'PROC\([^,]+,\s*\{"'"$model"'"\},\s*\{"[^"]*"\}' "$inc_file" 2>/dev/null \
        | grep -oP '\{"[^"]*"\}\s*$' \
        | tr -d '{}"' || true
}

# Helper: extract all CPU names from Clang's RISCVTargetParserDef.inc
query_clang_cpu_list() {
    local inc_file="$CLANG_TOOLCHAIN_PATH/include/llvm/TargetParser/RISCVTargetParserDef.inc"
    if [ ! -f "$inc_file" ]; then
        return
    fi
    grep -oP 'PROC\([^,]+,\s*\{"\K[^"]+' "$inc_file"
}

# Unified march query: prefer GCC (versioned via readelf), fallback to Clang
# parser def. CPUs unknown to both are dropped by callers.
query_march() {
    local model="$1"
    local result=""
    if [ "${GCC_AVAILABLE:-0}" -eq 1 ]; then
        result=$(query_gcc_march "$model")
    fi
    if [ -z "$result" ] && [ "${CLANG_AVAILABLE:-0}" -eq 1 ]; then
        result=$(query_clang_march "$model")
    fi
    echo "$result"
}

# Helper: strip version numbers from arch string and merge single-letter extensions
strip_arch_versions() {
    python3 -c "
import sys, re
march = sys.argv[1]
parts = march.split('_')
stripped = [re.sub(r'\d+p\d+$', '', p) for p in parts]
base = stripped[0]
prefix = re.match(r'^rv\d+', base).group()
base_letters = base[len(prefix):]
single_letters = []
multi_exts = []
for ext in stripped[1:]:
    if len(ext) == 1 and ext.isalpha():
        single_letters.append(ext)
    else:
        multi_exts.append(ext)
all_base = base_letters + ''.join(single_letters)
canon_order = 'iemafdqcbvph'
sorted_base = ''.join(c for c in canon_order if c in all_base)
sorted_base += ''.join(c for c in all_base if c not in canon_order)
result = prefix + sorted_base
if multi_exts:
    result += '_' + '_'.join(multi_exts)
print(result)
" "$1"
}

# Helper: normalize march string
# mode=xthead     drop xtheadXXX, draft (zfbfmin/zvfbfwma) kept, decompose b
# mode=gnu        drop xtheadXXX + draft + implicit
# mode=gnu_strict mode=gnu + drop everything in $GNU_BLOCKLIST (env var)
#
# Auto-detects two input formats and preserves the format on output:
#   1. ELF versioned form:   rv64i2p1_m2p0_a2p1_f2p2_..._zicfilp1p0
#                            (every token after the base carries an NpM suffix;
#                            single-letter exts like m/a/f/d/c/b/v/h are split
#                            out from the base by GCC). Output keeps versions.
#   2. Compact unversioned:  rv64imafdcv_xthead..._zfh
#                            Legacy form from cpu-isa-mapping.conf. Output is
#                            unversioned as before.
normalize_march() {
    local march="$1"
    local mode="$2"
    local blocklist="${GNU_BLOCKLIST:-}"
    python3 -c "
import sys, re
march = sys.argv[1].strip(\"'\")
mode = sys.argv[2]
blocklist = set(sys.argv[3].split()) if sys.argv[3] else set()

def strip_ver(ext):
    return re.sub(r'\d+p\d+$', '', ext)

exts = march.split('_')
base = exts[0]
rest = exts[1:]

# Detect ELF versioned format: any rest token that is a single-letter
# extension carrying an NpM version suffix (e.g. m2p0, f2p2, b1p0).
versioned_input = any(re.match(r'^[a-z]\d+p\d+$', e) for e in rest)

implicit = {'zmmul', 'zaamo', 'zalrsc', 'zfhmin',
            'zve32f', 'zve32x', 'zve64d', 'zve64f', 'zve64x',
            'zvl128b', 'zvl32b', 'zvl64b'}
draft = {'zfbfmin', 'zvfbfwma'}
# Privileged extensions invalid in user-mode -march
privileged_prefixes = ('ss', 'sv', 'sh', 'sm', 'sd', 'rva2')

if versioned_input:
    # Keep version suffixes on output; filter using stripped names.
    rest = [e for e in rest
            if not strip_ver(e).startswith('xthead')
            and not strip_ver(e).startswith('xxtcsr')
            and not strip_ver(e).startswith(privileged_prefixes)]
    rest = [e for e in rest if strip_ver(e) not in implicit]
    if mode in ('gnu', 'gnu_strict'):
        rest = [e for e in rest if strip_ver(e) not in draft]
    if mode == 'gnu_strict' and blocklist:
        rest = [e for e in rest if strip_ver(e) not in blocklist]
    # B extension: GCC's ELF form already decomposes into zba/zbb/zbc/zbs,
    # so a bare 'b' token is redundant when those are present. Drop it.
    has_zb = any(strip_ver(e) in ('zba', 'zbb', 'zbc', 'zbs') for e in rest)
    if has_zb:
        rest = [e for e in rest if strip_ver(e) != 'b']
    # Canonical RISC-V extension ordering (required by Clang):
    #   single-letter standard extensions follow CANONICAL_ORDER
    #   (i, e, m, a, f, d, q, c, b, v, p, h);
    #   multi-letter extensions (z*, s*, x*) come after, sorted alphabetically.
    CANONICAL_ORDER = 'iemafdqcbvph'
    single = [e for e in rest if len(strip_ver(e)) == 1]
    multi  = [e for e in rest if len(strip_ver(e)) > 1]
    def _single_key(e):
        n = strip_ver(e)
        try:
            return CANONICAL_ORDER.index(n)
        except ValueError:
            return len(CANONICAL_ORDER)
    single.sort(key=_single_key)
    multi.sort()
    rest = single + multi
    print(base + ('_' + '_'.join(rest) if rest else ''))
else:
    # Legacy unversioned input (e.g. from cpu-isa-mapping.conf).
    rest = [e for e in rest
            if not e.startswith('xthead') and not e.startswith('xxtcsr')
            and not e.startswith(privileged_prefixes)]
    rest = [e for e in rest if strip_ver(e) not in implicit]
    if mode in ('gnu', 'gnu_strict'):
        rest = [e for e in rest if strip_ver(e) not in draft]
    if mode == 'gnu_strict' and blocklist:
        rest = [e for e in rest if strip_ver(e) not in blocklist]
    has_b = 'b' in base[4:] or any(strip_ver(e) == 'b' for e in rest)
    if has_b:
        base = base.replace('b', '')
        rest = [e for e in rest if strip_ver(e) != 'b']
        for ext in ['zba', 'zbb', 'zbc', 'zbs']:
            if not any(strip_ver(e) == ext for e in rest):
                rest.append(ext)
    rest.sort()
    print(base + ('_' + '_'.join(rest) if rest else ''))
" "$march" "$mode" "$blocklist"
}

# Helper: derive RISC-V mabi from a march string and rv32/rv64 arch tag.
# Returns one of: ilp32, ilp32d, lp64, lp64d.
# Float ABI is selected when march has any of f/d/g, either as single letters
# in the base ISA (rv64imafdc, rv32gc) or as separate versioned extensions
# (rv64i2p1_..._f2p2_d2p2_...).
derive_mabi() {
    local march="$1"
    local arch="$2"
    local base="${march%%_*}"
    local has_float=false
    if [[ "$base" == *f* ]] || [[ "$base" == *d* ]] || [[ "$base" == *g* ]]; then
        has_float=true
    elif [[ "$march" =~ _f[0-9]*p[0-9]* ]] || [[ "$march" =~ _d[0-9]*p[0-9]* ]] || [[ "$march" =~ _g[0-9]*p[0-9]* ]]; then
        has_float=true
    fi
    if [ "$arch" = "rv32" ]; then
        $has_float && echo "ilp32d" || echo "ilp32"
    else
        $has_float && echo "lp64d" || echo "lp64"
    fi
}

# Detect which extensions a candidate open-source GNU/Clang toolchain rejects
# by iteratively trying to compile a probe march and stripping the failing
# extension reported in the diagnostic.
#
# Args:
#   $1 = open-source GCC root  (must contain bin/riscv{32,64}-*-gcc)
#   $2 = open-source Clang root (must contain bin/clang)
#   $3 = probe march string with version numbers (Clang format preferred);
#        the rv32/rv64 prefix in this string drives mabi/target selection
#        unless $4 overrides
#   $4 = (optional) arch: 'rv32' or 'rv64'. When set, overrides auto-detection
#        from the probe march prefix. Defaults to detection from $3 (or rv64
#        if the probe doesn't start with rv32/rv64).
#
# Echoes a space-separated list of rejected extension *names* (no version
# suffix). Returns 0 even when one or both toolchains are missing — caller
# decides how to fall back.
detect_gnu_blocklist() {
    local gcc_root="$1"
    local clang_root="$2"
    local probe_march="$3"
    local arch="${4:-}"

    if [ -z "$probe_march" ]; then
        echo "WARNING: detect_gnu_blocklist: empty probe march, returning fallback list" >&2
        echo "$KNOWN_GNU_BLOCKLIST_FALLBACK"
        return 0
    fi

    # Determine arch (rv32 vs rv64) — explicit arg wins, else infer from probe
    if [ -z "$arch" ]; then
        case "$probe_march" in
            rv32*) arch="rv32" ;;
            *)     arch="rv64" ;;
        esac
    fi
    local bits mabi target
    if [ "$arch" = "rv32" ]; then
        bits=32 ; mabi="ilp32d" ; target="riscv32-linux-gnu"
    else
        bits=64 ; mabi="lp64d"  ; target="riscv64-linux-gnu"
    fi

    # Pick binaries (arch-aware: riscv${bits}-*-gcc)
    local gcc_bin=""
    if [ -d "$gcc_root" ]; then
        gcc_bin="$(find "$gcc_root/bin" -maxdepth 1 -name "riscv${bits}-*-gcc" 2>/dev/null | head -1)"
    fi
    local clang_bin=""
    if [ -d "$clang_root" ] && [ -x "$clang_root/bin/clang" ]; then
        clang_bin="$clang_root/bin/clang"
    fi

    if [ -z "$gcc_bin" ] && [ -z "$clang_bin" ]; then
        echo "WARNING: detect_gnu_blocklist: no open-source toolchain available, using static fallback: $KNOWN_GNU_BLOCKLIST_FALLBACK" >&2
        echo "$KNOWN_GNU_BLOCKLIST_FALLBACK"
        return 0
    fi

    local probe_c
    probe_c="$(mktemp --suffix=.c)"
    echo 'int main(void){return 0;}' > "$probe_c"
    local probe_o="${probe_c%.c}.o"

    local -A rejected=()
    local tc_label tc_invoke tc_root
    for tc_label in gcc clang; do
        if [ "$tc_label" = "gcc" ]; then
            [ -z "$gcc_bin" ] && continue
            tc_invoke="$gcc_bin"
            tc_root="$gcc_root"
        else
            [ -z "$clang_bin" ] && continue
            tc_invoke="$clang_bin --target=$target -menable-experimental-extensions"
            tc_root="$clang_root"
        fi

        local cur_march="$probe_march"
        local i
        for i in $(seq 1 64); do
            local err
            err=$($tc_invoke -march="$cur_march" -mabi="$mabi" -c "$probe_c" -o "$probe_o" 2>&1) || true
            # Success: empty output means accepted
            if [ -z "$err" ]; then
                break
            fi
            # Pull first failing extension name from diagnostic.
            # GCC: "extension 'NAME' starts with..." or "unknown extension 'NAME'"
            # Clang variants:
            #   "unsupported (non-standard|standard) ... extension 'NAME'"
            #   "experimental extension 'NAME'"
            #   "experimental extension requires explicit version number `NAME`"
            #     (backticks, NOT single quotes; emitted when arch passes parse
            #      but a token needs a version suffix)
            local ext
            ext=$(echo "$err" | grep -oP "extension '\K[^']+" | head -1)
            if [ -z "$ext" ]; then
                # Try Clang backtick form ("version number `name`")
                ext=$(echo "$err" | grep -oP "version number \`\K[^\`]+" | head -1)
            fi
            if [ -z "$ext" ]; then
                # Couldn't parse; bail out for this toolchain
                echo "WARNING: detect_gnu_blocklist: $tc_label parse failed: $(echo "$err" | head -1)" >&2
                break
            fi
            rejected[$ext]=1
            # Strip the extension (with any version suffix) from cur_march
            cur_march=$(echo "$cur_march" | sed -E "s/_${ext}[0-9]+p[0-9]+//")
        done
    done

    rm -f "$probe_c" "$probe_o"

    if [ ${#rejected[@]} -eq 0 ]; then
        # Both toolchains accepted full march — emit empty list
        echo ""
    else
        # Sort for deterministic output
        printf '%s\n' "${!rejected[@]}" | sort | tr '\n' ' ' | sed 's/ $//'
    fi
}

# Pick a probe march string for detect_gnu_blocklist.
#
# Args:
#   $1 = cpu-isa-mapping.conf path (source of CPU coverage)
#   $2 = (optional) XuanTie Clang root — if its RISCVTargetParserDef.inc is
#        present, used to look up the versioned -march for the richest CPU.
#        Clang requires version numbers for experimental extensions; GCC
#        accepts versioned strings transparently.
#   $3 = (optional) arch filter: 'rv32' or 'rv64'. When set, only mapping
#        entries whose march starts with that prefix are considered. Default
#        is no filter (any arch).
#
# Algorithm:
#   1. Scan mapping for entry-line (`default_isa[cpu]="..."`) with longest
#      march, optionally filtered by arch prefix.
#   2. Extract the CPU name; if XuanTie Clang available, query its parser def
#      for the versioned march.
#   3. Fallback: return the unversioned mapping entry (GCC-only detection).
pick_probe_march_from_mapping() {
    local mapping_file="$1"
    local clang_root="${2:-}"
    local arch_filter="${3:-}"
    [ -f "$mapping_file" ] || return 0

    # 1: find CPU with richest march (filter header/comment lines via grep ^).
    # If arch_filter is set, restrict to entries whose ISA string begins with it.
    local pattern='^default_isa\['
    if [ -n "$arch_filter" ]; then
        pattern='^default_isa\[[^]]*\]="'"$arch_filter"
    fi
    local longest_line
    # `grep -E` exits 1 on no matches; under callers' `set -euo pipefail` that
    # would abort before the `-z` check below can return cleanly.
    longest_line=$({ grep -E "$pattern" "$mapping_file" || true; } \
        | awk '{print length, $0}' | sort -nr | head -1 | cut -d' ' -f2-)
    [ -z "$longest_line" ] && return 0
    local cpu unversioned
    cpu=$(echo "$longest_line" | sed -n 's/^default_isa\[\([^]]*\)\].*/\1/p')
    unversioned=$(echo "$longest_line" | sed -n 's/^default_isa\[[^]]*\]="\([^"]*\)".*/\1/p')

    # 2+3: prefer versioned form from XuanTie Clang
    if [ -n "$clang_root" ] && [ -f "$clang_root/include/llvm/TargetParser/RISCVTargetParserDef.inc" ]; then
        local versioned
        versioned=$(CLANG_TOOLCHAIN_PATH="$clang_root" query_clang_march "$cpu")
        if [ -n "$versioned" ]; then
            echo "$versioned"
            return 0
        fi
    fi
    echo "$unversioned"
}

# Detect blocklist for a single open-source toolchain dir and persist to
# <tc_dir>/.gnu-blocklist. Idempotent: skips work if header MD5 matches the
# current toolchain binary MD5 (and probe march unchanged).
#
# Args:
#   $1 = kind: 'gcc' or 'clang'
#   $2 = toolchain dir (must contain bin/clang or bin/riscv{32,64}-*-gcc)
#   $3 = probe march string (rv32* or rv64* — drives binary and mabi selection)
#   $4 = (optional) arch: 'rv32' or 'rv64'. Default: inferred from probe prefix.
#
# Writes:
#   <tc_dir>/.gnu-blocklist with:
#     # Auto-generated by toolchain-init.sh
#     # Arch: rv32|rv64
#     # Toolchain MD5: <md5>
#     # Probe: <probe>
#     BLOCKLIST="ext1 ext2 ..."
detect_and_save_blocklist() {
    local kind="$1"
    local tc_dir="$2"
    local probe="$3"
    local arch="${4:-}"
    [ -d "$tc_dir" ] || { echo "  [blocklist] skip: $tc_dir not a dir" >&2; return 0; }

    if [ -z "$arch" ]; then
        case "$probe" in
            rv32*) arch="rv32" ;;
            *)     arch="rv64" ;;
        esac
    fi
    local bits=64
    [ "$arch" = "rv32" ] && bits=32

    local bin=""
    if [ "$kind" = "clang" ]; then
        bin="$tc_dir/bin/clang"
    else
        bin=$(find "$tc_dir/bin" -maxdepth 1 -name "riscv${bits}-*-gcc" 2>/dev/null | head -1)
    fi
    if [ -z "$bin" ] || [ ! -x "$bin" ]; then
        echo "  [blocklist] skip: no $kind ($arch) binary in $tc_dir/bin" >&2
        return 0
    fi

    local out="$tc_dir/.gnu-blocklist"
    local cur_md5
    cur_md5=$(md5sum "$bin" | awk '{print $1}')

    if [ -f "$out" ]; then
        local saved_md5 saved_probe
        saved_md5=$(awk -F': ' '/^# Toolchain MD5:/ {print $2; exit}' "$out")
        saved_probe=$(awk -F': ' '/^# Probe:/ {sub(/^# Probe: /,""); print; exit}' "$out")
        if [ "$saved_md5" = "$cur_md5" ] && [ "$saved_probe" = "$probe" ]; then
            echo "  [blocklist] $tc_dir: up-to-date, skip"
            return 0
        fi
    fi

    echo "  [blocklist] probing $kind ($arch): $tc_dir"
    local bl=""
    if [ "$kind" = "clang" ]; then
        bl=$(detect_gnu_blocklist "" "$tc_dir" "$probe" "$arch")
    else
        bl=$(detect_gnu_blocklist "$tc_dir" "" "$probe" "$arch")
    fi
    {
        echo "# Auto-generated by toolchain-init.sh"
        echo "# Date: $(date -u +%FT%TZ)"
        echo "# Arch: $arch"
        echo "# Toolchain MD5: $cur_md5"
        echo "# Probe: $probe"
        echo "BLOCKLIST=\"$bl\""
    } > "$out"
    echo "  [blocklist] $tc_dir: $bl"
}

# Read .gnu-blocklist files from a list of toolchain dirs and echo the
# space-separated union (deduplicated, sorted). Missing files are silently
# skipped.
load_union_blocklist() {
    local out=""
    local d bl_file bl
    for d in "$@"; do
        bl_file="$d/.gnu-blocklist"
        [ -f "$bl_file" ] || continue
        bl=$(awk -F= '/^BLOCKLIST=/ {gsub(/"/, "", $2); print $2; exit}' "$bl_file")
        out="$out $bl"
    done
    # `grep -v '^$'` exits 1 when union is empty; tolerate so callers under
    # `set -euo pipefail` don't abort on a legitimately empty blocklist.
    echo "$out" | tr ' ' '\n' | { grep -v '^$' || true; } | sort -u | tr '\n' ' ' | sed 's/ $//'
}
