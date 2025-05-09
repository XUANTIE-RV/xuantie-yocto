#!/bin/bash

set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
TOP_DIR=$(cd "$SCRIPT_DIR/../../../.." && pwd)
DOWNLOAD_DIR="$TOP_DIR/xuantie-build/downloads"

# Function to create symbolic links
create_link() {
    cd "$TOOLCHAIN_DIR/sysroot/usr/$1"
    array_name="libanl.so.1 libBrokenLocale.so.1 libcrypt.so.1 libdl.so.2 libm.so.6 \
        libnss_compat.so.2 libnss_db.so.2 libnss_dns.so.2 libnss_files.so.2 \
        libnss_hesiod.so.2 libpthread.so.0 libresolv.so.2 librt.so.1 \
        libthread_db.so.1 libutil.so.1"
    for i in $array_name; do
        ln -sf ../../${1}/${i} $(echo $i | awk -F \. '{print $1"."$2}')
    done
    ln -sf ../../${1}/librt.so.1 librt.so.1
}

# GCC-specific symbolic link handling
handle_gcc_specific() {
    cd "$TOOLCHAIN_DIR/bin"
    for file in ./*; do
        if [ -f "$file" ]; then
            base_name=$(basename -- "$file")
            new_base_name="${base_name/riscv64-unknown-linux-gnu/riscv64-linux}"
            ln -s "$file" "$new_base_name"
        fi
    done

    create_link lib64/lp64
    create_link lib64/lp64d
    create_link lib64xthead/lp64
    create_link lib64xthead/lp64d
    create_link lib32/ilp32
    create_link lib32/ilp32d
    create_link lib32xthead/ilp32
    create_link lib32xthead/ilp32d

    cd "$TOOLCHAIN_DIR/sysroot/"
    cp -rfd lib64xthead/lp64d/* lib
    ln -srf lib/ld-2.33.so lib/ld-linux-riscv64-lp64d.so.1
    mv usr/lib64xthead/lp64d usr/lib
    ln -srf usr/lib usr/lib64xthead/lp64d
    mkdir -p usr/libexec32/getconf
    mv usr/libexec/getconf/*32* usr/libexec32/getconf
}

# Setup environment and configuration
setup_environment() {
    # Create directories
    mkdir -p "$DOWNLOAD_DIR"
    mkdir -p "$TOOLCHAIN_DIR"

    # Rename old config file
    if [ -f "$CONFIG_FILE" ]; then
        mv "$CONFIG_FILE" "$HIDDEN_CONFIG"
    fi

    # Check if hidden config exists
    if [ ! -f "$HIDDEN_CONFIG" ]; then
        return 1
    fi
}

# Download and verify toolchain
download_and_verify() {
    local toolchain_path="$1"
    local toolchain_md5="$2"
    local toolchain_url="$3"

    # Check existing file
    if [[ -f "$toolchain_path" ]]; then
        echo "Existing toolchain detected, verifying MD5"
        if echo "$toolchain_md5  $toolchain_path" | md5sum -c -; then
            echo "MD5 verification passed"
            return 0
        else
            echo "MD5 verification failed, re-downloading"
            rm -f "$toolchain_path"
        fi
    fi

    # Download if needed
    if [[ ! -f "$toolchain_path" ]]; then
        echo "Downloading toolchain to $DOWNLOAD_DIR"
        wget -O "$toolchain_path" "$toolchain_url" || {
            echo "Download failed"
            return 1
        }
        echo "Download completed"
    fi

    # Final MD5 check
    echo "Final MD5 verification"
    if ! echo "$toolchain_md5  $toolchain_path" | md5sum -c -; then
        echo "MD5 verification failed"
        rm -f "$toolchain_path"
        return 1
    fi
}

# Extract and setup toolchain
extract_toolchain() {
    local toolchain_path="$1"
    local target_dir="$2"

    echo "Extracting toolchain to $target_dir"
    rm -rf "$target_dir"/*
    tar -xf "$toolchain_path" -C "$target_dir" --strip-components=1 || {
        echo "Extraction failed"
        return 1
    }
    echo "Extraction completed"
}

# Update version marker
update_version_marker() {
    local marker_file="$1"
    local toolchain_name="$2"
    echo "This riscv-toolchain is from '$toolchain_name'" > "$marker_file"
}

# Process both GCC and Clang
for TOOLCHAIN_TYPE in gcc clang; do
    TOOLCHAIN_DIR="$TOP_DIR/riscv-toolchain-$TOOLCHAIN_TYPE"
    MARKER_FILE="$TOOLCHAIN_DIR/README.md"
    CONFIG_FILE="$TOOLCHAIN_DIR/config"
    HIDDEN_CONFIG="$TOOLCHAIN_DIR/.config"

    # Setup environment
    setup_environment || continue

    # Source configuration
    source "$HIDDEN_CONFIG"
    TOOLCHAIN_NAME=$(basename "$TOOLCHAIN_URL")
    TOOLCHAIN_PATH="$DOWNLOAD_DIR/$TOOLCHAIN_NAME"

    # Version check
    if [[ -f "$MARKER_FILE" ]]; then
        if grep -q "$TOOLCHAIN_NAME" "$MARKER_FILE"; then
            continue
        else
            echo "Updating $TOOLCHAIN_TYPE to new version ($TOOLCHAIN_NAME)"
            rm -rf "$TOOLCHAIN_DIR"/*
        fi
    fi

    # Download and verify
    echo "Processing $TOOLCHAIN_TYPE..."
    download_and_verify "$TOOLCHAIN_PATH" "$TOOLCHAIN_MD5" "$TOOLCHAIN_URL"

    # Extract toolchain
    extract_toolchain "$TOOLCHAIN_PATH" "$TOOLCHAIN_DIR"

    # Apply GCC-specific handling
    if [ "$TOOLCHAIN_TYPE" = "gcc" ]; then
        handle_gcc_specific
    fi

    # Update marker
    update_version_marker "$MARKER_FILE" "$TOOLCHAIN_NAME"
    echo "Configuration completed for $TOOLCHAIN_TYPE"
done
