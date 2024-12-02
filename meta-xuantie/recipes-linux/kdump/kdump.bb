SUMMARY = "Enable the kdump service"
DESCRIPTION = "kdump service"
LICENSE = "CLOSED"

TARGET_CC_ARCH += "${LDFLAGS}"

PV = "2.0.20"
SRC_URI = " \
	   file://kexec \
	   file://vmcore-dmesg \
	   file://kdump-tools \
	   file://kdump-conf \
	   file://kdump.service \
	   file://rootfs.cpio.gz;unpack=0 \
	   "
inherit bin_package systemd update-rc.d

SRCREV = "${AUTOREV}"
board ?= "${MACHINE_ARCH}"
VMLINUXPATH ?= "${DEPLOY_DIR_IMAGE}"
do_install[depends] += "linux-xuantie:do_deploy"

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${localstatedir}/lib/kdump

    install -m 0755 ${WORKDIR}/kexec  ${D}${bindir}
    install -m 0755 ${WORKDIR}/vmcore-dmesg  ${D}${bindir}
    install -m 0755 ${WORKDIR}/kdump-conf ${D}${sysconfdir}/default
    install -m 0755 ${WORKDIR}/kdump-tools ${D}${bindir}
    install -m 0755 ${WORKDIR}/rootfs.cpio.gz ${D}${localstatedir}/lib/kdump

    install -m 0755 ${VMLINUXPATH}/vmlinux ${D}${localstatedir}/lib/kdump

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -m 0644 ${WORKDIR}/kdump.service ${D}${systemd_system_unitdir}/kdump.service
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${INIT_D_DIR}
        ln -srf ${D}${bindir}/kdump-tools ${D}${INIT_D_DIR}/kdump-tools.sh
    fi

}

SYSTEMD_SERVICE:${PN} = "kdump.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

INITSCRIPT_NAME = "kdump-tools.sh"
INITSCRIPT_PARAMS = "start 99 S ."

FILES:${PN} += " ${bindir} "
FILES:${PN} += " ${sysconfdir} "
FILES:${PN} += " ${systemd_system_unitdir} "
FILES:${PN} += " ${localstatedir} "

PACKAGES = "${PN}"

INSANE_SKIP:${PN} += " debug-files already-stripped rpaths "
