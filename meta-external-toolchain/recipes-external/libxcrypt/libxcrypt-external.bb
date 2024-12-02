SUMMARY = "Extended cryptographic library (from glibc)"
DESCRIPTION = "Forked code from glibc libary to extract only crypto part."
HOMEPAGE = "https://github.com/besser82/libxcrypt"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
PROVIDES = "virtual/crypt"

inherit external-toolchain

EXTERNAL_PROVIDE_PATTERN = "${libdir}/libcrypt.so.*"

libc_rdep = "${@'${PREFERRED_PROVIDER_virtual/libc}' if '${PREFERRED_PROVIDER_virtual/libc}' else '${TCLIBC}'}"
RDEPENDS:${PN} += "${libc_rdep}"

FILES:${PN} = "${libdir}/libcrypt.so.* \
               ${libdir}/libcrypt-*.so \
               ${libdir}/libowcrypt*.so.* \
               ${libdir}/libowcrypt-*.so \
"
FILES:${PN}-dev = "\
    ${libdir}/libcrypt.so \
    ${libdir}/libowcrypt.so \
    ${includedir}/crypt.h \
    ${libdir}/pkgconfig/libcrypt.pc \
    ${libdir}/pkgconfig/libxcrypt.pc \
"
