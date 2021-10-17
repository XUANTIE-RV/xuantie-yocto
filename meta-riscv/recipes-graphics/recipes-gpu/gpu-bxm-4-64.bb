DESCRIPTION = " "
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-fm"

PREFERRED_VERSION_libdrm = "2.4.101"
DEPENDS = " libdrm bison-native zlib-native font-util-native font-cursor-misc process-linker video-memory linux-thead "


DEPENDS += " \
    intltool-native \
    libtool-native \
    autoconf-native \
    automake-native \
    libtool-cross \
"
inherit pkgconfig

SRC_URI = "git://git@gitlab.alibaba-inc.com/thead-linux-private/gpu_bxm_4_64.git;branch=master;protocol=ssh \
           file://.param \
           file://0001-modify-compile-for-yocto.patch \
           "

SRCREV="${AUTOREV}"

S = "${WORKDIR}/git"

export SYSROOT_DIR="${PKG_CONFIG_SYSROOT_DIR}"
export DISCIMAGE="${SYSROOT_DIR}"

export PROJECT_DIR?="${COREBASE}/.."
export BUILD_ROOT?="${PROJECT_DIR}/thead-build/light-fm"
# export ARCH?="riscv"
# export BOARD_NAME="light_fpga_fm_c910"
# export BUILDROOT_DIR?="${ROOT_DIR}/thead-build/light-fm"
# export CROSS_COMPILE="riscv64-unknown-linux-gnu-"

export TOOLCHAIN_DIR?="${EXTERNAL_TOOLCHAIN}/bin"
export KERNELDIR="${BUILD_ROOT}/tmp-glibc/work/light_fm-oe-linux/linux-thead/5.10.y-r0/linux-light_fm-standard-build"

export http_proxy="http://11.162.93.61:3128"
export https_proxy="http://11.162.93.61:3128"

# #export INSTALL_DIR_ROOTFS?="/usr/local/oecore-x86_64/sysroots/riscv64-oe-linux"
# #export INSTALL_DIR_SDK?="${BUILDROOT_DIR}"

export PATH="${HOME}/.local/bin:${PROJECT_DIR}/openembedded-core/scripts:${PROJECT_DIR}/openembedded-core/bitbake/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin:${EXTERNAL_TOOLCHAIN}/bin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/sysroots-uninative/x86_64-linux/usr/bin:${PROJECT_DIR}/openembedded-core/scripts:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64/1.0-r0/recipe-sysroot-native/usr/bin/riscv64-oe-linux:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64/1.0-r0/recipe-sysroot/usr/bin/crossscripts:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64/1.0-r0/recipe-sysroot-native/usr/sbin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64/1.0-r0/recipe-sysroot-native/usr/bin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64/1.0-r0/recipe-sysroot-native/sbin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64/1.0-r0/recipe-sysroot-native/bin:${PROJECT_DIR}/openembedded-core/bitbake/bin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/hosttools:/bin:/sbin"

#export PATH="${PROJECT_DIR}/openembedded-core/scripts:${PROJECT_DIR}/openembedded-core/bitbake/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin:${EXTERNAL_TOOLCHAIN}/bin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/sysroots-uninative/x86_64-linux/usr/bin:${PROJECT_DIR}/openembedded-core/scripts:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64/1.0-r0/recipe-sysroot-native/usr/bin/riscv64-oe-linux:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64/1.0-r0/recipe-sysroot/usr/bin/crossscripts:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64/1.0-r0/recipe-sysroot-native/usr/sbin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64/1.0-r0/recipe-sysroot-native/usr/bin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64/1.0-r0/recipe-sysroot-native/sbin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/work/riscv64-oe-linux/gpu-bxm-4-64/1.0-r0/recipe-sysroot-native/bin:${PROJECT_DIR}/openembedded-core/bitbake/bin:${PROJECT_DIR}/thead-build/light-fm/tmp-glibc/hosttools:/bin:/sbin"
export KERNEL_VERSION="$(cat ${BUILD_ROOT}/tmp-glibc/pkgdata/light-fm/kernel-depmod/kernel-abiversion)"


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
#export WINDOW_SYSTEM="nulldrmws"
export WINDOW_SYSTEM="lws-generic"
export BUILD="debug"
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
    oe_runmake info
    oe_runmake clean_driver
    oe_runmake driver
    oe_runmake clean_lib
    oe_runmake lib
    oe_runmake install_driver
    oe_runmake install_lib
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}/lib/modules/${KERNEL_VERSION}/extra/
    install -d ${D}/usr/local/bin/
    install -d ${D}/usr/lib/pkgconfig/
    install -d ${D}/lib/firmware/
    install -d ${D}/usr/local/share/pvr/shaders/
    install -d ${D}/etc/vulkan/icd.d/
    install -d ${D}/etc/OpenCL/vendors/
    install -d ${D}/etc/init.d/
    install -d ${D}/etc/udev/rules.d/

