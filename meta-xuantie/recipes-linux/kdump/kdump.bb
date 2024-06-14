SUMMARY = "Enable the kdump service"
DESCRIPTION = "kdump service"
LICENSE = "CLOSED"


TARGET_CC_ARCH += "${LDFLAGS}"

SRC_URI = " \
	   file://kexec \
	   file://vmcore-dmesg \
	   file://kdump-tools \
	   file://kdump-conf \
	   file://kdump.service \
	   "
inherit bin_package systemd

SRCREV = "${AUTOREV}"

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${systemd_system_unitdir}

    install -m 0755 ${WORKDIR}/kexec  ${D}${bindir}
    install -m 0755 ${WORKDIR}/vmcore-dmesg  ${D}${bindir}
    install -m 0755 ${WORKDIR}/kdump-conf ${D}${sysconfdir}/default
    install -m 0755 ${WORKDIR}/kdump-tools ${D}${bindir}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -m 0644 ${WORKDIR}/kdump.service ${D}${systemd_system_unitdir}/kdump.service
    fi

}

SYSTEMD_SERVICE_${PN} = "kdump.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

FILES_${PN} += " ${bindir} "
FILES_${PN} += " ${sysconfdir} "
FILES_${PN} += " ${systemd_system_unitdir} "

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files already-stripped rpaths "
