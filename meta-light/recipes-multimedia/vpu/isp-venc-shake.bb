DESCRIPTION = "Light ISP VENC SHAKE module driver"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-*"

SRC_URI = " \
            git://git@gitee.com/thead-yocto/isp_venc_shake.git;branch=master;protocol=http \
          "

THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"

S = "${WORKDIR}/git"

DEPENDS += " linux-thead "

export SYSROOT_DIR="${PKG_CONFIG_SYSROOT_DIR}"

export ARCH?="riscv"
export BOARD_NAME="${MACHINEOVERRIDES}"
export CROSS_COMPILE="riscv64-linux-"

export TOOLCHAIN_DIR?="${EXTERNAL_TOOLCHAIN}"
export LINUX_DIR?="${STAGING_KERNEL_BUILDDIR}"

export PATH="/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"

export EXTRA_OEMAKE = "VIDEO_MEMORY_PATH=${PKG_CONFIG_SYSROOT_DIR}/${includedir}/vidmem"
export PROJECT_DIR?="${COREBASE}/.."
export KERNEL_VERSION="$(cat ${BASE_WORKDIR}/kernel_version)"

PARALLEL_MAKEINST = "-j1"

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra/ivs
    install -d ${D}${libdir}/ivs
    install -d ${D}${includedir}/ivs
    install -d ${D}${datadir}/ivs/test/bin

    install -m 0644 ${S}/output/rootfs/bsp/ivs/ko/*.ko        ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra/ivs
    install -m 0755 ${S}/output/rootfs/bsp/ivs/lib/*          ${D}${libdir}/ivs
    install -m 0755 ${S}/output/rootfs/bsp/ivs/lib/*.so       ${D}${libdir}
    install -m 0755 ${S}/output/rootfs/bsp/ivs/test/*         ${D}${datadir}/ivs/test/bin
    install -m 0644 ${S}/user_mode/isp_venc_shake_hal.h       ${D}${includedir}/ivs
}

FILES_${PN} = "${base_libdir} ${libdir} ${datadir} ${includedir}"
PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files staticdev "
