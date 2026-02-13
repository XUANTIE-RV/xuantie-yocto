DESCRIPTION = "Small Path Tracer"
HOMEPAGE = "http://www.kevinbeason.com/smallpt/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a75658fd660ce7cc930fe58b149a7939"

SRC_URI = " \
    https://www.kevinbeason.com/smallpt/smallpt.tar.gz \
"

SRC_URI[sha256sum] = "af82ec5d57a7eb30e5f6fb6ed8b6fc2c36596324475b8646f8ce4d629ffd8e43"

S = "${WORKDIR}/smallpt"

DEPENDS:append:toolchain-clang = " openmp"

do_compile() {
    oe_runmake CPP="${CXX} -Wno-narrowing" smallpt
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/smallpt ${D}${bindir}/smallpt
}
