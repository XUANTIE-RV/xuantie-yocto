# musl provides struct stat64 / lstat64 as compat macros only under
# _LARGEFILE64_SOURCE. btrfs-tools uses stat64 in mkfs/rootdir.c.
CFLAGS:append:libc-musl = " -D_LARGEFILE64_SOURCE"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# common/device-utils.c uses basename() but only includes <string.h>; under
# musl that header does not declare basename(), and recent clang turns the
# implicit-declaration / int-to-pointer-assignment warning into a hard error.
SRC_URI:append:libc-musl = " file://0001-common-device-utils-include-libgen.h-for-basename.patch"

# py3.13 removed the private _PyObject_LookupSpecial API used by the
# btrfsutil python extension (module.c); replace with public API.
SRC_URI += "file://0001-python-replace-private-lookup-special-with-public-api.patch"
