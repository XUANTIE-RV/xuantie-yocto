# musl does not provide sys/queue.h; copy it from libbsd into a local compat dir.
DEPENDS:append:libc-musl = " libbsd"
CFLAGS:append:libc-musl = " -I${B}/musl-compat"

do_configure:prepend:libc-musl() {
    mkdir -p ${B}/musl-compat/sys
    cp ${STAGING_INCDIR}/bsd/sys/queue.h ${B}/musl-compat/sys/queue.h
}
