DESCRIPTION = " "
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-*"

PREFERRED_VERSION_libdrm = "2.4.101"
DEPENDS = " libdrm bison-native zlib-native font-util-native font-cursor-misc process-linker video-memory linux-thead wayland wayland-native wayland-protocols"
RDEPENDS_${PN} = " libpvr-mesa-wsi"

DEPENDS += " \
    intltool-native \
    libtool-native \
    autoconf-native \
    automake-native \
    libtool-cross \
"
inherit pkgconfig

SRC_URI = "git://git@gitee.com/thead-yocto/gpu_bxm_4_64-kernel.git;branch=master;protocol=http \
           file://.param \
           file://0001-delete-um-for-yocto.patch \
           file://0001-support-parallel-make-for-yocto.patch \
           file://pvrsrvkm.conf \
           "

THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"

S = "${WORKDIR}/git"

export SYSROOT_DIR="${PKG_CONFIG_SYSROOT_DIR}"
export DISCIMAGE="${SYSROOT_DIR}"

export PROJECT_DIR?="${COREBASE}/.."
export BUILD_ROOT?="${TOPDIR}"

export TOOLCHAIN_DIR?="${EXTERNAL_TOOLCHAIN}/bin"
export KERNELDIR?="${STAGING_KERNEL_BUILDDIR}"

export http_proxy="http://11.162.93.61:3128"
export https_proxy="http://11.162.93.61:3128"

export PATH="${HOME}/.local/bin:${PROJECT_DIR}/openembedded-core/scripts:${PROJECT_DIR}/openembedded-core/bitbake/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin:${EXTERNAL_TOOLCHAIN}/bin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/sysroots-uninative/x86_64-linux/usr/bin:${PROJECT_DIR}/openembedded-core/scripts:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64-kernel/1.0-r0/recipe-sysroot-native/usr/bin/riscv64-oe-linux:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64-kernel/1.0-r0/recipe-sysroot/usr/bin/crossscripts:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64-kernel/1.0-r0/recipe-sysroot-native/usr/sbin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64-kernel/1.0-r0/recipe-sysroot-native/usr/bin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64-kernel/1.0-r0/recipe-sysroot-native/sbin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64-kernel/1.0-r0/recipe-sysroot-native/bin:${PROJECT_DIR}/openembedded-core/bitbake/bin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/hosttools:/bin:/sbin"

#export PATH="${PROJECT_DIR}/openembedded-core/scripts:${PROJECT_DIR}/openembedded-core/bitbake/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin:${EXTERNAL_TOOLCHAIN}/bin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/sysroots-uninative/x86_64-linux/usr/bin:${PROJECT_DIR}/openembedded-core/scripts:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64-kernel/1.0-r0/recipe-sysroot-native/usr/bin/riscv64-oe-linux:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64-kernel/1.0-r0/recipe-sysroot/usr/bin/crossscripts:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64-kernel/1.0-r0/recipe-sysroot-native/usr/sbin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64-kernel/1.0-r0/recipe-sysroot-native/usr/bin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64-kernel/1.0-r0/recipe-sysroot-native/sbin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64-kernel/1.0-r0/recipe-sysroot-native/bin:${PROJECT_DIR}/openembedded-core/bitbake/bin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/hosttools:/bin:/sbin"
export KERNEL_VERSION="$(cat ${BASE_WORKDIR}/kernel_version)"


# export ROOT_DIR="/home/zhangjb/riscv_yocto"
# export PATH+=:$(ROOT_DIR)/buildroot/output/host/bin
# export WINDOW_SYSTEM="nullws"
# export MIPS_ELF_ROOT=$(ROOT_DIR)/gpu_bxm_4_64/tools/mips-mti-elf/2014.07-1
# export SYSROOT="${SYSROOT_DIR}"
# export DISCIMAGE="${SYSROOT_DIR}"
# export KERNELDIR=${LINUX_DIR}""
# export BUILD="debug"

#export WINDOW_SYSTEM="xorg"
#export WINDOW_SYSTEM="wayland"
export WINDOW_SYSTEM="nulldrmws"
#export WINDOW_SYSTEM="lws-generic"
#export BUILD="debug"
export BUILD="release"
#export SUPPORT_VK_PLATFORMS="wayland"
#export MESA_WSI="1"
export LINUX_ROOT="${S}"
export LLVM_BUILD_DIR="${S}/tools/llvm"
export VERSION="6052913"
# export RGX_BVNC="36.52.104.182"
#export DRIVERDIR=rogue_km-$(DDK_VERSION)
#export LIBDIR=rogue-$(DDK_VERSION)
#export CC="gcc"


# PARALLEL_MAKEINST = "-j1"

#EXTRA_OEMAKE += "VERBOSE=1"

do_compile() {
    oe_runmake driver
    oe_runmake install_driver
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}/etc/modules-load.d/
    install -d ${D}/lib/modules/${KERNEL_VERSION}/extra/
    install -m 0755 ${WORKDIR}/pvrsrvkm.conf ${D}/etc/modules-load.d/

    install -m 0755 ${S}/rogue_km/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_riscv64/*.ko                        ${D}/lib/modules/${KERNEL_VERSION}/extra/
}

# PACKAGES = " "

# RDEPENDS_${PN} = " "

# FILES_${PN} = " "

# SYSTEMD_SERVICE_${PN} = "haasui_app.service"
# SYSTEMD_AUTO_ENABLE_${PN} = "enable"

FILES_${PN} += " ${base_libdir} "
FILES_${PN} += " ${datadir} "
FILES_${PN} += " ${bindir} "
FILES_${PN} += " ${libdir} "
FILES_${PN} += " /lib/modules/${KERNEL_VERSION}/extra/ "

#PACKAGES = "${PN}"

#INSANE_SKIP_${PN} += " debug-files already-stripped rpaths "

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " arch debug-files already-stripped dev-deps file-rdeps "
