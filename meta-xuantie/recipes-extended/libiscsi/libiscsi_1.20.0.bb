SUMMARY = "libiscsi: An iSCSI library"
DESCRIPTION = "libiscsi is an iSCSI library that provides a simple and straight forward API for iSCSI clients."
HOMEPAGE = "https://github.com/sahlberg/libiscsi"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5bb9bfbf336406c7b1066367474dda7b"

SRC_URI = "git://github.com/sahlberg/libiscsi.git;branch=master;protocol=https"
SRCREV = "9637e1371e84788c23e0481a3087ebacd3b18b6d"

inherit autotools

DEPENDS += "automake libtool pkgconfig"
CFLAGS += "-Wno-strict-prototypes"

S = "${WORKDIR}/git"
B = "${S}"

do_configure:prepend() {
    ./autogen.sh
}
