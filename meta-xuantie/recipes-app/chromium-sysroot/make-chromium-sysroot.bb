SUMMARY = "Make a sysroot for chromium build"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = ""

DEPENDS = "nss libdrm libxkbcommon glib-2.0 dbus atk pango cairo at-spi2-atk pipewire pciutils curl libx11 libxcomposite libffi"
DEPENDS += "systemd gtk+3"
DEPENDS += "qtbase qtsvg qtdeclarative qtgraphicaleffects"
DEPENDS += "${@bb.utils.contains('MACHINECONFIG','public','th1520-bsp-proprietary','libcsi-g2d',d)}"
SRC_URI = "\
    file://sysroot_test.c \
    file://CMakeLists.txt \
"

S = "${WORKDIR}"

inherit cmake

#OECMAKE_GENERATOR = "Unix Makefiles"
#PARALLEL_MAKEINST = "-j1"

EXTRA_OECMAKE = ""

do_install() {
}

FILES:${PN} += " ${bindir} "
FILES:${PN} += " ${includedir} "
FILES:${PN} += " ${libdir} "

PROVIDES = "${PN}"
PACKAGES = "${PN}"

INSANE_SKIP:${PN} += "debug-files file-rdeps dev-deps installed-vs-shipped"
