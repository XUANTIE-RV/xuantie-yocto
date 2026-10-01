SUMMARY = "JBIG lossless image compression library"
DESCRIPTION = "JBIG-KIT provides a portable library of highly optimized \
header and compression routines for the JBIG (ISO/IEC 11544) compatible \
lossless image compression algorithm, which is well suited for bilevel \
images such as scanned documents and faxes."
HOMEPAGE = "https://www.cl.cam.ac.uk/~mgk25/jbigkit/"
SECTION = "libs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

SRC_URI = "https://www.cl.cam.ac.uk/~mgk25/jbigkit/download/jbigkit-${PV}.tar.gz"
SRC_URI[sha256sum] = "de7106b6bfaf495d6865c7dd7ac6ca1381bd12e0d81405ea81e7f2167263d932"

S = "${WORKDIR}/jbigkit-${PV}"

# Upstream only builds static archives; build the shared libraries
# (Debian libjbig0 ABI: libjbig.so.0 / libjbig85.so.0) directly.
do_compile() {
    cd ${S}/libjbig
    ${CC} ${CFLAGS} -fPIC -c jbig.c jbig85.c jbig_ar.c
    ${CC} ${LDFLAGS} -shared -Wl,-soname,libjbig.so.0 -o libjbig.so.0 jbig.o jbig_ar.o
    ${CC} ${LDFLAGS} -shared -Wl,-soname,libjbig85.so.0 -o libjbig85.so.0 jbig85.o jbig_ar.o
}

do_install() {
    install -d ${D}${libdir} ${D}${includedir}
    install -m 0755 ${S}/libjbig/libjbig.so.0 ${S}/libjbig/libjbig85.so.0 ${D}${libdir}/
    ln -sf libjbig.so.0 ${D}${libdir}/libjbig.so
    ln -sf libjbig85.so.0 ${D}${libdir}/libjbig85.so
    install -m 0644 ${S}/libjbig/jbig.h ${S}/libjbig/jbig85.h ${S}/libjbig/jbig_ar.h ${D}${includedir}/
}

BBCLASSEXTEND = "native nativesdk"
