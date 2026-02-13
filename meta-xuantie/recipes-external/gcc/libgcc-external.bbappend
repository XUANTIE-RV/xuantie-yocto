copy_files() {
    source_dir="$1"
    target_dir="$2"

    find "$target_dir" -type f -o -type l | while read -r target_file; do
        relative_path="${target_file#$target_dir/}"
        source_file="$source_dir/$relative_path"

        if [ -L "$source_file" ] || [ -e "$source_file" ]; then
            cp -fP "$source_file" "$target_file"
        fi
    done
}

do_install:append() {
    if [ ${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME} != "${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT}" ] || [ ${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME} != "${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME_DEFAULT}" ]; then
        copy_files "${EXTERNAL_TOOLCHAIN_SYSROOT}/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}" "${D}${base_libdir}"
    fi
}

DEPENDS:append:toolchain-clang = " clang-cross-${TARGET_ARCH}"
