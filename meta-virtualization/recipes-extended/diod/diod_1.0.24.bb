SUMMARY = "Diod is a user space server for the kernel v9fs client."
DESCRIPTION = "\
Diod is a user space server for the kernel v9fs client (9p.ko, 9pnet.ko). \
Although the kernel client supports several 9P variants, diod only supports \
9P2000.L, and only in its feature-complete form, as it appeared in 2.6.38."
SECTION = "console/network"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

PV = "1.0.24+git${SRCPV}"
SRCREV = "0ea3fe3d829b5085307cd27a512708d99ef48199"
SRC_URI = "git://github.com/chaos/diod.git;protocol=https;branch=master \
           file://diod \
           file://diod.conf \
           file://0001-build-allow-builds-to-work-with-separate-build-dir.patch \
           file://0002-auto.diod.in-remove-bashisms.patch \
           file://0001-diod-ops.c-add-header-file-for-makedev.patch \
           file://0001-drod-add-option-to-config-systemddir.patch \
          "
DEPENDS = "libcap ncurses tcp-wrappers lua"

EXTRA_OECONF = "--with-systemddir=${systemd_unitdir}/system"

S = "${WORKDIR}/git"

inherit autotools systemd

do_install:append () {
        # install our init based on start-stop-daemon
        install -D -m 0755 ${WORKDIR}/diod ${D}${sysconfdir}/init.d/diod
        # install a real(not commented) configuration file for diod
        install -m 0644 ${WORKDIR}/diod.conf ${D}${sysconfdir}/diod.conf
}

FILES:${PN} += "${systemd_unitdir}"
