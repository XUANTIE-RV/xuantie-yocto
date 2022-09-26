DESCRIPTION = "thead efuse hal api lib"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-*"

SRC_URI = " \
            git://git@gitee.com/thead-yocto/light-libs.git;branch=master;protocol=http \
          "
THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"

S = "${WORKDIR}/git/efuse-hal-lib"

export SYSROOT_DIR="${RECIPE_SYSROOT}"
export SYSROOT_DIR="${RECIPE_SYSROOT_NATIVE}"

export ARCH?="riscv"
export CROSS_COMPILE="riscv64-linux-"
export INSTALL_DIR_SDK?="${SDK_DEPLOY}"

EXTRA_OEMAKE+="BUILD_SYSTEM='YOCTO_BUILD'"

PARALLEL_MAKEINST = "-j1"

do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}${base_libdir}/modules/5.10.4-thead/extra
    install -d ${D}${libdir}
    install -d ${D}${includedir}/efuse
    install -d ${D}${datadir}/efuse
    install -d ${D}${datadir}/efuse/test

    install -m 0755 ${S}/lib/output/lib*.so*                              ${D}${libdir}
    install -m 0755 ${S}/test/output/*                                    ${D}${datadir}/efuse/test/
    install -m 0755 ${S}/lib/src/efuse-api.h                              ${D}${includedir}/efuse
}

FILES_${PN} += " ${base_libdir} "
FILES_${PN} += " ${libdir} "
FILES_${PN} += " ${datadir} "
FILES_${PN} += " ${includedir} "

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files already-stripped"

