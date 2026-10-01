
PACKAGECONFIG += "rdma vdpa"
PACKAGECONFIG[vdpa] = ",,libmnl,"

IPROUTE2_MAKE_SUBDIRS += "${@bb.utils.filter('PACKAGECONFIG', 'vdpa', d)}"

IPROUTE2_PACKAGES =+ "\
    ${PN}-vdpa \
"

FILES:${PN}-vdpa = "${base_sbindir}/vdpa"

inherit xuantie-gcc-compatible

# musl does not provide GNU basename() in <string.h>; use POSIX version.
CFLAGS:append:libc-musl = " -include libgen.h"
