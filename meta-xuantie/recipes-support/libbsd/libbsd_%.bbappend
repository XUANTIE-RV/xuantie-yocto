# musl does not provide off64_t (off_t is already 64-bit in musl)
CFLAGS:append:libc-musl = " -Doff64_t=off_t"
