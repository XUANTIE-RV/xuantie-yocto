inherit kernel
require recipes-kernel/linux/linux-yocto.inc

SRC_URI = "git://github.com/ruyisdk/linux-xuantie-kernel.git;branch=linux-6.6.36;protocol=https"


do_kernel_configme:prepend() {
    MARKER_FILE=${S}/configme
    if [ ! -f "$MARKER_FILE" ]; then
        if [ "${@bb.utils.contains('MACHINE_FEATURES', 'kcdebug', 'true', 'false', d)}" = "true" ]; then
            if [ "${TUNE_ARCH}" = "riscv64" ]; then
                # apply extra config fragment for debug kernel
                oe_runmake_call -C ${S} O=${B} xuantie_debug_defconfig
            elif [ "${TUNE_ARCH}" = "riscv32" ]; then
                oe_runmake_call -C ${S} O=${B} xuantie_rv32_debug_defconfig
            fi
        else
            if [ "${TUNE_ARCH}" = "riscv64" ]; then
                # apply extra config fragment for default kernel
                oe_runmake_call -C ${S} O=${B} xuantie_defconfig
            elif [ "${TUNE_ARCH}" = "riscv32" ]; then
                oe_runmake_call -C ${S} O=${B} xuantie_rv32_defconfig
            fi
        fi
        echo "CONFIG_LOCALVERSION="\"${LINUX_VERSION_EXTENSION}\" >> ${B}/.config
        echo "CONFIG_LOCALVERSION_AUTO=n" >> ${B}/.config
        cp ${B}/.config ${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG}
        touch "$MARKER_FILE"
    fi
}

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"


KERNEL_VERSION_SANITY_SKIP="1"

# support tee for common xuantie defconfig,Commits on Feb 3, 2026
SRCREV = "66b4411dfc5a428212939fc50b8a92c04e318a81"
LICENSE = "CLOSED"

S = "${WORKDIR}/linux-${PV}"

do_configure:append() {
    [ -d ${STAGING_KERNEL_DIR} ] && rm -rf ${STAGING_KERNEL_DIR}
    [ -f ${STAGING_KERNEL_DIR} ] && rm -rf ${STAGING_KERNEL_DIR}

    ln -s ${S}  ${STAGING_KERNEL_DIR}
}

DEBUG_PREFIX_MAP:remove = "-fmacro-prefix-map=${S}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR} \
 -fdebug-prefix-map=${S}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR} \
 -fmacro-prefix-map=${B}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR} \
 -fdebug-prefix-map=${B}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR} \
 -fdebug-prefix-map=${STAGING_DIR_HOST}= \
 -fmacro-prefix-map=${STAGING_DIR_HOST}= \
 -fdebug-prefix-map=${STAGING_DIR_NATIVE}= \
"
do_compile:append() {
   ${OBJDUMP} -d -S vmlinux > vmlinux.asm
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
    cp ${B}/vmlinux.asm ${DEPLOYDIR}
}

KCONFIG_MODE="--alldefconfig"

FILES:${KERNEL_PACKAGE_NAME}-base += "${sysconfdir}"

DEPENDS += "pahole-native"

# TOOLCHAIN:forcevariable = "clang"

# DEPENDS:append = " ${TOOLCHAIN}-cross-${TARGET_ARCH}"
# do_kernel_configme[depends] += "clang-cross-${TARGET_ARCH}:do_populate_sysroot"


# KERNEL_CC = "${CCACHE}${HOST_PREFIX}clang ${HOST_CC_KERNEL_ARCH} ${DEBUG_PREFIX_MAP} -fdebug-prefix-map=${STAGING_KERNEL_DIR}=${KERNEL_SRC_PATH} -fdebug-prefix-map=${STAGING_KERNEL_BUILDDIR}=${KERNEL_SRC_PATH} -fdebug-prefix-map=${STAGING_KERNEL_BUILDDIR}=${KERNEL_SRC_PATH}"
# KERNEL_LD = "${CCACHE}${HOST_PREFIX}ld.lld ${HOST_LD_KERNEL_ARCH} ${HOST_LD_KERNEL_ARCH}"
# KERNEL_AR = "${CCACHE}${HOST_PREFIX}llvm-ar ${HOST_AR_KERNEL_ARCH}"

# do_compile:prepend() {
# 	export LLVM=1
# }

# do_compile_kernelmodules:prepend() {
# 	export LLVM=1
# }

# python do_kernel_configcheck() {
# }
