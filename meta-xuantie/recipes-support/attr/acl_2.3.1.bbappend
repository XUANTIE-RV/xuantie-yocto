# musl provides dirent64/readdir64 compatibility macros in <dirent.h>
# but only when _LARGEFILE64_SOURCE is defined.
CFLAGS:append:libc-musl = " -D_LARGEFILE64_SOURCE"
