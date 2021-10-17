DESCRIPTION = "D1 nezha init files"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://GPL-2;md5=ef841af1f51e0456cc3c472bed908d71"

inherit bin_package systemd
DEPENDS_append = " update-rc.d-native"

COMPATIBLE_MACHINE = "(^d1*)"

SRC_URI = "\
    file://d1-nezha-init.service \
    file://d1-nezha-init.sh \
    file://GPL-2 "

S = "${WORKDIR}"

do_install() {

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -d ${D}${sysconfdir}/nezha/

        install -m 0644 ${WORKDIR}/d1-nezha-init.service ${D}${systemd_system_unitdir}/d1-nezha-init.service
        install -m 0755 ${WORKDIR}/d1-nezha-init.sh ${D}${sysconfdir}/nezha/d1-nezha-init.sh
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/d1-nezha-init.sh ${D}${sysconfdir}/init.d
        update-rc.d -r ${D} d1-nezha-init.sh start 02 S .
    fi
}

SYSTEMD_SERVICE_${PN} = "d1-nezha-init.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

FILES_${PN} += " ${systemd_system_unitdir} "
FILES_${PN} += " ${sysconfdir} "

PACKAGES = "${PN}"
