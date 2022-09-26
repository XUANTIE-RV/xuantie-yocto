DESCRIPTION = "Light process linker module"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-*"

SRC_URI = " \
            git://git@gitee.com/thead-yocto/process-linker.git;branch=master;protocol=http \
          "

THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"

S = "${WORKDIR}/git"

DEPENDS = "video-memory"
RDEPENDS_${PN} = "video-memory"

export SYSROOT_DIR="${PKG_CONFIG_SYSROOT_DIR}"

export ARCH?="riscv"
export BOARD_NAME="${MACHINEOVERRIDES}"
export CROSS_COMPILE="riscv64-linux-"

export TOOLCHAIN_DIR?="${EXTERNAL_TOOLCHAIN}"
export LINUX_DIR?="${STAGING_KERNEL_BUILDDIR}"

export PATH="/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"

export EXTRA_OEMAKE = "INC_PATH=${PKG_CONFIG_SYSROOT_DIR}/${includedir} LIB_PATH=${PKG_CONFIG_SYSROOT_DIR}/${libdir}"

do_install() {
    install -d ${D}${libdir}/plink
    install -d ${D}${includedir}/plink
    install -d ${D}${datadir}/plink/test/bin

    install -m 0755 ${S}/output/*.so          ${D}${libdir}
    install -m 0755 ${S}/output/*.so          ${D}${libdir}/plink
    install -m 0755 ${S}/output/plinkserver   ${D}${datadir}/plink/test/bin
    install -m 0755 ${S}/output/plinkclient   ${D}${datadir}/plink/test/bin
    install -m 0755 ${S}/output/plinkstitcher ${D}${datadir}/plink/test/bin
    install -m 0644 ${S}/inc/*                ${D}${includedir}/plink
}

FILES_${PN} = "${libdir} ${datadir} ${includedir}"
PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files staticdev "
