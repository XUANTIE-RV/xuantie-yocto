inherit kernel
require recipes-kernel/linux/linux-yocto.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

FILESEXTRAPATHS_prepend := "${THISDIR}/files/xuantie-c910:"

SRC_URI = " \
    https://cdn.kernel.org/pub/linux/kernel/v5.x/linux-${PV}.tar.xz;name=kernel \
    file://xuantie-linux-5.4.36.patch \
    file://light-c910-dts.patch \
    file://light-c910_defconfig \
    file://qemu-c910_defconfig \
"

SRC_URI[kernel.md5sum] = "4c4edf3e7d127e4f1a471aa3a370f3e7"
SRC_URI[kernel.sha256sum] = "b9faea98122e8316af8fb428c942e81797b5d28a8fc59a24a4e47959e3765b8d"


LINUX_VERSION ?= "${PV}"
LINUX_VERSION_EXTENSION ?= "-thead"

SRCREV="${AUTOREV}"

DEPENDS += "elfutils-native"

# COMPATIBLE_MACHINE = "${MACHINE}"
COMPATIBLE_MACHINE = "ice|light-mpw|light-fm"

S = "${WORKDIR}/linux-${PV}"

# KBUILD_DEFCONFIG_qemu64-c910 = "qemux-c910_defconfig"
# KBUILD_DEFCONFIG_light-c910 = "light-c910_defconfig"
# KBUILD_DEFCONFIG_ice-c910 = "ice-c910_defconfig"

do_configure_append() {
  [ -d ${STAGING_KERNEL_DIR} ] && rm -rf ${STAGING_KERNEL_DIR}
  [ -f ${STAGING_KERNEL_DIR} ] && rm -rf ${STAGING_KERNEL_DIR}

  ln -s ${S}  ${STAGING_KERNEL_DIR}
}
do_install_append() {

   [ -f ${STAGING_KERNEL_BUILDDIR} ] && rm -rf ${STAGING_KERNEL_BUILDDIR}
   [ -d ${STAGING_KERNEL_BUILDDIR} ] && rm -rf ${STAGING_KERNEL_BUILDDIR}

   ln -s ${B}  ${STAGING_KERNEL_BUILDDIR}
}

KCONFIG_MODE="--alldefconfig"
