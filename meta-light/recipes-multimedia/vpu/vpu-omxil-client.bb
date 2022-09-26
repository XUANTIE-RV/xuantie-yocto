DESCRIPTION = "Light OpenMAX IL 1.1.2 Codec clients"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-*"

SRC_URI = " \
            git://git@gitee.com/thead-yocto/vpu-omxil-client.git;branch=master;protocol=http \
          "

THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"

S = "${WORKDIR}/git"

DEPENDS = " process-linker libomxil "
RDEPENDS_${PN} = " process-linker libomxil "

DEPENDS += " image-proprietary "
RDEPENDS_${PN} += " image-proprietary "

export SYSROOT_DIR="${PKG_CONFIG_SYSROOT_DIR}"

export ARCH?="riscv"
export BOARD_NAME="${MACHINEOVERRIDES}"
export CROSS_COMPILE="riscv64-linux-"

export TOOLCHAIN_DIR?="${EXTERNAL_TOOLCHAIN}"
export LINUX_DIR?="${STAGING_KERNEL_BUILDDIR}"

export PATH="/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"

export EXTRA_OEMAKE = " \
                      INC_PATH=${PKG_CONFIG_SYSROOT_DIR}/${includedir} \
                      LIB_PATH=${PKG_CONFIG_SYSROOT_DIR}/${libdir} \
                      "

do_install() {
    install -d ${D}${datadir}/omxil/test/bin
    install -m 0755 ${S}/output/*test        ${D}${datadir}/omxil/test/bin
}

PACKAGES = "${PN}"
FILES_${PN} = "${datadir}"

INSANE_SKIP_${PN} += " debug-files "
