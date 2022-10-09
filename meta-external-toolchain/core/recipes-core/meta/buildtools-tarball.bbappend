# buildtools-tarball is host only, and does not add TOOLCHAIN_TARGET_TASK to
# RDEPENDS. Forcibly empty it, otherwise a TOOLCHAIN_TARGET_TASK_append at the
# config level will break the buildtools-tarball build
python () {
    d.setVar('TOOLCHAIN_TARGET_TASK', '')
}
