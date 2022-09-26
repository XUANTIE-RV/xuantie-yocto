DESCRIPTION = "thead VI sub-system drivers"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-*"
DEPENDS = "linux-thead"

SRC_URI = " \
            git://git@gitee.com/thead-yocto/vi-kernel.git;branch=master;protocol=http \
            file://vi-kernel.service \
            file://98-vi-kernel.preset\
            file://0001-fix-isp-ry-not-free-cma-memroy-issue.patch\
          "

THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"

S = "${WORKDIR}/git"

export SYSROOT_DIR="${PKG_CONFIG_SYSROOT_DIR}"
export PROJECT_DIR?="${COREBASE}/.."
export ARCH?="riscv"
export BOARD_NAME="${MACHINEOVERRIDES}"
export BUILD_ROOT?="${TOPDIR}"
export BUILDROOT_DIR?="${BUILD_ROOT}"
export CROSS_COMPILE="riscv64-linux-"
export TOOLCHAIN_DIR?="${EXTERNAL_TOOLCHAIN}"
export LINUX_DIR?="${STAGING_KERNEL_BUILDDIR}"
export INSTALL_DIR_ROOTFS?="${IMAGE_ROOTFS}"
export INSTALL_DIR_SDK?="${SDK_DEPLOY}"
export PATH="${PKG_CONFIG_SYSROOT_DIR}/${includedir}:${PKG_CONFIG_SYSROOT_DIR}/${libdir}:${SYSROOT_DIR}:${SYSROOT_DIR}/usr/include:${SYSROOT_DIR}/usr/lib:${SYSROOT_DIR}/lib:${SYSROOT_DIR}/include:${RECIPE_SYSROOT_NATIVE}/usr/bin/riscv64-oe-linux:${COREBASE}/scripts:${COREBASE}/bitbake/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin"
export KERNEL_VERSION="$(cat ${BASE_WORKDIR}/kernel_version)"

EXTRA_OEMAKE+="BUILD_SYSTEM='YOCTO_BUILD'"

PARALLEL_MAKEINST = "-j1"
do_compile() {
    cd ${WORKDIR}/git
    oe_runmake > vi-kernel.log
}

do_install() {
  install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra

  install -d ${D}${includedir}/vvcam/isp
  install -d ${D}${includedir}/vvcam/dw200
  install -d ${D}${includedir}/vvcam/dec400
  install -d ${D}${includedir}/vvcam/vi_pre
  install -d ${D}${includedir}/vvcam/common
  install -d ${D}${includedir}/vvcam/camera
  install -d ${D}${includedir}/vvcam/native
  install -d ${D}${includedir}/vvcam/native/video
  install -d ${D}${includedir}/vvcam/dwe
  install -d ${D}${includedir}/vvcam/vse
  install -d ${D}${includedir}/vvcam/csi
  install -d ${D}${includedir}/vvcam/soc
  install -d ${D}${includedir}/vvcam/vi_pre
  install -d ${D}${includedir}/vvcam/flash_led

  install -d ${D}${includedir}/vvcam_ry/isp
  install -d ${D}${includedir}/vvcam_ry/common
  install -d ${D}${includedir}/vvcam_ry/csi
  install -d ${D}${includedir}/vvcam_ry/soc
  install -d ${D}${datadir}/vi
  install -d ${D}/lib/systemd/system
  install -d ${D}/lib/systemd/system-preset

  #ISP
  #install -m 0644 ${S}/output/rootfs/bsp/isp/ko/*.ko                              ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
  install -m 0644 ${S}/output/rootfs/bsp/isp/ko/thead_video.ko                     ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
  install -m 0644 ${S}/output/rootfs/bsp/isp/ko/vvcam_dec400.ko                    ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
  install -m 0644 ${S}/output/rootfs/bsp/isp/ko/vvcam_isp_ry.ko                    ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
  install -m 0644 ${S}/output/rootfs/bsp/isp/ko/vi_pre.ko                          ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
  install -m 0644 ${S}/output/rootfs/bsp/isp/ko/vvcam_flash_led.ko                 ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
  install -m 0644 ${S}/output/rootfs/bsp/isp/ko/vvcam_dw200.ko                     ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
  install -m 0644 ${S}/output/rootfs/bsp/isp/ko/vvcam_isp.ko                       ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
  install -m 0644 ${S}/output/rootfs/bsp/isp/ko/vvcam_sensor.ko                    ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
  install -m 0644 ${S}/output/rootfs/bsp/isp/ko/vvcam_soc.ko                       ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
  install -m 0755 ${S}/output/rootfs/bsp/isp/ko/*.sh                               ${D}${datadir}/vi

  install -m 0644 ${S}/vvcam/isp/*.h                                               ${D}${includedir}/vvcam/isp
  install -m 0644 ${S}/vvcam/common/*.h                                            ${D}${includedir}/vvcam/common
  install -m 0644 ${S}/vvcam/native/dec400/*.h                                     ${D}${includedir}/vvcam/dec400
  install -m 0644 ${S}/vvcam/dw200/*.h                                             ${D}${includedir}/vvcam/dw200
  install -m 0644 ${S}/vvcam/vse/*.h                                             ${D}${includedir}/vvcam/vse
  install -m 0644 ${S}/vvcam/dwe/*.h                                             ${D}${includedir}/vvcam/dwe
  install -m 0644 ${S}/vvcam/csi/*.h                                         ${D}${includedir}/vvcam/csi
  install -m 0644 ${S}/vvcam/soc/*.h                                         ${D}${includedir}/vvcam/soc
  install -m 0644 ${S}/vvcam/native/video/*.h                                 ${D}${includedir}/vvcam/native/video
  install -m 0644 ${S}/vvcam/native/vi_pre/*.h                                 ${D}${includedir}/vvcam/vi_pre
  install -m 0644 ${S}/vvcam/native/flash_led/*.h                                 ${D}${includedir}/vvcam/flash_led

  install -m 0644 ${S}/vvcam_ry/isp/*.h                                               ${D}${includedir}/vvcam_ry/isp
  install -m 0644 ${S}/vvcam_ry/common/*.h                                         ${D}${includedir}/vvcam_ry/common
  install -m 0644 ${S}/vvcam_ry/csi/*.h                                         ${D}${includedir}/vvcam_ry/csi
  install -m 0644 ${S}/vvcam_ry/soc/*.h                                         ${D}${includedir}/vvcam_ry/soc
  install -m 0755 ${WORKDIR}/98-vi-kernel.preset 				                        ${D}/lib/systemd/system-preset
  install -m 0755 ${WORKDIR}/vi-kernel.service 				                            ${D}/lib/systemd/system
}

#do_package_qa[noexec] = "1"

FILES_${PN} += " ${base_libdir} "
#FILES_${PN} += " ${libdir} "
FILES_${PN} += " ${datadir} "

PACKAGES = "${PN}"

# RDEPENDS_${PN} = " "
INSANE_SKIP_${PN} += " installed-vs-shipped "
