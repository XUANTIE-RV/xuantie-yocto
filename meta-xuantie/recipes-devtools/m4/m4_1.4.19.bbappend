
FILES:${PN}:append:libc-musl = "${libdir}/charset.alias"
INSANE_SKIP:${PN}:libc-musl = "installed-vs-shipped"
HOST_CC_ARCH:remove:toolchain-clang = "-target ${HOST_SYS}"

# GCC 15 (C23): m4 1.4.19 gnulib 使用了与 C23 不兼容的旧式语法
CFLAGS:append = "${@' -std=gnu17' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
