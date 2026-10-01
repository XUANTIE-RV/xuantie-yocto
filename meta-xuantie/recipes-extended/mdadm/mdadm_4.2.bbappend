# musl does not provide GNU basename() in <string.h>.
# musl provides off64_t/lseek64 compat macros only under _LARGEFILE64_SOURCE.
CFLAGS:append:libc-musl = " -include libgen.h -D_LARGEFILE64_SOURCE"

# _LARGEFILE64_SOURCE causes lseek type conflicts in ptest; disable it for musl.
PTEST_ENABLED:libc-musl = "0"
