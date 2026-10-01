#!/bin/bash
# Toolchain Download Script
#
# Usage:
#   bash toolchain-init.sh --vendor xuantie                             # Download ALL XuanTie toolchains
#   bash toolchain-init.sh --vendor riscv-gnu                           # Download ALL open-source toolchains
#   bash toolchain-init.sh --vendor xuantie --kernel 6.6                # Download kernel 6.6 only
#   bash toolchain-init.sh --vendor xuantie --libc muslc64              # Download muslc64 only
#   bash toolchain-init.sh --vendor xuantie --compiler clang            # Download clang only
#   bash toolchain-init.sh --vendor xuantie --kernel all --libc all --compiler all  # Same as default (ALL)
#   bash toolchain-init.sh --vendor xuantie --force                     # Force re-download
#   bash toolchain-init.sh --native-update                              # Update native gcc/clang/gdb bb recipes
#   bash toolchain-init.sh --vendor xuantie --native-update             # Download + update native recipes
#   bash toolchain-init.sh -h                                           # Show help
#
# --libc options:
#   XuanTie:  glibc, muslc64, muslc32, all
#   riscv-gnu: glibc64, glibc32, muslc64, muslc32, uclibc-ng64, uclibc-ng32, all
#
# --compiler options: gcc, clang, all
# --kernel options (xuantie only): 5.10, 6.6, all

set -e

# Use BASH_SOURCE and readlink for reliable path resolution regardless of how script is invoked
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCRIPT_DIR="$(readlink -f "$SCRIPT_DIR")"
TOP_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Set download directory - use absolute path to avoid conflicts when sourced
DOWNLOAD_DIR="${TOP_DIR}/xuantie-build/toolchains-download"

# Create download directory if it does not exist
if [ ! -d "$DOWNLOAD_DIR" ]; then
    mkdir -p "$DOWNLOAD_DIR"
fi

# --- Native bb update (JSON-driven via toolchain_updater.py) ---

update_native_bb_recipes() {
    if [[ "$VENDOR" == "riscv-gnu" ]]; then
        echo "  Skipping native bb update: riscv-gnu has no native bb entries"
        return 0
    fi

    echo ""
    echo "============================================"
    echo " Updating native toolchain bb recipes (JSON-driven)"
    echo "============================================"

    local force_arg=""
    [[ $FORCE -eq 1 ]] && force_arg="--force"
    python3 "$SCRIPT_DIR/toolchain_updater.py" --type bb $force_arg \
        "$SCRIPT_DIR/xuantie_toolchain_url.json"
}

# No defaults for vendor — must be specified
VENDOR=""
KERNEL_VER="all"
LIBC_TYPE="all"
COMPILER_TYPE="all"
FORCE=0
NATIVE_UPDATE=0

usage() {
    echo "Usage: $0 --vendor VENDOR [OPTIONS]"
    echo ""
    echo "Required:"
    echo "  --vendor VENDOR         Toolchain vendor: xuantie or riscv-gnu"
    echo ""
    echo "Optional (default: ALL):"
    echo "  --kernel VER            Kernel version (xuantie only): 6.6, 5.10, all"
    echo "  --libc TYPE             LIBC variant:"
    echo "                          XuanTie:  glibc, muslc64, muslc32, all"
    echo "                          riscv-gnu: glibc64, glibc32, muslc64, muslc32, uclibc-ng64, uclibc-ng32, all"
    echo "  --compiler TYPE         Compiler type: gcc, clang, all"
    echo ""
    echo "Other:"
    echo "  --native-update         Update native gcc/clang/gdb/binutils-tools bb recipes"
    echo "                          (auto-detect versions from deployed toolchain)"
    echo "  --force                 Force re-download even if toolchain already exists"
    echo "  -h                      Show this help message"
    echo ""
    echo "If --kernel/--libc/--compiler are not specified, ALL variants are downloaded."
    echo ""
    exit 0
}

# Parse arguments
while [[ $# -gt 0 ]]; do
    case "$1" in
        --vendor)     VENDOR="$2";       shift 2 ;;
        --kernel)     KERNEL_VER="$2";   shift 2 ;;
        --libc)       LIBC_TYPE="$2";    shift 2 ;;
        --compiler)   COMPILER_TYPE="$2"; shift 2 ;;
        --force)      FORCE=1;           shift ;;
        --native-update) NATIVE_UPDATE=1; shift ;;
        -h|--help)    usage ;;
        *)            echo "Unknown option: $1"; usage ;;
    esac
