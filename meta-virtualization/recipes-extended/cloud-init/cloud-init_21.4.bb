DESCRIPTION = "Init scripts for use on cloud images"
HOMEPAGE = "https://github.com/canonical/cloud-init"
SECTION = "devel/python"
LICENSE = "GPL-3.0-only | Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c6dd79b6ec2130a3364f6fa9d6380408 \
    file://LICENSE-GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
    file://LICENSE-Apache2.0;md5=3b83ef96387f14655fc854ddc3c6bd57 \
"

SRCREV = "a97fd062f7dbd4b824fd006edd08927ef9dbf24a"
SRC_URI = "git://github.com/canonical/cloud-init;branch=main;protocol=https \
    file://cloud-init-source-local-lsb-functions.patch \
    file://0001-setup.py-check-for-install-anywhere-in-args.patch \
    file://0001-setup.py-respect-udevdir-variable.patch \
"

S = "${WORKDIR}/git"

DISTUTILS_INSTALL_ARGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '--init-system=sysvinit_deb', '', d)}"
DISTUTILS_INSTALL_ARGS:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--init-system=systemd', '', d)}"

do_install:append() {
    ln -s ${libdir}/${BPN}/uncloud-init ${D}${sysconfdir}/cloud/uncloud-init
    ln -s ${libdir}/${BPN}/write-ssh-key-fingerprints ${D}${sysconfdir}/cloud/write-ssh-key-fingerprints
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
       install -m 755 -d ${D}${sysconfdir}/init.d/
       install -m 755 ${S}/sysvinit/redhat/* ${D}${sysconfdir}/init.d/
    fi

}

inherit pkgconfig
inherit setuptools3_legacy
inherit update-rc.d
inherit systemd

# setup.py calls "pkg-config systemd --variable=systemdsystemunitdir" and needs to find our dev manager
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'udev', '', d)}"

inherit python3native

PACKAGES += "${PN}-systemd"

FILES:${PN} += "${sysconfdir}/* \
                ${datadir}/* \
                ${nonarch_libdir}/${BPN}/*"

FILES:${PN}-systemd += "${systemd_unitdir}/*"
RDEPENDS:${PN}-systemd += " ${PN}"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${BPN} = "cloud-init"

DEPENDS += "python3-pyyaml-native \
            python3-requests-native \
            python3-jinja2-native \
           "

RDEPENDS:${PN} = "python3 \
                  python3-jinja2 \
                  python3-configobj \
                  python3-requests \
                  python3-jsonpatch \
                  python3-jsonschema \
                  python3-pyyaml \
                  python3-oauthlib \
                  python3-netifaces \
                  python3-charset-normalizer \
                  bash \
                 "

