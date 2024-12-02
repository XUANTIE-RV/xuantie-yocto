inherit kernel
require recipes-kernel/linux/linux-yocto.inc

SRC_URI = " \
            git://github.com/ruyisdk/linux-xuantie-kernel.git;branch=linux-6.6.36;protocol=https \
            file://0001-riscv-kdump-Fix-gen-proc-vmcore.patch \
            file://0002-disable-canaan-gpu.patch \
            file://kernel.12HZ.cfg \
            file://kernel.torture-test.cfg \
            file://kernel.virtio.cfg \
            file://kernel.net.cfg \
            file://local-version.cfg \
"

DEBUG_CFG = " \
            file://kernel.debug.cfg \
            file://kernel.ftrace.cfg \
            file://kernel.ebpf.cfg \
            file://kernel.em.cfg \
            file://kernel.kselftest.cfg \
            file://kernel.kdump.cfg \
"

SRC_URI += "${@bb.utils.contains('MACHINE_FEATURES', 'kcdebug', '${DEBUG_CFG}', '', d)}"

SRC_URI:append:riscv32 = " file://32-bit.cfg"
# SRC_URI:append:riscv64 = " ${@bb.utils.contains('MACHINE_FEATURES', 'kcdebug', 'file://kernel.btf.cfg', '', d)}"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"


KERNEL_VERSION_SANITY_SKIP="1"

# Date: Fri Sep 13 14:49:03 2024 +0800
# commit info: dts: k230: fix LCD DTS
SRCREV = "b5c8e48a9ebf3f5726dc056f71430ddd3f598e07"
LICENSE = "CLOSED"

S = "${WORKDIR}/linux-${PV}"

do_configure:append() {
   [ -d ${STAGING_KERNEL_DIR} ] && rm -rf ${STAGING_KERNEL_DIR}
   [ -f ${STAGING_KERNEL_DIR} ] && rm -rf ${STAGING_KERNEL_DIR}

   ln -s ${S}  ${STAGING_KERNEL_DIR}
}

do_install:append() {
   install -d ${D}${sysconfdir}

   head=$(git --git-dir=${S}/.git rev-parse --verify HEAD 2>/dev/null)
   echo "commit-id:"${head} > ${D}${sysconfdir}/kernel-release
}

do_deploy:append() {
   install -d ${D}${sysconfdir}

   if [ ! -d ${DEPLOY_DIR_IMAGE}/.boot ]; then
      mkdir -p ${DEPLOY_DIR_IMAGE}/.boot
   fi
   head=$(git --git-dir=${S}/.git rev-parse --verify HEAD 2>/dev/null)
   echo "commit-id:"${head} > ${DEPLOY_DIR_IMAGE}/.boot/kernel-release
   rm -f ${BASE_WROKDIR}/kernel_version
   touch ${BASE_WORKDIR}/kernel_version
   echo ${KERNEL_VERSION_PKG_NAME} > ${BASE_WORKDIR}/kernel_version

   cp ${B}/vmlinux ${DEPLOYDIR}
}

KCONFIG_MODE="--alldefconfig"

FILES:${KERNEL_PACKAGE_NAME}-base += "${sysconfdir}"
