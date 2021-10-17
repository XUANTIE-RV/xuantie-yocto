DESCRIPTION = "thead neural network accelerator driver"
HOMEPAGE = "https://code.aone.alibaba-inc.com/thead-linux-private/npu_ax3386"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = ""

COMPATIBLE_MACHINE = "light-fm"

DEPENDS = " openssl cmake-native zlib linux-thead "
RDEPENDS_${PN} += " bash openssl python3 zlib boost "

SRC_URI = " \
            git://git@gitlab.alibaba-inc.com/thead-linux-private/npu_ax3386.git;branch=master;protocol=ssh \
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
    install -d ${D}${datadir}/npu
    install -d ${D}${datadir}/npu/test
    install -d ${D}${datadir}/npu/test/bin
    install -d ${D}${datadir}/npu/test/resource/cnn_testbench
    install -d ${D}${datadir}/npu/test/resource/cnn_testbench/lenet
    install -d ${D}${datadir}/npu/test/resource/cnn_testbench/lenet/OCM_1M5
    install -d ${D}${datadir}/npu/test/resource/nnvm_testbench

    install -m 0644 ${S}/output/rootfs/bsp/npu/ko/*.ko                               ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0644 ${S}/output/rootfs/bsp/npu/lib/lib*.so*                          ${D}${libdir}
    install -m 0755 ${S}/output/rootfs/bsp/npu/ko/*.sh                               ${D}${datadir}/npu
    install -m 0755 ${S}/output/rootfs/bsp/npu/test/run_*_testbench.sh               ${D}${datadir}/npu
    install -m 0755 ${S}/output/rootfs/bsp/npu/test/*_testbench                      ${D}${datadir}/npu/test
    install -m 0755 ${S}/output/rootfs/bsp/npu/test/bin/*                            ${D}${datadir}/npu/test/bin
    install -m 0755 ${S}/output/rootfs/bsp/npu/test/resource/cnn_testbench/*.txt     ${D}${datadir}/npu/test/resource/cnn_testbench
    install -m 0755 ${S}/output/rootfs/bsp/npu/test/resource/cnn_testbench/lenet/*.bin   ${D}${datadir}/npu/test/resource/cnn_testbench/lenet
    install -m 0755 ${S}/output/rootfs/bsp/npu/test/resource/cnn_testbench/lenet/OCM_1M5/*   ${D}${datadir}/npu/test/resource/cnn_testbench/lenet/OCM_1M5
    install -m 0755 ${S}/output/rootfs/bsp/npu/test/resource/nnvm_testbench/*        ${D}${datadir}/npu/test/resource/nnvm_testbench
}

FILES_${PN} += " ${base_libdir} "
FILES_${PN} += " ${libdir} "
FILES_${PN} += " ${datadir} "

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files already-stripped dev-deps file-rdeps "
