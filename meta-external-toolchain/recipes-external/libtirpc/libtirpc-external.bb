SUMMARY = "Transport-Independent RPC library"
DESCRIPTION = "Libtirpc is a port of Suns Transport-Independent RPC library to Linux"
SECTION = "libs/network"
HOMEPAGE = "http://sourceforge.net/projects/libtirpc/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=183075&atid=903784"
LICENSE = "BSD-3-Clause"

PROVIDES = "virtual/librpc"

inherit external-toolchain

FILES:${PN} = "\
    ${sysconfdir}/bindresvport.blacklist \
    ${sysconfdir}/netconfig \
    ${libdir}/libtirpc${SOLIBS} \
"
FILES:${PN}-dev = "\
    ${libdir}/libtirpc${SOLIBSDEV} \
    ${includedir}/tirpc \
    ${libdir}/pkgconfig/libtirpc.pc \
"
FILES:${PN}-staticdev = "${libdir}/libtirpc.a"

libc_rdep = "${@'${PREFERRED_PROVIDER_virtual/libc}' if '${PREFERRED_PROVIDER_virtual/libc}' else '${TCLIBC}'}"
RDEPENDS:${PN} += "${libc_rdep}"
