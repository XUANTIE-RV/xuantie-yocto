FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE_${PN}_d1 = "wpa_supplicant_d1.service"
SYSTEMD_AUTO_ENABLE_d1 = "enable"

SRC_URI_append_d1 = " \
    file://wpa_supplicant_d1.service \
    file://wpa_supplicant.conf-sane \
    file://defconfig \
    file://wpa_supplicant_init.sh \
"
DEPENDS_append = " update-rc.d-native"

do_install_append_d1() {
    install -m 600 ${WORKDIR}/wpa_supplicant.conf-sane-d1 ${D}${sysconfdir}/wpa_supplicant.conf

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/wpa_supplicant_d1.service ${D}${systemd_system_unitdir}
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/wpa_supplicant_init.sh ${D}${sysconfdir}/init.d
        update-rc.d -r ${D} wpa_supplicant_init.sh start 03 5 3 2 .
    fi
}

SYSTEMD_SERVICE_${PN} = "wpa_supplicant_light.service"
SYSTEMD_AUTO_ENABLE = "enable"

SRC_URI_append = " \
    file://wpa_supplicant_light.service \
    file://wpa_supplicant.conf-sane \
    file://defconfig \
    file://wpa_supplicant_init.sh \
"
DEPENDS_append = " update-rc.d-native"

do_install_append() {
    install -m 600 ${WORKDIR}/wpa_supplicant.conf-sane ${D}${sysconfdir}/wpa_supplicant.conf

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/wpa_supplicant_light.service ${D}${systemd_system_unitdir}
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/wpa_supplicant_init.sh ${D}${sysconfdir}/init.d
        update-rc.d -r ${D} wpa_supplicant_init.sh start 03 5 3 2 .
    fi
}
