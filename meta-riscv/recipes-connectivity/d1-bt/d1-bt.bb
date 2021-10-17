SUMMARY = "Enable the D1 bluetooth module"
DESCRIPTION = "D1 board bluetooth"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://hciattach-d1;md5=5810644bac26e938e5f4c9f0ddba2fae"

SRC_URI = "\
    file://d1-bt.service \
    file://d1-bt-init.sh \
    file://hciattach-d1 \
    file://d1-bt-rc.sh \
"

inherit bin_package systemd
DEPENDS_append = " update-rc.d-native"


S = "${WORKDIR}"

do_install () {
    install -d ${D}${bindir}
    install -m0755 ${B}/hciattach-d1 ${D}${bindir}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/d1-bt.service ${D}${systemd_system_unitdir}

        install -d ${D}${sysconfdir}/nezha
        install -m 0755 ${WORKDIR}/d1-bt-init.sh ${D}${sysconfdir}/nezha
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/d1-bt-rc.sh ${D}${sysconfdir}/init.d
        update-rc.d -r ${D} d1-bt-rc.sh start 99 5 .
    fi

}

SYSTEMD_SERVICE_${PN} = "d1-bt.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

FILES_${PN} += "\
    ${bindir} \
    ${systemd_system_unitdir} \
    ${sysconfdir}/nezha \
"

FILES_${PN}-dbg += "${bindir}/.debug"