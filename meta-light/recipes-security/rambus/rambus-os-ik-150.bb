DESCRIPTION = "rambus Security-IP-150 DDK"
LICENSE = "CLOSED"

PV = "1.0"
PR = "r0"

COMPATIBLE_MACHINE = "light-*"

SRC_URI = " \
            git://git@gitee.com/thead-yocto/light-libs.git;branch=master;protocol=http \
          "
THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"

S = "${WORKDIR}/git/rambus_sec_lib"

DEPENDS += " linux-thead "

export PROJECT_DIR?="${COREBASE}/.."
export BUILD_ROOT?="${TOPDIR}"
export KERNEL_VERSION="$(cat ${BASE_WORKDIR}/kernel_version)"

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
    install -d ${D}${base_libdir}/firmware
    install -d ${D}${libdir}
    install -d ${D}${libdir}/engines-1.1
    install -d ${D}${libdir}/pkgconfig
    install -d ${D}${includedir}/openssl
    install -d ${D}${datadir}/rambus-IP-150
    install -d ${D}${datadir}/rambus-IP-150/bin
    install -d ${D}${datadir}/rambus-IP-150/ssl

    install -m 0755 ${S}/lib/firmware/firmware_eip28.bin ${D}${base_libdir}/firmware/firmware_eip28.bin
    install -m 0755 ${S}/lib/modules/extra/*.ko ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0755 ${S}/lib/*.so* ${D}${libdir}/engines-1.1
    install -m 0755 ${S}/lib/*.a* ${D}${libdir}/engines-1.1
    install -m 0755 ${S}/lib/engines-1.1/* ${D}${libdir}/engines-1.1
    install -m 0755 ${S}/lib/pkgconfig/* ${D}${libdir}/pkgconfig
    install -m 0755 ${S}/include/openssl/* ${D}${includedir}/openssl
    cp ${S}/bin/* ${D}${datadir}/rambus-IP-150/bin/ -rf
    install -m 0755 ${S}/ssl/*.cnf ${D}${datadir}/rambus-IP-150/ssl
    install -m 0755 ${S}/ssl/*.dist ${D}${datadir}/rambus-IP-150/ssl
}

FILES_${PN} += " ${base_libdir} "
FILES_${PN} += " ${libdir} "
FILES_${PN} += " ${includedir} "
FILES_${PN} += " ${datadir} "

PACKAGES = "${PN}"
INSANE_SKIP_${PN} += " staticdev debug-files already-stripped dev-deps file-rdeps "
