DESCRIPTION = "thead light fm ddr performance monitor driver"
HOMEPAGE = "https://code.aone.alibaba-inc.com/thead-linux-private/thead-ddr-pmu"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = ""

COMPATIBLE_MACHINE = "light-fm"

DEPENDS = " linux-thead "
RDEPENDS_${PN} += " perf "

SRC_URI = " \
            git://git@gitlab.alibaba-inc.com/thead-linux-private/thead-ddr-pmu.git;branch=master;protocol=ssh \
          "

SRCREV="${AUTOREV}"

S = "${WORKDIR}/git"

export SYSROOT_DIR="${RECIPE_SYSROOT}"

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
    #plain "IMAGE_ROOTFS=${IMGAE_ROOTFS}"
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra

    install -m 0644 ${S}/output/rootfs/bsp/ddr-pmu/ko/*.ko                               ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
}

FILES_${PN} += " ${base_libdir} "

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files already-stripped"

