DESCRIPTION = "openblas-test"
LICENSE = "CLOSED"


SRC_URI = " \
    git://github.com/xianyi/BLAS-Tester.git;protocol=https;branch=master \
    file://0001-fix-build-error.patch \
"

SRCREV = "8e1f62468ef377c4621dc9dee49f3a4b17134652"

S = "${WORKDIR}/git"

DEPENDS += "openblas"


inherit xuantie-set-package-arch-with-mcpu

def openblas_get_config(d):
    generic_configs = ["c9xx-riscv64"]
    if d.getVar('XUANTIE_MCPU') in generic_configs:
        return "${XUANTIE_TUNE_CCARGS}";
    else:
        return "-mcpu=" + d.getVar('XUANTIE_MCPU');

TUNE_CCARGS += "${@openblas_get_config(d)} -Wno-implicit-function-declaration"

export ARCH = "RISCV64"

do_install() {
    install -d ${D}${bindir}
    cp ${B}/bin/* ${D}${bindir}
}

FIEL:${PN} = "${bindir}"
