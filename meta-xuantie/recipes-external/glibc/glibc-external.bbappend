INHIBIT_PACKAGE_STRIP_FILES += "${PKGD}${base_libdir}/ld-2.33.so ${PKGD}${base_libdir}/libpthread-2.33.so"

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
    if [ ${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME} != "lib64xthead" ] || [ ${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME} != "lp64d" ]; then
        if [ ${TUNE_ARCH} = "riscv32" ]; then
            copy_files "${EXTERNAL_TOOLCHAIN_SYSROOT}/sbin32" "${D}${base_sbindir}"
            copy_files "${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/sbin32" "${D}${sbindir}"
            copy_files "${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/bin32" "${D}${bindir}"
            rm -rf ${D}${libexecdir}/*
            cp -rfP ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/libexec32/* ${D}${libexecdir}
        fi
        copy_files "${EXTERNAL_TOOLCHAIN_SYSROOT}/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}" "${D}${base_libdir}"
        copy_files "${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}" "${D}${libdir}"
        rm ${D}${base_libdir}/ld-linux-*
        if [ ${TUNE_ARCH} != "riscv32" ]; then
            ln -sr ${D}${base_libdir}/ld-2.33.so ${D}${base_libdir}/ld-linux-${TUNE_ARCH}-lp64d.so.1
            ln -sr ${D}${base_libdir}/ld-2.33.so ${D}${base_libdir}/ld-linux-${TUNE_ARCH}-lp64.so.1
        else
            ln -sr ${D}${base_libdir}/ld-2.33.so ${D}${base_libdir}/ld-linux-${TUNE_ARCH}-ilp32d.so.1
            ln -sr ${D}${base_libdir}/ld-2.33.so ${D}${base_libdir}/ld-linux-${TUNE_ARCH}-ilp32.so.1
        fi
        glibc_external_do_install_extra
    fi
    rm -rf ${D}/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME} ${D}${prefix}/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}
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
