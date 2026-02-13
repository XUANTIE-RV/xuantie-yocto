SUMMARY = "Support InfiniBand verbs"
DESCRIPTION = "Libibverbs is a library that allows userspace processes to use InfiniBand/RDMA 'verbs' directly."
HOMEPAGE = "http://www.openfabrics.org/downloads/verbs/"
SECTION = "libs/devel"

LICENSE = "GPL-2.0-only | BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=7c557f27dd795ba77cc419dddc656b51"

# Official repo is at git://git.kernel.org/pub/scm/libs/infiniband/libibverbs.git
SRC_URI = "https://www.openfabrics.org/downloads/verbs/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "1544ebb89d861ce84057ab43dfcd22a0"
SRC_URI[sha256sum] = "c352a7f24e9a9d30ea74faa35d1b721d78d770506a0c03732e3132b7c85ac330"

inherit autotools

DEPENDS = "libnl pkgconfig-native"
PACKAGES += "${PN}-utils"
FILES:${PN} = "${sysconfdir} ${libdir}/*.so.*"
FILES:${PN}-utils = "${bindir}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[valgrind] = "--with-valgrind,--without-valgrind,valgrind,"

do_install:append() {
        mkdir -p ${D}${sysconfdir}/libibverbs.d
        rm -f ${D}${libdir}/libibverbs.la
}

PROVIDES = "virtual/libibverbs"
RPROVIDES:${PN} = "virtual-libibverbs"

COMPATIBLE_HOST:mipsarch = "none"
COMPATIBLE_HOST:arm = "none"
