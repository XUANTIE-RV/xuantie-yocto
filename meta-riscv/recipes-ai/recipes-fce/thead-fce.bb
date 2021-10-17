DESCRIPTION = "thead feature comparasion engine driver"
HOMEPAGE = "https://code.aone.alibaba-inc.com/thead-linux-private/fce_thead"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = ""

COMPATIBLE_MACHINE = "light-fm"

DEPENDS = " linux-thead "

SRC_URI = " \
            git://git@gitlab.alibaba-inc.com/thead-linux-private/fce_thead.git;branch=master;protocol=ssh \
          "

SRCREV="${AUTOREV}"

S = "${WORKDIR}/git"

export SYSROOT_DIR="${RECIPE_SYSROOT}"
#export SYSROOT_DIR="${RECIPE_SYSROOT_NATIVE}"

export PROJECT_DIR?="${COREBASE}/.."
export ARCH?="riscv"
export BOARD_NAME="light_fpga_fm_c910"
export BUILD_ROOT?="${PROJECT_DIR}/thead-build/light-fm"
export BUILDROOT_DIR?="${BUILD_ROOT}"
export CROSS_COMPILE="riscv64-linux-"
export TOOLCHAIN_DIR?="${EXTERNAL_TOOLCHAIN}"
export LINUX_DIR?="${BUILD_ROOT}/tmp-glibc/work/light_fm-oe-linux/linux-thead/5.10.y-r0/linux-light_fm-standard-build"

export INSTALL_DIR_ROOTFS?="${BUILD_ROOT}/tmp-glibc/work/light_fm-oe-linux/core-image-minimal/1.0-r0/rootfs"
export INSTALL_DIR_SDK?="${BUILD_ROOT}/tmp-glibc/deploy/sdk"

export PATH="${SYSROOT_DIR}:${SYSROOT_DIR}/usr/include:${SYSROOT_DIR}/usr/lib:${SYSROOT_DIR}/lib:${SYSROOT_DIR}/include:${RECIPE_SYSROOT_NATIVE}/usr/bin/riscv64-oe-linux:${COREBASE}/scripts:${COREBASE}/bitbake/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin"
export KERNEL_VERSION="$(cat ${BUILD_ROOT}/tmp-glibc/pkgdata/light-fm/kernel-depmod/kernel-abiversion)"

EXTRA_OEMAKE+="BUILD_SYSTEM='YOCTO_BUILD'"

PARALLEL_MAKEINST = "-j1"

do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
    install -d ${D}${libdir}
    install -d ${D}${datadir}/fce
    install -d ${D}${datadir}/fce/test
    install -d ${D}${datadir}/fce/test/resource/featurelib/anti-incre_softmax
    install -d ${D}${datadir}/fce/test/resource/input

    install -m 0755 ${S}/output/rootfs/bsp/fce/ko/*.ko                               ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0755 ${S}/output/rootfs/bsp/fce/lib/lib*.so*                          ${D}${libdir}
    install -m 0755 ${S}/output/rootfs/bsp/fce/test/*.sh                             ${D}${datadir}/fce
    install -m 0755 ${S}/output/rootfs/bsp/fce/test/fce_*                            ${D}${datadir}/fce/test
    install -m 0755 ${S}/output/rootfs/bsp/fce/test/resource/featurelib/anti-incre_softmax/*          ${D}${datadir}/fce/test/resource/featurelib/anti-incre_softmax
    install -m 0755 ${S}/output/rootfs/bsp/fce/test/resource/input/*                       ${D}${datadir}/fce/test/resource/input
}

FILES_${PN} += " ${base_libdir} "
FILES_${PN} += " ${libdir} "
FILES_${PN} += " ${datadir} "

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files already-stripped"

