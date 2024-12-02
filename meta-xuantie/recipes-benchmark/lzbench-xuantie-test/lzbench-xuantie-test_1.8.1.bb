DESCRIPTION = "lzbench xuantie test for zstd"
LICENSE = "CLOSED"

SRC_URI = " \
        git://github.com/inikep/lzbench.git;branch=master;protocol=https \
        file://0001-Support-dynamic-linking-of-zstd-library.patch \
        "

SRCREV = "d138844ea56b36ff1c1c43b259c866069deb64ad"

S = "${WORKDIR}/git"

DEPENDS += "zstd"

EXTRA_OEMAKE += "VERBOSE=1 DONT_BUILD_GLZA=1 DONT_BUILD_TORNADO=1 DONT_BUILD_FASTLZMA2=1 MOREFLAGS+=-lzstd"


do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/lzbench ${D}/${bindir}
}
