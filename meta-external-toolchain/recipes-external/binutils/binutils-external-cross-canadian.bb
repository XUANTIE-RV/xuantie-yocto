require recipes-external/binutils/binutils-external.inc
inherit external-toolchain-cross-canadian

PN .= "-${TRANSLATED_TARGET_ARCH}"

FILES:${PN} = "\
    ${@' '.join('${bindir}/${EXTERNAL_TARGET_SYS}-' + i for i in '${binutils_binaries}'.split())} \
    ${@' '.join('${exec_prefix}/${EXTERNAL_TARGET_SYS}/bin/' + i for i in '${binutils_binaries}'.split())} \
    ${exec_prefix}/lib/ldscripts \
"

do_install:append () {
    if [ ! -e ${D}${bindir}/${EXTERNAL_TARGET_SYS}-ld.bfd ]; then
		rm -rf ${D}${bindir}/${EXTERNAL_TARGET_SYS}-ld.bfd
		echo "ln -s ${EXTERNAL_TARGET_SYS}-ld ${D}${bindir}/${EXTERNAL_TARGET_SYS}-ld.bfd"
        ln -s ${EXTERNAL_TARGET_SYS}-ld ${D}${bindir}/${EXTERNAL_TARGET_SYS}-ld.bfd
    fi
}