done

# --native-update can work without --vendor (standalone mode)
if [[ $NATIVE_UPDATE -eq 1 && -z "$VENDOR" ]]; then
    update_native_bb_recipes
    exit 0
fi

# Validate vendor (required for download mode)
if [[ -z "$VENDOR" ]]; then
    echo "Error: --vendor is required. Valid values: xuantie, riscv-gnu"
    exit 1
fi
if [[ "$VENDOR" != "xuantie" && "$VENDOR" != "riscv-gnu" ]]; then
    echo "Error: Invalid vendor '$VENDOR'. Valid values: xuantie, riscv-gnu"
    exit 1
fi

# Validate kernel version (xuantie only, riscv-gnu does not support kernel filter)
if [[ "$VENDOR" == "xuantie" ]] && [[ "$KERNEL_VER" != "5.10" && "$KERNEL_VER" != "6.6" && "$KERNEL_VER" != "all" ]]; then
    echo "Error: Invalid kernel '$KERNEL_VER'. Valid values: 5.10, 6.6, all"
    exit 1
fi
if [[ "$VENDOR" == "riscv-gnu" ]] && [[ "$KERNEL_VER" != "all" ]]; then
    echo "Warning: riscv-gnu toolchain does not use kernel filter, ignoring --kernel $KERNEL_VER"
    KERNEL_VER="all"
fi

# Validate libc type
if [[ "$VENDOR" == "xuantie" ]]; then
    if [[ "$LIBC_TYPE" != "glibc" && "$LIBC_TYPE" != "muslc64" && "$LIBC_TYPE" != "muslc32" && "$LIBC_TYPE" != "all" ]]; then
        echo "Error: Invalid libc '$LIBC_TYPE' for xuantie. Valid: glibc, muslc64, muslc32, all"
        exit 1
    fi
else
    if [[ "$LIBC_TYPE" != "glibc64" && "$LIBC_TYPE" != "glibc32" && "$LIBC_TYPE" != "muslc64" && "$LIBC_TYPE" != "muslc32" && "$LIBC_TYPE" != "uclibc-ng64" && "$LIBC_TYPE" != "uclibc-ng32" && "$LIBC_TYPE" != "all" ]]; then
        echo "Error: Invalid libc '$LIBC_TYPE' for riscv-gnu. Valid: glibc64, glibc32, muslc64, muslc32, uclibc-ng64, uclibc-ng32, all"
        exit 1
    fi
fi

# Validate compiler type
if [[ "$COMPILER_TYPE" != "gcc" && "$COMPILER_TYPE" != "clang" && "$COMPILER_TYPE" != "all" ]]; then
    echo "Error: Invalid compiler '$COMPILER_TYPE'. Valid: gcc, clang, all"
    exit 1
fi

# Build lists
[[ "$KERNEL_VER" == "all" ]] && KERNEL_LIST="6.6 5.10" || KERNEL_LIST="$KERNEL_VER"

if [[ "$LIBC_TYPE" == "all" ]]; then
    [[ "$VENDOR" == "xuantie" ]] && LIBC_LIST="glibc muslc64 muslc32" || LIBC_LIST="glibc64 glibc32 muslc64 muslc32 uclibc-ng64 uclibc-ng32"
else
    LIBC_LIST="$LIBC_TYPE"
fi

[[ "$COMPILER_TYPE" == "all" ]] && COMPILER_LIST="gcc clang" || COMPILER_LIST="$COMPILER_TYPE"

# Select JSON config
[[ "$VENDOR" == "xuantie" ]] && JSON_CONFIG="$SCRIPT_DIR/xuantie_toolchain_url.json" || JSON_CONFIG="$SCRIPT_DIR/riscv_gnu_toolchain_url.json"

echo "============================================"
echo " Toolchain Downloader"
echo " Vendor:    $VENDOR"
echo " Kernel:    $KERNEL_VER"
echo " LIBC:      $LIBC_TYPE"
echo " Compiler:  $COMPILER_TYPE"
[[ $FORCE -eq 1 ]] && echo " Mode:      Force re-download"
echo " Config:    $JSON_CONFIG"
echo "============================================"
echo ""

