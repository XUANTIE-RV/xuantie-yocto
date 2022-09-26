SUMMARY = "Enable the Light bluetooth module"
DESCRIPTION = "Light board bluetooth"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://rtk_hciattach;md5=6301061798b06a2edb5e8470725ae018 \
                    file://rtl8723d_config;md5=432f0fce7f1bafab50be9d65a5d6a632 \
                    file://rtl8723d_fw;md5=78f647ccb55d4c7f402d8f06511e6e81"

SRC_URI = "\
    file://light-bt.service \
    file://bt-init.sh \
    file://rtk_hciattach \
    file://rtl8723d_config \
    file://rtl8723d_fw \
"

inherit bin_package systemd
DEPENDS_append = " update-rc.d-native"


S = "${WORKDIR}"

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${base_libdir}/firmware/rtlbt/
    install -m0755 ${B}/rtk_hciattach ${D}${bindir}
    install -m0644 ${B}/rtl8723d_config ${D}${base_libdir}/firmware/rtlbt/
    install -m0644 ${B}/rtl8723d_fw ${D}${base_libdir}/firmware/rtlbt/

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/light-bt.service ${D}${systemd_system_unitdir}

        install -d ${D}${sysconfdir}/
        install -m 0755 ${WORKDIR}/bt-init.sh ${D}${sysconfdir}/
    fi
}

SYSTEMD_SERVICE_${PN} = "light-bt.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

FILES_${PN} += "\
    ${bindir} \
    ${systemd_system_unitdir} \
    ${sysconfdir} \
"

FILES_${PN}-dbg += "${bindir}/.debug"