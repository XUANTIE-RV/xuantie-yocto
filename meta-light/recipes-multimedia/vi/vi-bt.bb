DESCRIPTION = "thead neural network accelerator driver"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = ""

COMPATIBLE_MACHINE = "light-*"

#DEPENDS = " openssl cmake-native python3 zlib boost"
DEPENDS = " openssl cmake-native python3 zlib boost linux-thead"

SRC_URI = " \
            git://git@gitee.com/thead-yocto/baremetal-drivers.git;branch=master;protocol=http \
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
  install -d ${D}${datadir}/bm_visys
  install -m 0644 ${S}/output/rootfs/bsp/baremetal/ko/bm_csi.ko                               ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
  install -m 0644 ${S}/output/rootfs/bsp/baremetal/ko/bm_visys.ko                             ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
  install -m 0755 ${S}/driver/visys/Module.symvers                                             ${D}${datadir}/bm_visys
}

FILES_${PN} += " ${base_libdir} "
FILES_${PN} += " ${libdir} "
FILES_${PN} += " ${datadir} "

PACKAGES = "${PN}"

# RDEPENDS_${PN} = " "
