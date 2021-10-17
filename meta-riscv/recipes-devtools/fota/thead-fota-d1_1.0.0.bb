DESCRIPTION = "THead fota service for D1"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "(^d1*)"

inherit systemd
DEPENDS_append = " update-rc.d-native"

RDEPENDS_${PN} += " dbus-lib glib-2.0 bluez5 zlib "

SRC_URI = " \
            file://fota-service \
            file://fota-dbus.conf \
            file://fota.conf \
            file://thead_fota.service \
            file://thead_fota.sh \
          "

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/dbus-1/system.d
    install -d ${D}${systemd_system_unitdir}

    install -m 0755 ${WORKDIR}/fota-service                            ${D}${bindir}/
    install -m 0644 ${WORKDIR}/fota.conf                               ${D}${sysconfdir}/
    install -m 0644 ${WORKDIR}/fota-dbus.conf                          ${D}${sysconfdir}/dbus-1/system.d/

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -m 0644 ${WORKDIR}/thead_fota.service                  ${D}${systemd_system_unitdir}/haasui_app.service
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/thead_fota.sh ${D}${sysconfdir}/init.d
        update-rc.d -r ${D} thead_fota.sh start 90 5 .
    fi

}

FILES_${PN} += " ${bindir} ${sysconfdir} ${systemd_system_unitdir} "

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " already-stripped "
