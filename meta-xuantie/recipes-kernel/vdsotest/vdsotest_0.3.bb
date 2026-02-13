SUMMARY = "vdsotest is a utility for testing and benchmarking a Linux VDSO"
HOMEPAGE = "https://github.com/nlynch-mentor/vdsotest"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/nlynch-mentor/vdsotest.git;protocol=https;branch=master"
SRCREV = "4ad733fb25364e4afaf4060e57c2429a93b686c5"

S = "${WORKDIR}/git"

inherit autotools
