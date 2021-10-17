SUMMARY = "CSI G2D HAL interfaces for using Vivante G2D module"
DESCRIPTION = "The runtime library for using services provide by Vivante G2D module."
HOMEPAGE = "https://code.aone.alibaba-inc.com/thead-linux-private/libcsi-g2d"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-fm"

PROVIDES = "libcsi-g2d"

S = "${WORKDIR}/git"

SRCREV = "${AUTOREV}"
SRC_URI = "git://git@gitlab.alibaba-inc.com/thead-linux-private/libcsi-g2d.git;branch=master;protocol=ssh"


EXTRA_OEMAKE += " install"
do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}/csi-g2d

    install -m 0755 ${S}/build/libs/*.so ${D}${libdir}
    cp -r ${S}/build/include/* ${D}${includedir}/csi-g2d
}

CFLAGS_append = " -I${STAGING_INCDIR}/gal-viv"

PACKAGES = "${PN}"
FILES_${PN} += " ${libdir}"
FILES_${PN} += " ${includedir}"

DEPENDS += " libgal-viv"

INSANE_SKIP_${PN} += " debug-files"
