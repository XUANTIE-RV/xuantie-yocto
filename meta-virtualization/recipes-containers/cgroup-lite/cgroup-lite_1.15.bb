SECTION = "devel"
SUMMARY = "Light-weight package to set up cgroups at system boot."
DESCRIPTION =  "Light-weight package to set up cgroups at system boot."
HOMEPAGE = "http://packages.ubuntu.com/source/artful/cgroup-lite"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=5d5da4e0867cf06014f87102154d0102"
SRC_URI = "https://launchpad.net/ubuntu/+archive/primary/+files/cgroup-lite_1.15.tar.xz"
SRC_URI += "file://cgroups-init"
SRC_URI[md5sum] = "1438c1f4a7227c0dedfce5f86f02591d"
SRC_URI[sha256sum] = "02f44c70ed3cf27b9e89e5266492fddf4b455336ab4e03abc85e92297537201f"

inherit allarch update-rc.d systemd

INITSCRIPT_NAME = "cgroups-init"
INITSCRIPT_PARAMS = "start 8 2 3 4 5 . stop 20 0 1 6 ."

# Keeps the sysvinit scripts out of the image if building
# where systemd is in use.
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "cgroups-init.service"
SYSTEMD_AUTO_ENABLE:${PN} = "mask"


do_install() {
	install -d ${D}/${base_bindir}
	install -m 0755 ${S}/scripts/cgroups-mount ${D}/${base_bindir}
	install -m 0755 ${S}/scripts/cgroups-umount ${D}/${base_bindir}

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/cgroups-init ${D}${sysconfdir}/init.d/cgroups-init

	install -d ${D}${systemd_unitdir}/system
	ln -sf /dev/null ${D}${systemd_unitdir}/system/cgroups-init.service
}
