SUMMARY = "Read only rootfs with overlay init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
DEPENDS = "virtual/kernel"

RDEPENDS_${PN} = "packagegroup-core-boot util-linux-blkid e2fsprogs-mke2fs u-boot"

SRC_URI = "file://init-readonly-rootfs-overlay-boot.sh"

S = "${WORKDIR}"

do_install() {
        install -m 0755 ${WORKDIR}/init-readonly-rootfs-overlay-boot.sh ${D}/init
        install -d "${D}/media/rfs/ro"
        install -d "${D}/media/rfs/rw"
}

FILES_${PN} += " /init /media/rfs"

# Due to kernel dependency
PACKAGE_ARCH = "${MACHINE_ARCH}"
