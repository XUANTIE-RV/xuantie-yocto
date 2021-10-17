DESCRIPTION = "ubi utils for D1"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://GPL-2;md5=ef841af1f51e0456cc3c472bed908d71"

COMPATIBLE_MACHINE = "(^d1*)"

SRC_URI = " \
            file://fw_env.config \
            file://fw_printenv \
            file://fw_setenv \
            file://ubiblock \
            file://ubinfo \
            file://ubiupdatevol \
            \
            file://GPL-2 \
          "

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}
    install -d ${D}${base_sbindir}
    install -d ${D}${sbindir}

    install -m 0644 ${WORKDIR}/fw_env.config                           ${D}${sysconfdir}/
    install -m 0755 ${WORKDIR}/fw_printenv                             ${D}${base_sbindir}/
    install -m 0755 ${WORKDIR}/fw_setenv                               ${D}${base_sbindir}/
    install -m 0755 ${WORKDIR}/ubiblock                                ${D}${sbindir}/
    install -m 0755 ${WORKDIR}/ubinfo                                  ${D}${sbindir}/
    install -m 0755 ${WORKDIR}/ubiupdatevol                            ${D}${sbindir}/
}

FILES_${PN} += " ${sysconfdir} ${base_sbindir} ${sbindir} "

INSANE_SKIP_${PN} += " already-stripped "