# Step 1: Download, update conf files, and extract toolchains
echo "Step 1: Downloading and deploying toolchains..."

# Loop over kernel versions
for kv in $KERNEL_LIST; do

echo ""
echo "=== Kernel $kv ==="

# Parse JSON: filter by kernel + libc + compiler
CONF_ENTRIES=$(python3 -c "
import json, sys, os, re
script_dir = '$SCRIPT_DIR'
vendor = '$VENDOR'
kernel_ver = '$kv'
libc_list = '$LIBC_LIST'.split()
compiler_list = '$COMPILER_LIST'.split()
with open('$JSON_CONFIG') as f:
    config = json.load(f)
for item in config.get('updates', []):
    if item['type'] == 'conf':
        for fp in item['file']:
            abs_path = os.path.realpath(os.path.join(script_dir, fp))
            conf_basename = os.path.basename(fp)

            # Determine compiler type from filename
            file_compiler = None
            if conf_basename.startswith('config-gcc'):
                file_compiler = 'gcc'
            elif conf_basename.startswith('config-clang'):
                file_compiler = 'clang'
            if file_compiler and file_compiler not in compiler_list:
                continue

            # Determine libc type from filename
            file_libc = None
            if vendor == 'xuantie':
                if 'muslc64' in conf_basename:
                    file_libc = 'muslc64'
                elif 'muslc32' in conf_basename:
                    file_libc = 'muslc32'
                else:
                    file_libc = 'glibc'
            else:
                if 'glibc64' in conf_basename:
                    file_libc = 'glibc64'
                elif 'glibc32' in conf_basename:
                    file_libc = 'glibc32'
                elif 'muslc64' in conf_basename:
                    file_libc = 'muslc64'
                elif 'muslc32' in conf_basename:
                    file_libc = 'muslc32'
                elif 'uclibc-ng64' in conf_basename:
                    file_libc = 'uclibc-ng64'
                elif 'uclibc-ng32' in conf_basename:
                    file_libc = 'uclibc-ng32'
                else:
                    file_libc = 'glibc64'
            if file_libc and file_libc not in libc_list:
                continue

            # Kernel version filter (xuantie only)
            if vendor == 'xuantie':
                if kernel_ver == '6.6':
                    if re.search(r'-5\.10(?!\d)', conf_basename):
                        continue
                elif kernel_ver == '5.10':
                    if not re.search(r'-5\.10(?!\d)', conf_basename):
                        continue

            print(f'{abs_path}|{item[\"new_url\"]}')
")

[[ -z "$CONF_ENTRIES" ]] && echo "  No matching toolchains for this combination" && continue

echo "$CONF_ENTRIES" | while IFS='|' read -r conf_file url; do
    [[ -z "$conf_file" ]] && continue
    conf_basename=$(basename "$conf_file")
    toolchain_name=$(basename "$url")
    toolchain_path="$DOWNLOAD_DIR/$toolchain_name"

    echo ""
    echo "--- $conf_basename ---"

    # Force re-download or check if already cached
    skip_download=0
    if [[ $FORCE -eq 1 ]]; then
        [[ -f "$toolchain_path" ]] && echo "  Force mode: removing existing file" && rm -f "$toolchain_path"
    elif [[ -f "$toolchain_path" ]]; then
        if [[ -f "$conf_file" ]]; then
            source "$conf_file"
            if [[ -n "${TOOLCHAIN_MD5:-}" ]] && echo "$TOOLCHAIN_MD5  $toolchain_path" | md5sum -c - 2>/dev/null; then
                echo "  Cached file MD5 OK, skipping download"
                skip_download=1
            else
                echo "  Cached file MD5 mismatch or unknown, re-downloading..."
                rm -f "$toolchain_path"
            fi
        fi
    fi

    if [[ $skip_download -eq 0 && ! -f "$toolchain_path" ]]; then
        echo "  Downloading: $url"
        # Ensure download directory exists
        mkdir -p "$(dirname "$toolchain_path")"
        max_retries=2
        retry=0
        download_success=0
        while [[ $retry -lt $max_retries ]]; do
            retry=$((retry + 1))
            [[ $retry -gt 1 ]] && echo "  Retry $retry/$max_retries..."
            if wget -q --show-progress -O "$toolchain_path" "$url" 2>/dev/null; then
                download_success=1
                break
            else
                echo "  WARNING: Download attempt $retry failed"
                rm -f "$toolchain_path"
            fi
        done
        if [[ $download_success -eq 0 ]]; then
            echo "  ERROR: Download failed for $toolchain_name after $max_retries attempts"
            exit 1
        fi
    fi

    # Compute MD5 and update conf file
    COMPUTED_MD5=$(md5sum "$toolchain_path" | awk '{print $1}')
    echo "  MD5: $COMPUTED_MD5"
    printf 'TOOLCHAIN_URL=%s\nTOOLCHAIN_MD5=%s\n' "$url" "$COMPUTED_MD5" > "$conf_file"

    # Determine deployment directory
    if [[ "$VENDOR" == "xuantie" ]]; then
        ksuffix=""
        [[ "$kv" == "5.10" ]] && ksuffix="-k5.10"
        case "$conf_basename" in
            config-gcc-*muslc64*) DEPLOY_DIR="$TOP_DIR/xuantie-toolchain-gcc-muslc${ksuffix}/rv64" ;;
            config-gcc-*muslc32*) DEPLOY_DIR="$TOP_DIR/xuantie-toolchain-gcc-muslc${ksuffix}/rv32" ;;
            config-clang-*)       DEPLOY_DIR="$TOP_DIR/xuantie-toolchain-clang${ksuffix}" ;;
            config-gcc-*)         DEPLOY_DIR="$TOP_DIR/xuantie-toolchain-gcc${ksuffix}" ;;
            *)                    DEPLOY_DIR="$TOP_DIR/xuantie-toolchain-gcc${ksuffix}" ;;
        esac
    else
        case "$conf_basename" in
            config-gcc-*-glibc64)     DEPLOY_DIR="$TOP_DIR/riscv-toolchain-gcc-glibc64" ;;
            config-gcc-*-glibc32)     DEPLOY_DIR="$TOP_DIR/riscv-toolchain-gcc-glibc32" ;;
            config-clang-*-glibc64)   DEPLOY_DIR="$TOP_DIR/riscv-toolchain-clang-glibc64" ;;
            config-clang-*-glibc32)   DEPLOY_DIR="$TOP_DIR/riscv-toolchain-clang-glibc32" ;;
            config-gcc-*-muslc64)     DEPLOY_DIR="$TOP_DIR/riscv-toolchain-gcc-muslc64" ;;
            config-gcc-*-muslc32)     DEPLOY_DIR="$TOP_DIR/riscv-toolchain-gcc-muslc32" ;;
            config-clang-*-muslc64)   DEPLOY_DIR="$TOP_DIR/riscv-toolchain-clang-muslc64" ;;
            config-clang-*-muslc32)   DEPLOY_DIR="$TOP_DIR/riscv-toolchain-clang-muslc32" ;;
            config-gcc-*-uclibc-ng64) DEPLOY_DIR="$TOP_DIR/riscv-toolchain-gcc-uclibc-ng64" ;;
            config-gcc-*-uclibc-ng32) DEPLOY_DIR="$TOP_DIR/riscv-toolchain-gcc-uclibc-ng32" ;;
            *)                        DEPLOY_DIR="$TOP_DIR/riscv-toolchain-gcc" ;;
        esac
    fi

    mkdir -p "$DEPLOY_DIR"

    # Skip extraction if already deployed with same version
    marker_file="$DEPLOY_DIR/.toolchain-version"
    if [[ $FORCE -eq 0 ]] && [[ -f "$marker_file" ]] && grep -qF "$COMPUTED_MD5" "$marker_file" 2>/dev/null; then
        echo "  Already deployed (MD5 match), skipping extraction"
        continue
    fi

    # Extract with retry
    echo "  Extracting to: $DEPLOY_DIR"
    
    max_retries=2
    retry=0
    extract_success=0
    while [[ $retry -lt $max_retries ]]; do
        retry=$((retry + 1))
        [[ $retry -gt 1 ]] && echo "  Retry $retry/$max_retries: re-downloading and extracting..."
        
        # Clean up before retry
        if [[ $retry -gt 1 ]]; then
            rm -f "$toolchain_path"
            rm -rf "${DEPLOY_DIR:?}"/*
            # Re-download
            echo "  Re-downloading: $url"
            if ! wget -q --show-progress -O "$toolchain_path" "$url" 2>/dev/null; then
                echo "  WARNING: Re-download failed, will retry..."
                continue
            fi
            # Recompute MD5
            COMPUTED_MD5=$(md5sum "$toolchain_path" | awk '{print $1}')
            printf 'TOOLCHAIN_URL=%s\nTOOLCHAIN_MD5=%s\n' "$url" "$COMPUTED_MD5" > "$conf_file"
        fi
        
        # Clean deploy dir and extract
        rm -rf "${DEPLOY_DIR:?}"/*
        mkdir -p "$DEPLOY_DIR"
        
        # Auto-detect compression format
        for tar_opt in "-xzf" "-xJf" "-xjf" "-xf"; do
            if tar $tar_opt "$toolchain_path" -C "$DEPLOY_DIR" --strip-components=1 2>/dev/null; then
                extract_success=1
                break 2
            fi
        done
    done
    
    if [[ $extract_success -eq 0 ]]; then
        echo "  ERROR: Extraction failed for $toolchain_name after $max_retries attempts"
        # Clean up failed files
        rm -f "$toolchain_path"
        rm -rf "${DEPLOY_DIR:?}"/*
        exit 1
    fi

    # Create compatibility symlinks
    if [[ -d "$DEPLOY_DIR/bin" ]]; then
        echo "  Creating compatibility symlinks..."
        cd "$DEPLOY_DIR/bin"
        for file in ./*; do
            [[ -f "$file" ]] || continue
            base_name=$(basename -- "$file")
            case "$base_name" in
                riscv64-unknown-linux-gnu-*) ln -sf "$file" "${base_name/riscv64-unknown-linux-gnu/riscv64-linux}" 2>/dev/null || true ;;
                riscv32-unknown-linux-gnu-*) ln -sf "$file" "${base_name/riscv32-unknown-linux-gnu/riscv32-linux}" 2>/dev/null || true ;;
            esac
        done
        cd - > /dev/null
    fi

    # Auto-detect and save toolchain version
    TOOLCHAIN_VER=""
    VER_KEY=""
    case "$conf_basename" in
        config-clang-*)
            echo "  Detecting Clang version..."
            CLANG_BIN="$DEPLOY_DIR/bin/clang"
            if [[ -x "$CLANG_BIN" ]]; then
                TOOLCHAIN_VER=$("$CLANG_BIN" -dumpversion 2>/dev/null)
                if [[ -n "$TOOLCHAIN_VER" ]]; then
                    echo "  Clang version: $TOOLCHAIN_VER"
                    VER_KEY="clang"
                else
                    echo "  WARNING: Failed to detect Clang version"
                fi
            else
                echo "  WARNING: Clang binary not found at $CLANG_BIN"
            fi
            ;;
        *)
            echo "  Detecting GCC version..."
            GCC_BIN=""
            for prefix in riscv64-unknown-linux-gnu  riscv32-unknown-linux-gnu \
                          riscv64-unknown-linux-musl riscv32-unknown-linux-musl; do
                if [[ -x "$DEPLOY_DIR/bin/${prefix}-gcc" ]]; then
                    GCC_BIN="$DEPLOY_DIR/bin/${prefix}-gcc"
                    break
                fi
            done
            if [[ -n "$GCC_BIN" ]]; then
                TOOLCHAIN_VER=$("$GCC_BIN" -dumpversion 2>/dev/null)
                if [[ -n "$TOOLCHAIN_VER" ]]; then
                    echo "  GCC version: $TOOLCHAIN_VER"
                    VER_KEY="gcc"
                else
                    echo "  WARNING: Failed to detect GCC version"
                fi
            else
                echo "  WARNING: GCC binary not found in $DEPLOY_DIR/bin/"
            fi
            ;;
    esac

    # Save toolchain info (include version if detected)
    if [[ -n "$TOOLCHAIN_VER" && -n "$VER_KEY" ]]; then
        echo "$toolchain_name md5=$COMPUTED_MD5 ${VER_KEY}_version=$TOOLCHAIN_VER" > "$marker_file"
        echo "TOOLCHAIN_${VER_KEY^^}_VERSION=$TOOLCHAIN_VER" >> "$marker_file"
    else
        echo "$toolchain_name md5=$COMPUTED_MD5" > "$marker_file"
    fi
    echo "  Done: $toolchain_name -> $DEPLOY_DIR"
done  # end CONF_ENTRIES while

done  # end for kv in KERNEL_LIST

echo ""
echo "============================================"
echo " Download complete: vendor=$VENDOR kernel=$KERNEL_VER libc=$LIBC_TYPE compiler=$COMPILER_TYPE"
echo "============================================"

# Step 1c: Detect & persist open-source toolchain blocklist
# For each present open-source toolchain dir, probe which RISC-V extensions
# the compiler rejects (e.g., smmtt, smsdid, xvqdotq, zalasr, ziccamoc that
# only XuanTie toolchain knows about). Result written to <tc>/.gnu-blocklist
# and consumed by gen-cpu-confs.sh when emitting -gnu.conf via gnu_strict mode.
echo ""
echo "Step 1c: Detecting open-source toolchain extension blocklist..."
# shellcheck source=lib/cpu-confs-lib.sh
source "$SCRIPT_DIR/lib/cpu-confs-lib.sh"
BL_MAPPING_FILE="$TOP_DIR/meta-xuantie/recipes-bsp/hw-c9xx/files/fpga/data/cpu-isa-configs/cpu-isa-mapping.conf"
# XuanTie Clang's RISCVTargetParserDef.inc provides the versioned -march needed
# for open-source Clang to accept experimental extensions. Falls back to the
# unversioned mapping entry when XuanTie Clang isn't present.
BL_XTCLANG_ROOT="$TOP_DIR/xuantie-toolchain-clang"
BL_PROBE_RV64=$(pick_probe_march_from_mapping "$BL_MAPPING_FILE" "$BL_XTCLANG_ROOT" rv64)
BL_PROBE_RV32=$(pick_probe_march_from_mapping "$BL_MAPPING_FILE" "$BL_XTCLANG_ROOT" rv32)
if [[ -z "$BL_PROBE_RV64" && -z "$BL_PROBE_RV32" ]]; then
    echo "  No probe march available (mapping file missing or empty); skipping detection"
else
    # rv64 dirs (gcc + clang)
    if [[ -n "$BL_PROBE_RV64" ]]; then
        for tc_dir in \
            "$TOP_DIR"/riscv-toolchain-gcc-glibc64 \
            "$TOP_DIR"/riscv-toolchain-gcc-muslc64 \
            "$TOP_DIR"/riscv-toolchain-gcc-uclibc-ng64; do
            [[ -d "$tc_dir" ]] || continue
            detect_and_save_blocklist gcc "$tc_dir" "$BL_PROBE_RV64" rv64
        done
        for tc_dir in \
            "$TOP_DIR"/riscv-toolchain-clang-glibc64 \
            "$TOP_DIR"/riscv-toolchain-clang-muslc64; do
            [[ -d "$tc_dir" ]] || continue
            detect_and_save_blocklist clang "$tc_dir" "$BL_PROBE_RV64" rv64
        done
    fi
    # rv32 dirs (gcc + clang)
    if [[ -n "$BL_PROBE_RV32" ]]; then
        for tc_dir in \
            "$TOP_DIR"/riscv-toolchain-gcc-glibc32 \
            "$TOP_DIR"/riscv-toolchain-gcc-muslc32 \
            "$TOP_DIR"/riscv-toolchain-gcc-uclibc-ng32; do
            [[ -d "$tc_dir" ]] || continue
            detect_and_save_blocklist gcc "$tc_dir" "$BL_PROBE_RV32" rv32
        done
        for tc_dir in \
            "$TOP_DIR"/riscv-toolchain-clang-glibc32 \
            "$TOP_DIR"/riscv-toolchain-clang-muslc32; do
            [[ -d "$tc_dir" ]] || continue
            detect_and_save_blocklist clang "$tc_dir" "$BL_PROBE_RV32" rv32
        done
    fi
fi

# Step 2: Generate CPU / profile distro configs from toolchain
echo ""
echo "Step 2: Generating CPU configuration files..."
if [[ "$VENDOR" == "xuantie" ]]; then
    GEN_TC_PATH="$TOP_DIR/xuantie-toolchain-gcc"
else
    GEN_TC_PATH="$TOP_DIR/riscv-toolchain-gcc-glibc64"
fi

GEN_ARGS=""
if [[ -x "$GEN_TC_PATH/bin/riscv64-unknown-linux-gnu-gcc" ]]; then
    GEN_ARGS="--toolchain $GEN_TC_PATH"
fi

# Detect Clang toolchain path
if [[ "$VENDOR" == "xuantie" ]]; then
    CLANG_TC_PATH="$TOP_DIR/xuantie-toolchain-clang"
else
    CLANG_TC_PATH="$TOP_DIR/riscv-toolchain-clang-glibc64"
fi
if [[ -f "$CLANG_TC_PATH/include/llvm/TargetParser/RISCVTargetParserDef.inc" ]]; then
    GEN_ARGS="$GEN_ARGS --clang-toolchain $CLANG_TC_PATH"
fi

if [[ -n "$GEN_ARGS" ]]; then
    $TOP_DIR/meta-xuantie/scripts/gen-cpu-confs.sh $GEN_ARGS
else
    echo "  SKIP: Neither GCC nor Clang toolchain found"
    echo "  Distro configs will not be regenerated."
fi

# Step 2b: Update native bb recipes (if --native-update specified with --vendor)
if [[ $NATIVE_UPDATE -eq 1 ]]; then
    update_native_bb_recipes
fi

# Step 3: Display available build parameters
echo ""
echo "============================================"
echo " Available Build Parameters"
echo "============================================"
echo ""

# --- TOOLCHAIN ---
echo "TOOLCHAIN (工具链 + C 库 + 编译器):"
if [[ "$VENDOR" == "xuantie" ]]; then
    echo "  玄铁工具链:"
    echo "    gcc + glibc (k6.6, default)"
    echo "    gcc + muslc64 (k6.6)"
    echo "    gcc + muslc32 (k6.6)"
    echo "    clang + glibc (k6.6)"
    echo ""
    echo "    gcc + glibc (k5.10)  --kernel 5.10"
    echo "    gcc + muslc64 (k5.10) --kernel 5.10"
    echo "    gcc + muslc32 (k5.10) --kernel 5.10"
    echo "    clang + glibc (k5.10) --kernel 5.10"
    echo ""
    echo "  开源工具链:"
    echo "    gcc + glibc64/glibc32/muslc64/muslc32/uclibc-ng64/uclibc-ng32"
    echo "    clang + glibc64/glibc32/muslc64/muslc32"
    echo ""
    echo "  Download all variants:"
    echo "    bash $0 --compiler all --libc all"
    echo "    bash $0 --kernel all --compiler all --libc all"
else
    echo "  开源工具链:"
    echo "    gcc + glibc64/glibc32/muslc64/muslc32/uclibc-ng64/uclibc-ng32"
    echo "    clang + glibc64/glibc32/muslc64/muslc32"
    echo ""
    echo "  Download all variants:"
    echo "    bash $0 --compiler all --libc all"
fi
echo ""

# --- KERNEL ---
echo "KERNEL (内核版本, 仅玄铁工具链):"
echo "  6.6-default   (default)  Linux 6.6 (uses k6.6 toolchain)"
echo "  6.6-debug                Linux 6.6 + debug config"
echo "  5.10-default             Linux 5.10 (auto-switches to k5.10 toolchain)"
echo "  5.10-debug               Linux 5.10 + debug config"
echo ""

# --- DISTRO ---
DISTRO_DIR="$TOP_DIR/meta-xuantie/conf/distro"
DISTRO_LIST=$(ls "$DISTRO_DIR"/*.conf 2>/dev/null | sed 's|.*/||; s|\.conf$||' | sort)
DISTRO_COUNT=$(echo "$DISTRO_LIST" | wc -l)

