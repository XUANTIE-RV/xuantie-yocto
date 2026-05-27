# musl provides struct stat64 / lstat64 as compat macros only under
# _LARGEFILE64_SOURCE. btrfs-tools uses stat64 in mkfs/rootdir.c.
CFLAGS:append:libc-musl = " -D_LARGEFILE64_SOURCE"
