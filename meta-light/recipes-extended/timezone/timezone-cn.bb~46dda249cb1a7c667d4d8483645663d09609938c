DESCRIPTION = "Set dafault timezone"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://GPL-2;md5=ef841af1f51e0456cc3c472bed908d71"

COMPATIBLE_MACHINE = "(^d1*)"

SRC_URI = "\
    file://Shanghai \
    file://GPL-2 \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/Shanghai ${D}${sysconfdir}/localtime
}

FILES_${PN} += " ${sysconfdir} "