echo "DISTRO (目标 ISA / CPU，共 $DISTRO_COUNT 个):"

COMPATIBLE_MODE=$(echo "$DISTRO_LIST" | grep -E '^rv(32|64|a|b).*-k[0-9]' || true)
CPU_MODELS=$(echo "$DISTRO_LIST" | grep -E '^(c|r)[0-9]' | grep -vE '\-k[0-9]' || true)
ARCH_MODE=$(echo "$DISTRO_LIST" | grep -E '^rv(32|64)(gc|imac|imafdc|imafdcv|gc_zfh_xtheadc)' | grep -vE '\-k[0-9]' || true)
PROFILES=$(echo "$DISTRO_LIST" | grep -E '^(rva|rvb)[0-9]' | grep -v '\-k[0-9]' || true)

[[ -n "$COMPATIBLE_MODE" ]] && { CM_TOTAL=$(echo "$COMPATIBLE_MODE" | wc -l); CM_SAMPLE=$(echo "$COMPATIBLE_MODE" | head -4 | tr '\n' ', ' | sed 's/,$//'); echo "  Compatible ($CM_TOTAL): $CM_SAMPLE, ..."; }
[[ -n "$CPU_MODELS" ]] && { CPU_TOTAL=$(echo "$CPU_MODELS" | wc -l); CPU_SAMPLE=$(echo "$CPU_MODELS" | head -8 | tr '\n' ', ' | sed 's/,$//'); echo "  CPU Models ($CPU_TOTAL): $CPU_SAMPLE, ..."; }
if [[ -n "$ARCH_MODE" ]] || [[ -n "$PROFILES" ]]; then
    ALL_ARCH=$(printf '%s\n%s' "$ARCH_MODE" "$PROFILES" | grep -v '^$' | sort)
    ALL_ARCH_TOTAL=$(echo "$ALL_ARCH" | wc -l)
    ALL_ARCH_SAMPLE=$(echo "$ALL_ARCH" | head -10 | tr '\n' ', ' | sed 's/,$//')
    echo "  ARCH ($ALL_ARCH_TOTAL): $ALL_ARCH_SAMPLE, ..."
