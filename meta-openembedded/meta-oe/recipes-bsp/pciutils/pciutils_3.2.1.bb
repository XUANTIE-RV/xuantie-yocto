SUMMARY = "PCI utilities"
DESCRIPTION = 'The PCI Utilities package contains a library for portable access \
to PCI bus configuration space and several utilities based on this library.'
HOMEPAGE = "http://atrey.karlin.mff.cuni.cz/~mj/pciutils.shtml"
SECTION = "console/utils"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS = "zlib kmod"

SRC_URI = "${KERNELORG_MIRROR}/software/utils/pciutils/pciutils-${PV}.tar.bz2 \
           file://configure.patch \
           file://lib-build-fix.patch \
           file://guess-fix.patch \
           file://makefile.patch"

SRC_URI[md5sum] = "425b1acad6854cc2bbb06ac8e48e76fc"
SRC_URI[sha256sum] = "12d52b19042e2fd058af12e7d877bbbce72213cb3a0b5ec7ff0703ac09e3dcde"

inherit multilib_header

PARALLEL_MAKE = ""

PCI_CONF_FLAG = "ZLIB=yes DNS=yes SHARED=yes"

# see configure.patch
do_configure () {
	(
	  cd lib && \
	  ${PCI_CONF_FLAG} ./configure ${PV} ${datadir} ${TARGET_OS} ${TARGET_ARCH}
	)
}

do_compile:prepend () {
	# Avoid this error:  ln: failed to create symbolic link `libpci.so': File exists
	rm -f ${S}/lib/libpci.so
}

export PREFIX = "${prefix}"
export SBINDIR = "${sbindir}"
export SHAREDIR = "${datadir}"
export MANDIR = "${mandir}"

EXTRA_OEMAKE += "${PCI_CONF_FLAG}"

# The configure script breaks if the HOST variable is set
HOST[unexport] = "1"

do_install () {
	oe_runmake DESTDIR=${D} install install-lib

	install -d ${D}${bindir}
	ln -s ../sbin/lspci ${D}${bindir}/lspci

	oe_multilib_header pci/config.h
}

PACKAGES =+ "${PN}-ids libpci libpci-dev libpci-dbg"
FILES:${PN}-ids = "${datadir}/pci.ids*"
FILES:libpci = "${libdir}/libpci.so.*"
FILES:libpci-dbg = "${libdir}/.debug"
FILES:libpci-dev = "${libdir}/libpci.a ${libdir}/libpci.la ${libdir}/libpci.so \
                    ${includedir}/pci ${libdir}/pkgconfig"
SUMMARY:${PN}-ids = "PCI utilities - device ID database"
DESCRIPTION:${PN}-ids = "Package providing the PCI device ID database for pciutils."
RDEPENDS:${PN} += "${PN}-ids"
