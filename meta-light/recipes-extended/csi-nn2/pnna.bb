DESCRIPTION = "CSI-NN2 for PowerVR neural network accelerator"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = ""

COMPATIBLE_MACHINE = "(^light*)"

DEPENDS = " "
RDEPENDS_${PN} += " bash "

SRC_URI = " \
            file://usr/lib/libshl_pnna.so \
          "

S = "${WORKDIR}"

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${S}/usr/lib/libshl_pnna.so                          ${D}${libdir}
}

FILES_${PN} += " ${libdir} "

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files already-stripped dev-deps file-rdeps "
