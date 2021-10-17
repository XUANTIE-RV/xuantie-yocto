inherit kernel
require recipes-kernel/linux/linux-yocto.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

SRC_URI = "git://git@gitlab.alibaba-inc.com/thead-linux/d1_linux-kernel.git;protocol=ssh \
           file://enable_initramfs.cfg \
           file://rootfs.cpio.gz.d1 \
          "

LINUX_VERSION ?= "${PV}"
LINUX_VERSION_EXTENSION ?= ""

SRCREV="${AUTOREV}"

DEPENDS += "elfutils-native"

S = "${WORKDIR}/linux-${PV}"

COMPATIBLE_MACHINE = "(^d1.*)"
KBUILD_DEFCONFIG_d1 = "d1_defconfig"

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
