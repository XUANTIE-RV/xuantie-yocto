SUMMARY = "Resize ext Filesystem to the partion size"
DESCRIPTION = "Resize the ext Filesystem to use full size of the partion"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://GPL-2;md5=ef841af1f51e0456cc3c472bed908d71"

RDEPENDS_${PN} += "bash e2fsprogs-resize2fs"

COMPATIBLE_MACHINE = "(^light*)"

SRC_URI = "file://resizefs.service \
            file://resizefs \
            file://GPL-2 \
"

inherit bin_package systemd

S = "${WORKDIR}"

do_install() {
     install -d ${D}${bindir}
     install -m 0755 ${WORKDIR}/resizefs ${D}${bindir}/resizefs
     echo "sysconfdir="${sysconfdir}
     install -d ${D}${systemd_system_unitdir}
     if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
         install -m 0644 ${WORKDIR}/resizefs.service ${D}${systemd_system_unitdir}/
     fi
}


SYSTEMD_SERVICE_${PN} = "resizefs.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

FILES_${PN} += "${systemd_system_unitdir}/resizefs.service"
FILES_${PN} += "${sysconfdir}/init.d/resizefs"

PACKAGES = "${PN}"

#INSANE_SKIP_${PN} += " debug-files already-stripped rpaths "
