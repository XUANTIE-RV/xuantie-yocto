SUMMARY = "Mount UbiFS using sysvinit/systemd"
DESCRIPTION = "D1 board mount ubifs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://licenses/GPL-2;md5=b011d03b29391e580476a86ca2551277"

SRC_URI = "\
    file://mount-overlayfs.service \
    file://mount_root.sh \
    file://licenses/GPL-2 \
    file://overlay.sh \
"

inherit bin_package systemd
DEPENDS_append = " update-rc.d-native"

S = "${WORKDIR}"

do_install () {
    install -d ${D}/rom
    install -d ${D}/data
    install -d ${D}/mnt/UDISK
    install -d ${D}/mnt/SDCARD

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/overlay.sh ${D}${sysconfdir}/init.d
        update-rc.d -r ${D} overlay.sh start 01 S .
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}/overlay
        install -d ${D}${sysconfdir}/nezha
        install -d ${D}${systemd_system_unitdir}

        install -m 0644 ${WORKDIR}/mount-overlayfs.service ${D}${systemd_system_unitdir}
        install -m 0755 ${WORKDIR}/mount_root.sh ${D}${sysconfdir}/nezha
    fi
}

SYSTEMD_SERVICE_${PN} = "mount-overlayfs.service"
# SYSTEMD_AUTO_ENABLE_${PN} = "enable"

FILES_${PN} += "\
    ${root_prefix}/overlay \
    ${root_prefix}/rom \
    ${root_prefix}/mnt \
    ${systemd_system_unitdir} \
    ${sysconfdir} \
"
