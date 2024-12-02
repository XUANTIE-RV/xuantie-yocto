FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://${BPN}.service \
    file://${BPN}.sh \
    "

inherit systemd update-rc.d

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "haveged.service"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "haveged.sh"
INITSCRIPT_PARAMS:${PN} = "start 38 S . stop 9 0 6 ."

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/haveged.service ${D}${systemd_system_unitdir}
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${INIT_D_DIR}
        install -m 0644 ${WORKDIR}/haveged.sh ${D}${INIT_D_DIR}
    fi
}
