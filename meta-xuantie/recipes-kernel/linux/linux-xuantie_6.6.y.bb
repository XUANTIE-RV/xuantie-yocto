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

# Sun Jan 25 00:52:12 2026 -0500
# riscv: mm: Fixup no5lvl failure when vaddr is invalid
SRCREV = "9c99e96ec4c065d5769137e14b5d48fa75b56830"
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

# Clang/LLVM toolchain support (activated when TOOLCHAIN=clang via toolchain-clang override)
DEPENDS:append:toolchain-clang = " clang-cross-${TARGET_ARCH}"

KERNEL_CC:toolchain-clang = "${CCACHE}${HOST_PREFIX}clang ${HOST_CC_KERNEL_ARCH} ${DEBUG_PREFIX_MAP} -fdebug-prefix-map=${STAGING_KERNEL_DIR}=${KERNEL_SRC_PATH} -fdebug-prefix-map=${STAGING_KERNEL_BUILDDIR}=${KERNEL_SRC_PATH}"
KERNEL_LD:toolchain-clang = "${CCACHE}${HOST_PREFIX}ld.lld ${HOST_LD_KERNEL_ARCH}"
KERNEL_AR:toolchain-clang = "${CCACHE}${HOST_PREFIX}llvm-ar ${HOST_AR_KERNEL_ARCH}"

EXTRA_OEMAKE:append:toolchain-clang = " LLVM=1"

do_compile:prepend:toolchain-clang() {
	export LLVM=1
}

do_compile_kernelmodules:prepend:toolchain-clang() {
	export LLVM=1
}

# kconfiglib's symbol_why.py (do_kernel_configcheck) parses Kconfig.include which
# runs as-version.sh $(CC) $(CLANG_FLAGS). When CLANG_FLAGS is unset (kconfiglib
# does not go through the kernel Makefile where Makefile.clang sets it), the
# assembler check fails because clang's integrated assembler needs -fintegrated-as.
# Fix: inject CLANG_FLAGS into the environment so kconfiglib shell calls pick it up.
python do_kernel_configcheck:prepend:toolchain-clang() {
    import os
    os.environ["CLANG_FLAGS"] = "-fintegrated-as"
}
