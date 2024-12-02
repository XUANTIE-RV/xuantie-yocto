inherit external-toolchain cross-canadian

# Toolchain binaries are expected to run on both this host and SDKMACHINE, so
# we should be able to use host tools.
STRIP:task-package = "strip"
STRIP:task-populate-sysroot = "strip"
OBJCOPY:task-package = "objcopy"
PACKAGE_DEPENDS:remove = "virtual/${TARGET_PREFIX}binutils"

# Scan just toolchains root directory, but include some additional mirrors
EXTERNAL_INSTALL_SOURCE_PATHS = "${EXTERNAL_TOOLCHAIN}"
FILES_MIRRORS += "\
    ${bindir}/|/bin/\n \
    ${libdir}/|/lib/\n \
    ${libexecdir}/|/libexec/\n \
    ${prefix}/|${target_prefix}/\n \
    ${prefix}/|${target_base_prefix}/\n \
    ${exec_prefix}/|${target_exec_prefix}/\n \
    ${exec_prefix}/|${target_base_prefix}/\n \
    ${base_prefix}/|${target_base_prefix}/\n \
"

# Align with more typical toolchain layout. Everything is already isolated by
# EXTERNAL_TARGET_SYS, we don't need cross-canadian.bbclass to do it for us.
bindir = "${exec_prefix}/bin"
libdir = "${exec_prefix}/lib"
libexecdir = "${exec_prefix}/libexec"

# We're relying on a compatible host libc, not one from a nativesdk build
INSANE_SKIP:${PN} += "build-deps file-rdeps"

do_install:append () {
    for i in ${D}${bindir}/${EXTERNAL_TARGET_SYS}-*; do
        if [ -e "$i" ]; then
            j="$(basename "$i")"
            #ln -sv "$j" "${D}${bindir}/${TARGET_PREFIX}${j#${EXTERNAL_TARGET_SYS}-}"
        fi
    done
}

python add_files_links () {
    prefix = d.getVar('EXTERNAL_TARGET_SYS') + '-'
    full_prefix = os.path.join(d.getVar('bindir'), prefix)
    new_prefix = d.getVar('TARGET_PREFIX')
    for pkg in d.getVar('PACKAGES').split():
        files = (d.getVar('FILES:%s' % pkg) or '').split()
        new_files = []
        for f in files:
            if f.startswith(full_prefix):
                new_files.append(f.replace(prefix, new_prefix))
        if new_files:
            d.appendVar('FILES:%s' % pkg, ' ' + ' '.join(new_files))
}
do_package[prefuncs] += "add_files_links"
