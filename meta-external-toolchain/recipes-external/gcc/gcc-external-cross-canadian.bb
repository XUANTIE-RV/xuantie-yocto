require recipes-external/gcc/gcc-external.inc
inherit external-toolchain-cross-canadian

PN .= "-${TRANSLATED_TARGET_ARCH}"

BINV = "${GCC_VERSION}"

RDEPENDS_${PN} = "binutils-external-cross-canadian-${TRANSLATED_TARGET_ARCH}"
FILES_${PN} = "\
    ${libdir}/gcc/${EXTERNAL_TARGET_SYS}/${BINV} \
    ${libexecdir}/gcc/${EXTERNAL_TARGET_SYS}/${BINV} \
    ${libdir}/libcc1* \
    ${@' '.join('${base_bindir}/${EXTERNAL_TARGET_SYS}-' + i for i in '${gcc_binaries}'.split())} \
"

external_libroot = "${@os.path.realpath('${EXTERNAL_TOOLCHAIN_LIBROOT}').replace(os.path.realpath('${EXTERNAL_TOOLCHAIN}') + '/', '/')}"
FILES_MIRRORS =. "${libdir}/gcc/${EXTERNAL_TARGET_SYS}/${BINV}/|${external_libroot}/\n"

INSANE_SKIP_${PN} += "dev-so staticdev"
