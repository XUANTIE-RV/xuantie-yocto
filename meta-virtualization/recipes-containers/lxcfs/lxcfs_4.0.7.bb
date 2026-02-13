SUMMARY = "LXCFS is a userspace filesystem created to avoid kernel limitations"
LICENSE = "LGPL-2.1-or-later"

inherit autotools pkgconfig systemd

SRC_URI = " \
    https://linuxcontainers.org/downloads/lxcfs/lxcfs-${PV}.tar.gz \
    file://systemd-allow-for-distinct-build-directory.patch \
    file://systemd-ensure-var-lib-lxcfs-exists.patch \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=29ae50a788f33f663405488bc61eecb1"
SRC_URI[md5sum] = "9d963976207fb0ca4701428ae0587aeb"
SRC_URI[sha256sum] = "3f28e2f4b04c0090aaf88b72666505f0313768a5254dd48a14c43cf78c543ec8"

DEPENDS += "fuse"
RDEPENDS:${PN} += "fuse"

FILES:${PN} += "${datadir}/lxc/config/common.conf.d/*"

CACHED_CONFIGUREVARS += "ac_cv_path_HELP2MAN='false // No help2man //'"
EXTRA_OECONF += "--with-distro=unknown --with-init-script=${VIRTUAL-RUNTIME_init_manager}"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "lxcfs.service"
