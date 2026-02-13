FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://getty-wrapper"

do_install:append() {
    install -d ${D}${base_sbindir}
    install -m 0755 ${WORKDIR}/getty-wrapper ${D}${base_sbindir}/getty-wrapper
}
FILES:${PN} += "${base_sbindir}/getty-wrapper"
