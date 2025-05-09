inherit kernel
require recipes-kernel/linux/linux-yocto.inc

SRC_URI = " \
            ${@bb.utils.contains('MACHINECONFIG', 'public', '${XUANTIE_GIT}/linux-xuantie-kernel.git;branch=linux-6.6.36;destsuffix=git;protocol=https', \
            'git://github.com/ruyisdk/linux-xuantie-kernel.git;branch=linux-6.6.36;destsuffix=git;protocol=https \
             git://gerrit.damo.xrvm.cn:29518/xuantie-yocto/ntrace-kernel-patch.git;name=ntrace-patch;destsuffix=ntrace-patchs;branch=master;protocol=ssh', d)} \
            file://0001-riscv-kdump-Fix-gen-proc-vmcore.patch \
            file://0002-disable-canaan-gpu.patch \
            file://local-version.cfg \
            file://disable-kasan.cfg \
"

# DEPENDS += "pahole-native"

do_kernel_configme:prepend() {
   MARKER_FILE=${S}/configme
   if [ ! -f "$MARKER_FILE" ]; then
      if ${@bb.utils.contains('MACHINECONFIG', 'public', 'false', 'true', d)}; then
         patch -d ${S} -p1 -i ${WORKDIR}/ntrace-patchs/*.patch
      fi
      # Remove ebpf.config
      sed -i 's/ ebpf\.config//g' ${S}/arch/${ARCH}/Makefile
      sed -i 's/ thp\.config//g' ${S}/arch/${ARCH}/Makefile
      rm ${S}/kernel/configs/debug.config
      if [ "${@bb.utils.contains('MACHINE_FEATURES', 'kcdebug', 'true', 'false', d)}" = "true" ]; then
         if [ "${TUNE_ARCH}" = "riscv64" ]; then
            oe_runmake_call -C ${S} O=${B} xuantie_debug_defconfig
         elif [ "${TUNE_ARCH}" = "riscv32" ]; then
            oe_runmake_call -C ${S} O=${B} xuantie_rv32_debug_defconfig
         fi
      else
         if [ "${TUNE_ARCH}" = "riscv64" ]; then
            oe_runmake_call -C ${S} O=${B} xuantie_defconfig
         elif [ "${TUNE_ARCH}" = "riscv32" ]; then
            oe_runmake_call -C ${S} O=${B} xuantie_rv32_defconfig
         fi
      fi
      cp ${B}/.config ${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG}
      touch "$MARKER_FILE"
   fi
}

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"


KERNEL_VERSION_SANITY_SKIP="1"

# Date: Commits on Apr 4, 2025
# commit info: riscv: configs: Enhanced security and debugging capabilities
SRCREV = "2c2f42557f00857200b05170a6c43223aa10d862"
SRCREV_ntrace-patch = "858960aadeacf7ddfd67039f1553649dda9688d9"
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
