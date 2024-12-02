SUMMARY = "Resize ext Filesystem to the partion size"
DESCRIPTION = "Resize the ext Filesystem to use full size of the partion"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://GPL-2;md5=ef841af1f51e0456cc3c472bed908d71"

RDEPENDS:${PN} += "bash e2fsprogs-resize2fs util-linux-fdisk"

SRC_URI = "file://resizefs.service \
            file://resizefs \
            file://GPL-2 \
"

PR = "r1"

inherit bin_package systemd update-rc.d

S = "${WORKDIR}"

INITSCRIPT_NAME = "resizefs"
INITSCRIPT_PARAMS = "start 03 1 2 3 4 5 ."

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/resizefs ${D}${bindir}/resizefs
    echo "sysconfdir="${sysconfdir}
    install -d ${D}${systemd_system_unitdir}
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -m 0644 ${WORKDIR}/resizefs.service ${D}${systemd_system_unitdir}/
    fi
    install -d ${D}${sysconfdir}/init.d
    ln -srf ${D}${bindir}/resizefs ${D}${sysconfdir}/init.d/resizefs
}


SYSTEMD_SERVICE:${PN} = "resizefs.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
