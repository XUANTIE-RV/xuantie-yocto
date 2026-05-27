# musl does not provide sys/queue.h; copy it from libbsd into a private
# include directory so that the libbsd overlay headers (stdio.h, stdlib.h
# etc.) are NOT pulled in — those cause infinite #include recursion.
DEPENDS:append:libc-musl = " libbsd"

do_configure:prepend:libc-musl() {
    install -d ${WORKDIR}/musl-compat/sys
    cp ${STAGING_INCDIR}/bsd/sys/queue.h ${WORKDIR}/musl-compat/sys/queue.h
}

CFLAGS:append:libc-musl = " -I${WORKDIR}/musl-compat"
