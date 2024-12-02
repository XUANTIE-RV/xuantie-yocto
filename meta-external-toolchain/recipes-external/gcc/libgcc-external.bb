SUMMARY = "The GNU Compiler Collection - libgcc"
HOMEPAGE = "http://www.gnu.org/software/gcc/"
SECTION = "devel"
DEPENDS += "virtual/${TARGET_PREFIX}binutils"
PROVIDES += "libgcc-initial"
PV = "${GCC_VERSION}"

inherit external-toolchain

LICENSE = "GPL-3.0-with-GCC-exception"

# libgcc needs libc, but glibc's utilities need libgcc, so short-circuit the
# interdependency here by manually specifying it rather than depending on the
# libc packagedata.
RDEPENDS:${PN} += "${@'${PREFERRED_PROVIDER_virtual/libc}' if '${PREFERRED_PROVIDER_virtual/libc}' else '${TCLIBC}'}"
INSANE_SKIP:${PN} += "build-deps file-rdeps"

# The dynamically loadable files belong to libgcc, since we really don't need the static files
# on the target, moreover linker won't be able to find them there (see original libgcc.bb recipe).
BINV = "${GCC_VERSION}"
FILES:${PN} = "${base_libdir}/libgcc_s.so.*"
LIBROOT_RELATIVE_RESOLVED = "${@os.path.relpath(os.path.realpath('${EXTERNAL_TOOLCHAIN_LIBROOT}'), os.path.realpath('${EXTERNAL_TOOLCHAIN_SYSROOT}'))}"
LIBROOT_RELATIVE = "${@os.path.relpath('${EXTERNAL_TOOLCHAIN_LIBROOT}', '${EXTERNAL_TOOLCHAIN_SYSROOT}')}"
FILES:${PN}-dev = "${base_libdir}/libgcc_s.so \
             /${LIBROOT_RELATIVE_RESOLVED} \
             /${LIBROOT_RELATIVE} \
"
INSANE_SKIP:${PN}-dev += "staticdev"
FILES:${PN}-dbg += "${base_libdir}/.debug/libgcc_s.so.*.debug"

# Follow any symlinks in the libroot (multilib build) to the main
# libroot and include any symlinks there that link to our libroot.
python add_ml_symlink () {
    pass
}
python add_ml_symlink:tcmode-external () {
    import pathlib

    def get_links(p):
        return (c for c in p.iterdir() if c.is_symlink())

    if not d.getVar('EXTERNAL_TOOLCHAIN'):
        return

    libroot = d.getVar('EXTERNAL_TOOLCHAIN_LIBROOT')
    if libroot != 'UNKNOWN':
        sysroot = pathlib.Path(d.getVar('EXTERNAL_TOOLCHAIN_SYSROOT')).resolve()
        libroot = pathlib.Path(libroot)
        for child in get_links(libroot):
            link_dest = child.resolve(strict=True)
            for other_child in get_links(link_dest):
                if other_child.resolve() == libroot.resolve():
                    other = other_child.parent.resolve() / other_child.name
                    relpath = other.relative_to(sysroot)
                    d.appendVar('SYSROOT_DIRS', ' /' + str(relpath.parent))
                    d.appendVar('FILES:${PN}-dev', ' /' + str(relpath))
}
add_ml_symlink[eventmask] = "bb.event.RecipePreFinalise"
addhandler add_ml_symlink

do_install_extra () {
    if [ -e "${D}${libdir}/${EXTERNAL_TARGET_SYS}" ] && [ -z "${MLPREFIX}" ]; then
        if ! [ -e "${D}${libdir}/${TARGET_SYS}" ]; then
            ln -s "${EXTERNAL_TARGET_SYS}" "${D}${libdir}/${TARGET_SYS}"
        fi
    fi

    # This belongs in gcc-runtime
    rm -rf ${D}${libdir}/${TARGET_SYS}/${BINV}/include
}

do_package[prefuncs] += "add_sys_symlink"

python add_sys_symlink () {
    import pathlib
    target_sys = pathlib.Path(d.expand('${D}${libdir}/${TARGET_SYS}'))
    if target_sys.exists():
        pn = d.getVar('PN')
        d.appendVar('FILES:%s-dev' % pn, ' ${libdir}/${TARGET_SYS}')
}
