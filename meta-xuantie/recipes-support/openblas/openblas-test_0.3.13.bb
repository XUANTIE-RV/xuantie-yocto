DESCRIPTION = "openblas-test"
LICENSE = "CLOSED"


SRC_URI = " \
    git://github.com/xianyi/BLAS-Tester.git;protocol=https;branch=master \
    file://0001-fix-build-error.patch \
"

SRCREV = "8e1f62468ef377c4621dc9dee49f3a4b17134652"

S = "${WORKDIR}/git"

DEPENDS += "openblas"


CFLAGS:append:riscv64 = " -mcpu=c920 -lc"
CXXFLAGS:append:riscv64 = " -mcpu=c920 -lc"

export ARCH = "RISCV64"

do_install() {
    install -d ${D}${bindir}
    cp ${B}/bin/* ${D}${bindir}
}

FIEL:${PN} = "${bindir}"
