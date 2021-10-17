PV = "${GCC_VERSION}"
BINV = "${GCC_VERSION}"

require recipes-devtools/gcc/gcc-runtime.inc
inherit external-toolchain

# GCC >4.2 is GPLv3
DEPENDS = "libgcc"
EXTRA_OECONF = ""
COMPILERDEP = ""

python () {
    lic_deps = d.getVarFlag('do_populate_lic', 'depends', False)
    d.setVarFlag('do_populate_lic', 'depends', lic_deps.replace('gcc-source-${PV}:do_unpack', ''))
    cfg_deps = d.getVarFlag('do_configure', 'depends', False)
    d.setVarFlag('do_configure', 'depends', cfg_deps.replace('gcc-source-${PV}:do_preconfigure', ''))
}

target_libdir = "${libdir}"
external_libroot = "${@os.path.realpath('${EXTERNAL_TOOLCHAIN_LIBROOT}').replace(os.path.realpath('${EXTERNAL_TOOLCHAIN}') + '/', '/')}"
FILES_MIRRORS =. "\
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/|${external_libroot}/\n \
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/include/|/lib/gcc/${EXTERNAL_TARGET_SYS}/${BINV}/include/ \n \
    ${libdir}/gcc/${TARGET_SYS}/|${libdir}/gcc/${EXTERNAL_TARGET_SYS}/\n \
    ${@'${includedir}/c\+\+/${GCC_VERSION}/${TARGET_SYS}/|${includedir}/c++/${GCC_VERSION}/${EXTERNAL_TARGET_SYS}${EXTERNAL_HEADERS_MULTILIB_SUFFIX}/\n' if d.getVar('EXTERNAL_HEADERS_MULTILIB_SUFFIX') != 'UNKNOWN' else ''} \
    ${includedir}/c\+\+/${GCC_VERSION}/${TARGET_SYS}/|${includedir}/c++/${GCC_VERSION}/${EXTERNAL_TARGET_SYS}/\n \
"

# The do_install_append in gcc-runtime.inc doesn't do well if the links
# already exist, as it causes a recursion that breaks traversal.
python () {
    adjusted = d.getVar('do_install_appended').replace('ln -s', 'link_if_no_dest')
    adjusted = adjusted.replace('mkdir', 'mkdir_if_no_dest')
    d.setVar('do_install_appended', adjusted)
}

link_if_no_dest () {
    if ! [ -e "$2" ] && ! [ -L "$2" ]; then
        ln -s "$1" "$2"
    fi
}

mkdir_if_no_dest () {
    if ! [ -e "$1" ] && ! [ -L "$1" ]; then
        mkdir "$1"
    fi
}

do_install_extra () {
    if [ "${TARGET_SYS}" != "${EXTERNAL_TARGET_SYS}" ] && [ -z "${MLPREFIX}" ]; then
        if [ -e "${D}${includedir}/c++/${GCC_VERSION}/${EXTERNAL_TARGET_SYS}" ]; then
            if ! [ -e "${D}${includedir}/c++/${GCC_VERSION}/${TARGET_SYS}" ]; then
                ln -s ${EXTERNAL_TARGET_SYS} ${D}${includedir}/c++/${GCC_VERSION}/${TARGET_SYS}
            fi
        fi
    fi

    # Clear out the unused c++ header multilibs
    multilib="${EXTERNAL_HEADERS_MULTILIB_SUFFIX}"
    if [ "$multilib" != "UNKNOWN" ]; then
        for path in ${D}${includedir}/c++/${GCC_VERSION}/${TARGET_SYS}/*; do
            case ${path##*/} in
                ${multilib#/})
                    mv -v "$path/"* "${D}${includedir}/c++/${GCC_VERSION}/${TARGET_SYS}/"
                    ;;
            esac
            rm -rfv "$path"
        done
    fi
}

FILES_${PN}-dbg += "${datadir}/gdb/python/libstdcxx"
FILES_libstdc++-dev = "\
    ${includedir}/c++ \
    ${libdir}/libstdc++.so \
    ${libdir}/libstdc++.la \
    ${libdir}/libsupc++.la \
"
FILES_libgomp-dev += "\
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/include/openacc.h \
"
BBCLASSEXTEND = ""

# gcc-runtime needs libc, but glibc's utilities need libssp in some cases, so
# short-circuit the interdependency here by manually specifying it rather than
# depending on the libc packagedata.
libc_rdep = "${@'${PREFERRED_PROVIDER_virtual/libc}' if '${PREFERRED_PROVIDER_virtual/libc}' else '${TCLIBC}'}"
RDEPENDS_libgomp += "${libc_rdep}"
RDEPENDS_libssp += "${libc_rdep}"
RDEPENDS_libstdc++ += "${libc_rdep}"
RDEPENDS_libatomic += "${libc_rdep}"
RDEPENDS_libquadmath += "${libc_rdep}"
RDEPENDS_libmpx += "${libc_rdep}"

do_package_write_ipk[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_deb[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_rpm[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