#    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_neutral/*.txt                                       ${D}/usr/local/share/pvr/shaders/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_neutral/hwperfjsonmerge.py                           ${D}/usr/local/bin/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_neutral/icdconf.json                ${D}/etc/vulkan/icd.d/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_neutral/IMG.icd                   ${D}/etc/OpenCL/vendors/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_neutral/pvrhtbd                          ${D}/usr/local/bin/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_neutral/pvrhwperf                        ${D}/usr/local/bin/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_neutral/pvrlogdump                       ${D}/usr/local/bin/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_neutral/pvrlogsplit                      ${D}/usr/local/bin/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_neutral/rgx.sh.36.52.104.182             ${D}/lib/firmware/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_neutral/rgx.fw.36.52.104.182             ${D}/lib/firmware/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_neutral/rc.pvr                           ${D}/etc/init.d/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_neutral/udev.pvr                         ${D}/etc/udev/rules.d/99-pvr.rules



    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_riscv64/*.so*                         ${D}/usr/lib/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_riscv64/pvr*                          ${D}/usr/local/bin/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_riscv64/rgx*                          ${D}/usr/local/bin/
#    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_riscv64/egl*                          ${D}/usr/local/bin/
#    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_riscv64/gles*                         ${D}/usr/local/bin/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_riscv64/hwperfbin2jsont               ${D}/usr/local/bin/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_riscv64/ocl_*                         ${D}/usr/local/bin/
    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_riscv64/rogue2d*                      ${D}/usr/local/bin/
#    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_riscv64/tearing_test                  ${D}/usr/local/bin/
#    install -m 0755 ${S}/rogue/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_riscv64/vkbonjour                     ${D}/usr/local/bin/

    install -m 0755 ${S}/rogue_km/binary_thead_linux_${WINDOW_SYSTEM}_${BUILD}/target_riscv64/*.ko                        ${D}/lib/modules/${KERNEL_VERSION}/extra/
#    install -m 0755 ${S}/rogue_km/binary_thead_linux_nullws_debug/target_x86_64/*.ko                        ${D}/usr/lib/

#    install -m 0755 ${S}/rogue/binary_thead_linux_nullws_debug/target_neutral/*.txt                                       ${D}/lib/modules/${KERNEL_VERSION}/extra/
#    install -m 0755 ${S}/rogue/binary_thead_linux_nullws_debug/target_neutral/usr/lib/*.so*                                 ${D}${libdir}/
#    install -m 0644 ${S}/rogue/binary_thead_linux_nullws_debug/target_neutral/usr/lib/pkgconfig/*                           ${D}/usr/lib/pkgconfig/
#

#    install -m 0755 ${WORKDIR}/recipe-sysroot/lib/modules/${KERNEL_VERSION}/extra/*              ${D}/lib/modules/${KERNEL_VERSION}/extra/
#    install -m 0755 ${WORKDIR}/recipe-sysroot/usr/local/share/pvr/shaders/*                 ${D}/usr/local/share/pvr/shaders/
#    install -m 0755 ${WORKDIR}/recipe-sysroot/usr/lib/*.so*                                 ${D}${libdir}/
#    install -m 0755 ${WORKDIR}/recipe-sysroot/usr/local/bin/*                               ${D}/usr/local/bin/
#    install -m 0644 ${WORKDIR}/recipe-sysroot/usr/lib/pkgconfig/*                           ${D}/usr/lib/pkgconfig/
#    install -m 0755 ${WORKDIR}/recipe-sysroot/lib/firmware/*                                ${D}/lib/firmware/
#    install -m 0755 ${WORKDIR}/recipe-sysroot/etc/vulkan/icd.d/icdconf.json                ${D}/etc/vulkan/icd.d/
#    install -m 0755 ${WORKDIR}/recipe-sysroot/etc/OpenCL/vendors/IMG.icd                   ${D}/etc/OpenCL/vendors/
#    install -m 0755 ${WORKDIR}/recipe-sysroot/etc/init.d/rc.pvr                            ${D}/etc/init.d/
#    install -m 0755 ${WORKDIR}/recipe-sysroot/etc/udev/rules.d/99-pvr.rules                ${D}/etc/udev/rules.d/
#
##    install -d ${D}${sysconfdir}/miniapp/resources/fonts
##    install -d ${D}${sysconfdir}/miniapp/resources/framework/js_modules
##    install -d ${D}${sysconfdir}/miniapp/resources/icu
##    install -d ${D}${sysconfdir}/miniapp/resources/presetpkgs
##
##    install -m 0755 ${WORKDIR}/usr/bin/appx                                          ${D}${bindir}/appx
##    install -m 0644 ${WORKDIR}/usr/lib/lib*.so*                                      ${D}${libdir}/
##    install -m 0644 ${WORKDIR}/etc/miniapp/resources/*.json                          ${D}${sysconfdir}/miniapp/resources/
##    install -m 0644 ${WORKDIR}/etc/miniapp/resources/fonts/*                         ${D}${sysconfdir}/miniapp/resources/fonts/
##    install -m 0644 ${WORKDIR}/etc/miniapp/resources/framework/js-framework.min.bin  ${D}${sysconfdir}/miniapp/resources/framework/js-framework.min.bin
##    install -m 0644 ${WORKDIR}/etc/miniapp/resources/framework/js_modules/*          ${D}${sysconfdir}/miniapp/resources/framework/js_modules/
##    install -m 0644 ${WORKDIR}/etc/miniapp/resources/presetpkgs/*                    ${D}${sysconfdir}/miniapp/resources/presetpkgs/
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
FILES_${PN} += " /usr/local/bin/ "
FILES_${PN} += " /usr/local/share/pvr/shaders/ "
FILES_${PN} += " /usr/lib/ "
FILES_${PN} += " /usr/lib/pkgconfig/ "
FILES_${PN} += " /etc/udev/rules.d/ "
FILES_${PN} += " /etc/init.d/ "
FILES_${PN} += " /etc/OpenCL/vendors/ "
FILES_${PN} += " /etc/vulkan/icd.d/ "
FILES_${PN} += " /lib/firmware/ "
FILES_${PN} += " /lib/modules/${KERNEL_VERSION}/extra/ "
#FILES_${PN} += " ${systemd_system_unitdir} "
#FILES_${PN} += " ${sysconfdir} "

#PACKAGES = "${PN}"

#INSANE_SKIP_${PN} += " debug-files already-stripped rpaths "

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " arch debug-files already-stripped dev-deps file-rdeps "
