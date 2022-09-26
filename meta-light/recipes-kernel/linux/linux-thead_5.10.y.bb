inherit kernel
require recipes-kernel/linux/linux-yocto.inc
DEPENDS = "e2fsprogs-native opensbi"

SRC_URI = " \
            git://git@gitee.com/thead-yocto/kernel.git;branch=master;protocol=http \
"
# crop the kernel based on the defconfig
# FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
# SRC_URI += "file://cvl1.cfg"
# SRC_URI += "file://cvl2.cfg"
# SRC_URI += "file://cvl3.cfg"

KERNEL_VERSION_SANITY_SKIP="1"

THEAD_BSP_TAG ?= "${AUTOREV}"
THEAD_LINUX_TAG ?= "${THEAD_BSP_TAG}"
SRCREV = "${THEAD_LINUX_TAG}"
LICENSE = "CLOSED"

DEPENDS += "elfutils-native"

COMPATIBLE_MACHINE = "light-*"

S = "${WORKDIR}/linux-${PV}"

do_configure_append() {
   [ -d ${STAGING_KERNEL_DIR} ] && rm -rf ${STAGING_KERNEL_DIR}
   [ -f ${STAGING_KERNEL_DIR} ] && rm -rf ${STAGING_KERNEL_DIR}

   ln -s ${S}  ${STAGING_KERNEL_DIR}

}
do_install_append() {

   [ -f ${STAGING_KERNEL_BUILDDIR} ] && rm -rf ${STAGING_KERNEL_BUILDDIR}
   [ -d ${STAGING_KERNEL_BUILDDIR} ] && rm -rf ${STAGING_KERNEL_BUILDDIR}

   ln -s ${B}  ${STAGING_KERNEL_BUILDDIR}

   if [ ! -d ${DEPLOY_DIR_IMAGE}/.boot ]; then
      mkdir -p ${DEPLOY_DIR_IMAGE}/.boot
   fi

   if [ -f ${B}/arch/riscv/boot/Image ]; then
      cp -f ${B}/arch/riscv/boot/Image ${DEPLOY_DIR_IMAGE}/.boot
   fi

   dtbfiles=`ls -lt ${B}/arch/riscv/boot/dts/thead/light-*.dtb | awk '{print $9}'`
   for i in $dtbfiles;
   do
      if [ -f $i ]; then
         cp ${i} ${DEPLOY_DIR_IMAGE}/.boot
      fi
   done
   dd if=/dev/zero of=${DEPLOY_DIR_IMAGE}/boot.ext4 count=10000 bs=4096
   ${COMPONENTS_DIR}/x86_64/e2fsprogs-native/sbin/mkfs.ext4 -F  ${DEPLOY_DIR_IMAGE}/boot.ext4 -d ${DEPLOY_DIR_IMAGE}/.boot

   rm -f ${BASE_WROKDIR}/kernel_version
   touch ${BASE_WORKDIR}/kernel_version
   echo ${KERNEL_VERSION_PKG_NAME} > ${BASE_WORKDIR}/kernel_version
}

do_install[nostamp] = "1"
KCONFIG_MODE="--alldefconfig"
