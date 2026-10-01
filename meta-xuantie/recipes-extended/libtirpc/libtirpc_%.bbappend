# musl does not provide sys/queue.h; copy it from libbsd into a private
# include directory so that the libbsd overlay headers (stdio.h, stdlib.h
# etc.) are NOT pulled in — those cause infinite #include recursion.
DEPENDS:append:libc-musl = " libbsd"

do_configure:prepend:libc-musl() {
    install -d ${WORKDIR}/musl-compat/sys
    cp ${STAGING_INCDIR}/bsd/sys/queue.h ${WORKDIR}/musl-compat/sys/queue.h
}

CFLAGS:append:libc-musl = " -I${WORKDIR}/musl-compat"

# GCC 15 将无原型的 K&R 函数声明视为 (void)，导致 __getpublickey_LOCAL
# 等函数调用报 "too many arguments" 错误。回退到 C17 标准以保留旧行为。
# 同时存在 -Wincompatible-pointer-types 升级为 error 的问题。
CFLAGS:append = "${@' -std=gnu17 -Wno-error=incompatible-pointer-types' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
