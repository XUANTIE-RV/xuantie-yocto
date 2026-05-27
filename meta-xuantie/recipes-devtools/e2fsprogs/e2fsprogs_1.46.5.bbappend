# musl provides fallocate64/fadvise64/posix_fadvise64/lseek64 as macros (via
# _LARGEFILE64_SOURCE), not real symbols. autoconf's AC_CHECK_FUNCS undefs
# these macros so the link test fails. Force the defines via CFLAGS.
# Without HAVE_LSEEK64, rv32 builds fall into a _syscall5-based llseek path
# that requires the glibc-only _syscall5 macro.
CFLAGS:append:libc-musl = " -D_LARGEFILE64_SOURCE -DHAVE_LSEEK64 -DHAVE_FALLOCATE64 -DHAVE_FADVISE64 -DHAVE_POSIX_FADVISE64"
