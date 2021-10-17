inherit kernel
require recipes-kernel/linux/linux-yocto.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

FILESEXTRAPATHS_prepend := "${THISDIR}/files/xuantie-c910:"

SRC_URI = " \
    git://gitee.com/thead-linux/kernel.git;branch=${KMACHINE};protocol=http \
"

LINUX_VERSION ?= "${PV}"
LINUX_VERSION_EXTENSION ?= "-thead"

SRCREV="${AUTOREV}"

DEPENDS += "elfutils-native"

# COMPATIBLE_MACHINE = "${MACHINE}"
COMPATIBLE_MACHINE = "ice"

S = "${WORKDIR}/linux-${PV}"

# KBUILD_DEFCONFIG_qemu64-c910 = "qemux-c910_defconfig"

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
