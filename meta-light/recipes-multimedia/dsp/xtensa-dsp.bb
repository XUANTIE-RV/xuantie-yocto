DESCRIPTION = "Light DSP module driver"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-*"

DEPENDS = " openssl cmake-native python3 zlib boost linux-thead vi-bt video-memory"
RDEPENDS_${PN} = "video-memory"

SRC_URI = " \
            git://git@gitee.com/thead-yocto/xtensa_dsp.git;branch=master;protocol=http \
            file://xtensa-dsp.service \
            file://98-xtensa-dsp.preset\
          "
SRC_URI_light-fm-bsp-v1.0.6 = " \
            git://git@gitee.com/thead-yocto/xtensa_dsp.git;branch=master;protocol=http \
            "
SRC_URI_light-fm-b-bsp-v1.0.6 = " \
            git://git@gitee.com/thead-yocto/xtensa_dsp.git;branch=master;protocol=http \
       "
SRC_URI_light-fm-a-linux = " \
            git://git@gitee.com/thead-yocto/xtensa_dsp.git;branch=master;protocol=http \
            file://xtensa-dsp.service \
            file://98-xtensa-dsp.preset\
       "

SRC_URI_light-fm-b-linux = " \
            git://git@gitee.com/thead-yocto/xtensa_dsp.git;branch=master;protocol=http \
            file://xtensa-dsp.service \
            file://98-xtensa-dsp.preset\
       "

THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"

S = "${WORKDIR}/git"


export SYSROOT_DIR="${RECIPE_SYSROOT}"

export ARCH?="riscv"
export BOARD_NAME="${MACHINEOVERRIDES}"
export CROSS_COMPILE="riscv64-linux-"

export TOOLCHAIN_DIR?="${EXTERNAL_TOOLCHAIN}"
export LINUX_DIR?="${STAGING_KERNEL_BUILDDIR}"

export INSTALL_DIR_ROOTFS?="${IMAGE_ROOTFS}"
export INSTALL_DIR_SDK?="${SDK_DEPLOY}"
export PROJECT_DIR?="${COREBASE}/.."
export BUILD_ROOT?="${TOPDIR}"
export KERNEL_VERSION="$(cat ${BASE_WORKDIR}/kernel_version)"

export  ="${SYSROOT_DIR}:${SYSROOT_DIR}/usr/include:${SYSROOT_DIR}/usr/lib:${SYSROOT_DIR}/lib:${SYSROOT_DIR}/include:${RECIPE_SYSROOT_NATIVE}/usr/bin/riscv64-oe-linux:${COREBASE}/scripts:${COREBASE}/bitbake/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin"

EXTRA_OEMAKE+="BUILD_SYSTEM='YOCTO_BUILD' VISYS_SYM_PATH=${PKG_CONFIG_SYSROOT_DIR}${datadir} "
CFLAGS_append = " -I${PKG_CONFIG_SYSROOT_DIR}/usr/include/vidmem/"
LDFLAGS_append = " -L${PKG_CONFIG_SYSROOT_DIR}/usr/lib/ -lvmem"
PARALLEL_MAKEINST = "-j1"

do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra/dsp
    install -d ${D}${libdir}
    install -d ${D}${datadir}/xtensa_dsp
    install -d ${D}${base_libdir}/firmware
    install -d ${D}${includedir}/dsp/lib/inc
    install -d ${D}/lib/systemd/system
    install -d ${D}/lib/systemd/system-preset

    install -m 0644 ${S}/output/rootfs/bsp/xtensa_dsp/ko/*.ko                               ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra/dsp
    install -m 0755 ${S}/driver/xrp-kernel/*.sh                                             ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra/dsp
    install -m 0644 ${S}/output/rootfs/bsp/xtensa_dsp/lib/lib*.so*                          ${D}${libdir}
    install -m 0755 ${S}//driver/xrp-kernel/*.sh                                            ${D}${datadir}/xtensa_dsp
    install -m 0755 ${S}/output/rootfs/bsp/xtensa_dsp/test/*                                ${D}${datadir}/xtensa_dsp
    install -m 0644 ${S}/firmware/*                                                         ${D}${base_libdir}/firmware
    install -m 0644 ${S}/driver/xrp-user/include/csi_dsp*.h                                 ${D}${includedir}/dsp/lib/inc
    install -m 0755 ${WORKDIR}/98-xtensa-dsp.preset 				                        ${D}/lib/systemd/system-preset
    install -m 0755 ${WORKDIR}/xtensa-dsp.service 				                            ${D}/lib/systemd/system
}



FILES_${PN} += " ${base_libdir} "
FILES_${PN} += " ${libdir} "
FILES_${PN} += " ${datadir} "
FILES_${PN} += " ${includedir} "
PACKAGES = "${PN}"
INSANE_SKIP_${PN} += " debug-files already-stripped dev-deps file-rdeps arch ldflags"
# PACKAGES = " "

# RDEPENDS_${PN} = " "

# FILES_${PN} = " "