fi

echo "  (any valid RISC-V ISA string can be used as DISTRO via passthrough)"
echo ""

# --- Example commands ---
echo "============================================"
echo " Quick Start"
echo "============================================"
echo ""
echo "  cd $TOP_DIR"
echo "  source openembedded-core/oe-init-build-env xuantie-build/xuantie"
echo ""

if [[ "$VENDOR" == "xuantie" ]]; then
    echo "  # Basic build (XuanTie GCC + glibc + kernel 6.6)"
    echo "  DISTRO=rv64imafdc_zfh_xtheadc KERNEL=6.6-debug bitbake xuantie-image-lite"
    echo ""
    echo "  # CPU model"
    echo "  DISTRO=c920 bitbake xuantie-image-lite"
    echo ""
    echo "  # musl libc"
    echo "  LIBC=muslc DISTRO=c920 bitbake xuantie-image-lite"
    echo ""
    echo "  # Clang"
    echo "  TOOLCHAIN=xuantie-llvm DISTRO=c920 bitbake xuantie-image-lite"
    echo ""
    echo "  # Kernel 5.10 (auto-switches to k5.10 toolchain)"
    echo "  DISTRO=c920 KERNEL=5.10-default bitbake xuantie-image-lite"
    echo ""
    echo "  Download toolchains:"
    echo "    bash $0                               # gcc + glibc (k6.6)"
    echo "    bash $0 --kernel 5.10                 # gcc + glibc (k5.10)"
    echo "    bash $0 --kernel all                  # gcc + glibc (k6.6 + k5.10)"
    echo "    bash $0 --libc all                    # gcc + glibc/muslc64/muslc32 (k6.6)"
    echo "    bash $0 --compiler all                # gcc/clang + glibc (k6.6)"
    echo "    bash $0 --kernel all --libc all --compiler all  # ALL variants"
else
    echo "  # Basic build (open-source GCC + glibc)"
    echo "  TOOLCHAIN=riscv-gcc DISTRO=rv64gc bitbake xuantie-image-lite"
    echo ""
    echo "  # musl libc"
    echo "  TOOLCHAIN=riscv-gcc LIBC=muslc DISTRO=rv64gc bitbake xuantie-image-lite"
    echo ""
    echo "  # uclibc-ng"
    echo "  TOOLCHAIN=riscv-gcc LIBC=uclibc-ng DISTRO=rv64gc bitbake xuantie-image-lite"
    echo ""
    echo "  # rv32"
    echo "  TOOLCHAIN=riscv-gcc DISTRO=rv32gc bitbake xuantie-image-lite"
    echo ""
    echo "  Download toolchains:"
    echo "    bash $0 --vendor riscv-gnu                              # gcc + glibc64"
    echo "    bash $0 --vendor riscv-gnu --libc glibc32               # gcc + glibc32"
    echo "    bash $0 --vendor riscv-gnu --libc all                   # gcc + all libc variants"
    echo "    bash $0 --vendor riscv-gnu --compiler all --libc all    # gcc/clang + all libc variants"
fi

echo ""
echo "============================================"
