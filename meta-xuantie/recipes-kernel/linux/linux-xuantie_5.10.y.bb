inherit kernel
require recipes-kernel/linux/linux-yocto.inc

XUANTIE_GIT = "git://git@gitee.com/xuantie-yocto/linux.git"
XUANTIE_GIT_MIRRORS = "git://git@github.com/XUANTIE-RV/linux.git"
MIRRORS += "${XUANTIE_GIT} ${XUANTIE_GIT_MIRRORS}"


SRC_URI = " \
            ${XUANTIE_GIT_MIRRORS};branch=linux-5.10.4;protocol=https \
            ${XUANTIE_GIT};branch=linux-5.10.4;protocol=https \
            file://0001-isa-zicsr-zifencei.patch \
            file://0002-perf-annotate-Fix-build-error.patch \
            file://0003-perf-build-Disable-fewer-flex-warnings.patch \
            file://0004-perf-build-Disable-fewer-bison-warnings.patch \
            file://local-version.cfg \
            file://kernel.svpbmt.cfg \
"

DEFAULT_CFG = " \
            file://kernel.12HZ.cfg \
            file://kernel.net.cfg \
            file://kernel.optee.cfg \
"

DEBUG_CFG = " \
            file://kernel.torture-test.cfg \
            file://kernel.net.cfg \
            file://kernel.debug.cfg \
            file://kernel.ftrace.cfg \
            file://kernel.optee.cfg \
"

SRC_URI:append = " ${@bb.utils.contains('MACHINE_FEATURES', 'kcdebug', '${DEBUG_CFG}', '${DEFAULT_CFG}', d)}"
SRC_URI:remove = " ${@bb.utils.contains('MACHINE_FEATURES', 'kcvector07', 'file://kernel.svpbmt.cfg', '', d)}"
SRC_URI:append = " ${@bb.utils.contains('MACHINE_FEATURES', 'kcvlen256', 'file://kernel.vlen256.cfg', '', d)}"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"


KERNEL_VERSION_SANITY_SKIP="1"

# Date: Thu Nov 21 10:54:xx 2024 +0800
# commit info: merge-tee-updates-from-optee-3.14.0
SRCREV = "b56a88cbb3bbb051197aeedeb93675001834460c"
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
