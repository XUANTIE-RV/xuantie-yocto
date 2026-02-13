FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:riscv32 = " \
    file://bin32 \
"

INHIBIT_PACKAGE_STRIP_FILES += "${PKGD}${base_libdir}/ld-${PV}.so ${PKGD}${base_libdir}/libpthread-${PV}.so"

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
    if [ "${@bb.utils.contains('MACHINE_FEATURES', 'toolchain-multilib', 'true', 'false', d)}" = "false" ] \
    && ( [ "${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}" != "${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT}" ] \
            || [ "${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}" != "${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME_DEFAULT}" ] ); then
        if [ ${TUNE_ARCH} = "riscv32" ]; then
            copy_files "${WORKDIR}/bin32/${base_sbindir}" "${D}${base_sbindir}"
            copy_files "${WORKDIR}/bin32/${sbindir}" "${D}${sbindir}"
            copy_files "${WORKDIR}/bin32/${bindir}" "${D}${bindir}"
        fi
        copy_files "${EXTERNAL_TOOLCHAIN_SYSROOT}/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}" "${D}${base_libdir}"
        copy_files "${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}" "${D}${libdir}"

    fi
    if [ -L "${D}${base_libdir}/ld-linux-${TUNE_ARCH}-${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}.so.1" ]; then
        rm -f ${D}${base_libdir}/ld-linux-${TUNE_ARCH}-${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}.so.1
        ln -sr ${D}${base_libdir}/ld-${PV}.so ${D}${base_libdir}/ld-linux-${TUNE_ARCH}-${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}.so.1
    fi
    if [ "${TUNE_ARCH}" = "riscv64" ]; then
        rm -rf ${D}/usr/libexec/getconf/*32* ${D}${base_libdir}/ld-linux-*32-*
    else
        rm -rf ${D}/usr/libexec/getconf/*64* ${D}${base_libdir}/ld-linux-*64-*
    fi
    rm -rf ${D}/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME} ${D}${prefix}/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}
    if [ -d "${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/lib32xthead/" ] && \
        [ -d "${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/lib64xthead/" ]; then
        cd ${D}${libdir}
        for link in *.so; do
            [ -L $link ] || continue
            current_target=$(readlink "$link")
            case "$current_target" in
                ../../../*)
                    new_target="../../${current_target#../../../}"
                    ln -sf "$new_target" "$link"
                    echo "Updated: $link -> $new_target"
                    ;;
                *)
                    ;;
            esac
        done
        cd -
    fi
    glibc_external_do_install_extra
}

python do_package:append() {
    bb.utils.mkdirhier(pkgdest + '/' + pn + '/' + d.getVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME'))
    os.symlink("../lib", pkgdest + '/' + pn + '/' + d.getVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME') + '/' + d.getVar('EXTERNAL_TOOLCHAIN_ABI_DIR_NAME'))
    bb.utils.mkdirhier(pkgdest + '/' + pn + '/usr/' + d.getVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME'))
    os.symlink("../lib", pkgdest + '/' + pn + '/usr/' + d.getVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME') + '/' + d.getVar('EXTERNAL_TOOLCHAIN_ABI_DIR_NAME'))
}

FILES:${PN}:append = " /${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME} ${prefix}/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}"

SYSROOT_PREPROCESS_FUNCS:append = " external_toolchain_sysroot_adjust"

external_toolchain_sysroot_adjust() {
	install -d ${SYSROOT_DESTDIR}/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/
	ln -s ../lib ${SYSROOT_DESTDIR}/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}

	install -d ${SYSROOT_DESTDIR}/usr/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/
	ln -s ../lib ${SYSROOT_DESTDIR}/usr/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}
}

DEPENDS:append:toolchain-clang = " clang-cross-${TARGET_ARCH}"

FILES:${PN}-staticdev:append = " ${libdir}/libxtstring.a ${libdir}/libXTString.a ${base_libdir}/libclang_rt.builtins-${TUNE_ARCH}.a"
