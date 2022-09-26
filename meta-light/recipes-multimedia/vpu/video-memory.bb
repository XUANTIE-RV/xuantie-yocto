DESCRIPTION = "Light Video Memory module driver"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-*"

SRC_URI = " \
            git://git@gitee.com/thead-yocto/video_memory.git;branch=master;protocol=http \
          "

THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV="${THEAD_BSP_TAG}"

S = "${WORKDIR}/git"

DEPENDS += " linux-thead "

export SYSROOT_DIR="${PKG_CONFIG_SYSROOT_DIR}"

export ARCH?="riscv"
export BOARD_NAME="${MACHINEOVERRIDES}"
export CROSS_COMPILE="riscv64-linux-"

export TOOLCHAIN_DIR?="${EXTERNAL_TOOLCHAIN}"
export LINUX_DIR?="${STAGING_KERNEL_BUILDDIR}"

export PATH="/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
export PROJECT_DIR?="${COREBASE}/.."
export KERNEL_VERSION="$(cat ${BASE_WORKDIR}/kernel_version)"

PARALLEL_MAKEINST = "-j1"

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
    install -d ${D}${libdir}/vidmem
    install -d ${D}${includedir}/vidmem
    install -d ${D}${datadir}/vidmem/test/bin

    install -m 0644 ${S}/output/rootfs/bsp/vidmem/ko/*.ko     ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0755 ${S}/output/rootfs/bsp/vidmem/lib/*.so    ${D}${libdir}
    install -m 0755 ${S}/output/rootfs/bsp/vidmem/test/*      ${D}${datadir}/vidmem/test/bin
    install -m 0644 ${S}/driver/video_memory.h                ${D}${includedir}/vidmem
    install -m 0644 ${S}/lib/video_mem.h                      ${D}${includedir}/vidmem
}

FILES_${PN} = "${base_libdir} ${libdir} ${datadir} ${includedir}"
PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files "
