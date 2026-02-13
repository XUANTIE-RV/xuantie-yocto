
FILES:${PN}:append:libc-musl = "${libdir}/charset.alias"
INSANE_SKIP:${PN}:libc-musl = "installed-vs-shipped"
HOST_CC_ARCH:remove:toolchain-clang = "-target ${HOST_SYS}"
