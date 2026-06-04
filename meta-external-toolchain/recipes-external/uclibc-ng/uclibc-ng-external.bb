# Recipe to extract uclibc-ng C library from an external toolchain
#
# uclibc-ng-external extracts the uclibc-ng libc shared library, dynamic linker,
# headers, CRT files, and stub static libraries from the external toolchain
# sysroot. It provides virtual/libc and related virtual packages for the
# uclibc-ng C library variant.

SUMMARY = "uClibc-ng C library (from external toolchain)"
DESCRIPTION = "The uClibc-ng C library extracted from a pre-built external toolchain."
HOMEPAGE = "http://www.uclibc-ng.org/"
SECTION = "libs"

inherit external-toolchain

COMPATIBLE_HOST = ".*-uclibc.*"

require recipes-external/uclibc-ng/uclibc-ng-external-version.inc

DEPENDS = "virtual/${TARGET_PREFIX}binutils"
PROVIDES += "\
    uclibc-ng \
    virtual/libc \
    virtual/libintl \
    virtual/libiconv \
    virtual/crypt \
    linux-libc-headers \
    linux-libc-headers-dev \
"

# License detection: uclibc-ng uses LGPL-2.1 license
LICENSE = "LGPL-2.1-only"

PACKAGES = "${PN}-dbg ${PN} ${PN}-dev ${PN}-staticdev ${PN}-doc"

FILES:${PN} = "\
    ${base_libdir}/libuClibc-*.so \
    ${base_libdir}/ld-uClibc.so.* \
    ${libdir}/libc.so \
    ${libdir}/libm.so.* \
    ${libdir}/libpthread.so.* \
"

FILES:${PN}-dev = "\
    ${libdir}/*crt*.o \
    ${libdir}/libm.a \
    ${libdir}/libpthread.a \
    ${libdir}/libdl.a \
    ${libdir}/libresolv.a \
    ${libdir}/libutil.a \
    ${libdir}/libcrypt.a \
    ${includedir}/*.h \
    ${includedir}/sys/*.h \
    ${includedir}/bits/*.h \
    ${includedir}/netinet/*.h \
    ${includedir}/arpa/*.h \
    ${includedir}/net/*.h \
    ${includedir}/rpc/*.h \
    ${includedir}/scsi/*.h \
"

# Include Linux kernel headers in -dev package (same as musl-external)
linux_include_subdirs = "asm asm-generic bits drm linux mtd rdma sound video"
FILES:${PN}-dev += "\
    ${@' '.join('${includedir}/%s' % d for d in '${linux_include_subdirs}'.split())} \
"

FILES:${PN}-staticdev = "\
    ${libdir}/libc.a \
    ${libdir}/libm.a \
    ${libdir}/libpthread.a \
"

FILES:${PN}-doc = ""

# Provide the standard uclibc-ng package names for dependency resolution
RPROVIDES:${PN} += "uclibc-ng rtld(GNU_HASH) ldd"
RPROVIDES:${PN}-dev += "uclibc-ng-dev virtual-libc-dev linux-libc-headers-dev bsd-headers-dev"
RPROVIDES:${PN}-staticdev += "uclibc-ng-staticdev"
RPROVIDES:${PN}-dbg += "uclibc-ng-dbg"

LEAD_SONAME = "libc.so"
INSANE_SKIP:${PN} += "dev-so libdir"
INSANE_SKIP:${PN}-dev += "staticdev"

python do_install () {
    bb.build.exec_func('external_toolchain_do_install', d)
}

# uclibc's utils need libgcc
do_package[depends] += "${MLPREFIX}libgcc:do_packagedata"
do_package_write_ipk[depends] += "${MLPREFIX}libgcc:do_packagedata"
do_package_write_deb[depends] += "${MLPREFIX}libgcc:do_packagedata"
do_package_write_rpm[depends] += "${MLPREFIX}libgcc:do_packagedata"

do_packagedata[depends] += "gcc-runtime:do_packagedata"
