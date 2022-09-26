DESCRIPTION = "thead iso7816 card test"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-*"

SRC_URI = " \
            git://git@gitee.com/thead-yocto/light-libs.git;branch=master;protocol=http \
          "
THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"

S = "${WORKDIR}/git/ISO7816"
export CROSS_COMPILE="riscv64-linux-"

PARALLEL_MAKEINST = "-j1"

do_compile() {
    oe_runmake
}


do_install() {
        install -d ${D}${datadir}/iso7816_card

        install -m 0755 ${S}/output/*   ${D}${datadir}/iso7816_card
        install -m 0755 ${S}/*.c        ${D}${datadir}/iso7816_card
        install -m 0755 ${S}/*.h        ${D}${datadir}/iso7816_card
}

FILES_${PN} += " ${datadir} "
PACKAGES = "${PN}"
INSANE_SKIP_${PN} += " debug-files already-stripped"
