DESCRIPTION = "thead neural network accelerator driver"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = ""

COMPATIBLE_MACHINE = "light-*"

DEPENDS = " linux-thead "
RDEPENDS_${PN} += " bash "

SRC_URI = " \
            git://git@gitee.com/thead-yocto/npu-ax3386-kernel.git;branch=master;protocol=http \
            file://npu-ax3386.service \
            file://98-npu-ax3386.preset \
          "

THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"

S = "${WORKDIR}/git"

export SYSROOT_DIR="${RECIPE_SYSROOT}"

export PROJECT_DIR?="${COREBASE}/.."
export ARCH?="riscv"
export BOARD_NAME="${MACHINEOVERRIDES}"
export BUILD_ROOT?="${TOPDIR}"
export BUILDROOT_DIR?="${BUILD_ROOT}"
export CROSS_COMPILE="riscv64-linux-"
export TOOLCHAIN_DIR?="${EXTERNAL_TOOLCHAIN}"
export LINUX_DIR?="${STAGING_KERNEL_BUILDDIR}"

export INSTALL_DIR_ROOTFS?="${IMAGE_ROOTFS}"
export INSTALL_DIR_SDK?="${SDK_DEPLOY}"

export PATH="${SYSROOT_DIR}:${SYSROOT_DIR}/usr/include:${SYSROOT_DIR}/usr/lib:${SYSROOT_DIR}/lib:${SYSROOT_DIR}/include:${RECIPE_SYSROOT_NATIVE}/usr/bin/riscv64-oe-linux:${COREBASE}/scripts:${COREBASE}/bitbake/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin"
export KERNEL_VERSION="$(cat ${BASE_WORKDIR}/kernel_version)"

EXTRA_OEMAKE+="BUILD_SYSTEM='YOCTO_BUILD'"

PARALLEL_MAKEINST = "-j1"

do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
    install -d ${D}/lib/systemd/system
    install -d ${D}/lib/systemd/system-preset

    install -m 0644 ${S}/output/rootfs/bsp/npu/ko/*.ko                               ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0755 ${WORKDIR}/98-npu-ax3386.preset 				     ${D}/lib/systemd/system-preset
    install -m 0755 ${WORKDIR}/npu-ax3386.service 				     ${D}/lib/systemd/system
}

FILES_${PN} += " ${base_libdir} "

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files already-stripped dev-deps file-rdeps "
