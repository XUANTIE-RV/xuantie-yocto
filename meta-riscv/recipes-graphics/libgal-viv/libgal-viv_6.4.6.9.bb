SUMMARY = "User space HAL interface to Vivante G2D module"
DESCRIPTION = "The runtime library for accessing services provided by G2D module. \
G2D stands for \"Graphic 2D\" which is a module provided by Vivante Corp."
HOMEPAGE = "https://code.aone.alibaba-inc.com/thead-linux-private/libgal-viv"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://Makefile;beginline=2;endline=10;md5=3cd7523257cbc2b0c9ab8b45d15ba22a"

COMPATIBLE_MACHINE = "light-fm"

PROVIDES = "libgal-viv"

S = "${WORKDIR}/git"

SRCREV="${AUTOREV}"
SRC_URI = "git://git@gitlab.alibaba-inc.com/thead-linux-private/libgal-viv.git;branch=master;protocol=ssh"

EXTRA_OEMAKE += " install"
do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}/gal-viv

    install -m 0755 ${S}/build/sdk/drivers/*.so ${D}${libdir}
    cp -r ${S}/build/sdk/include/HAL/* ${D}${includedir}/gal-viv
}

PACKAGES = "${PN}"
FILES_${PN} += " ${libdir}"
FILES_${PN} += " ${includedir}"

INSANE_SKIP_${PN} += " debug-files"
