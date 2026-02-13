DESCRIPTION = "Go Meta Linter."
HOMEPAGE = "https://github.com/alecthomas/gometalinter"
SECTION = "devel/go"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=dee20b35647295553d32faef05846a7b"

BBCLASSEXTEND = "native"

SRCNAME = "gometalinter"

PKG_NAME = "github.com/alecthomas/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;branch=master;protocol=https"

SRCREV = "e8d801238da6f0dfd14078d68f9b53fa50a7eeb5"
PV = "0.0+git${SRCPV}"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

FILES:${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
