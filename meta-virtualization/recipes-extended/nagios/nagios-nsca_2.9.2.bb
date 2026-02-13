require nagios-common.inc

DESCRIPTION = "Nagios Service Check Acceptor"
HOMEPAGE = "http://exchange.nagios.org"
SECTION = "console/network"
PRIORITY = "optional"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://src/nsca.c;beginline=1;endline=16;md5=dd7a195cc7d8a3ebcfabd65360d0cab4"

SRCNAME = "nsca"

SRC_URI = "http://prdownloads.sourceforge.net/sourceforge/nagios/${SRCNAME}-${PV}.tar.gz \
           file://init-script.in \
           file://nagios-nsca.service \
"

SRC_URI[md5sum] = "43f638c51367a3170476ed106fbb4858"
SRC_URI[sha256sum] = "96eb04ef695873ce462e3f8db08e8a49456ff9595d11acf70a3dd8a2c4af3b5e"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit update-rc.d autotools-brokensep systemd dos2unix

SKIP_RECIPE[nagios-nsca] ?= "${@bb.utils.contains('BBFILE_COLLECTIONS', 'webserver', '', 'Rdepends on nagios-base provided by nagios-core which depends on apache2 from meta-webserver which is not included', d)}"

DEPENDS = "libmcrypt"

EXTRA_OECONF += "--with-nsca-user=${NAGIOS_USER} \
                 --with-nsca-grp=${NAGIOS_GROUP} \
                 --with-libmcrypt-prefix=${STAGING_DIR_HOST} \
                 ac_cv_path_LIBMCRYPT_CONFIG=${STAGING_BINDIR_CROSS}/libmcrypt-config \
                 ac_cv_lib_wrap_main=no \
                 ac_cv_path_PERL=${bindir}/perl \
"

do_configure() {
    cp ${WORKDIR}/init-script.in ${S}/init-script.in
    oe_runconf || die "make failed"
}

do_install() {
    CONF_DIR=${D}${NAGIOS_CONF_DIR}

    install -d ${CONF_DIR}
    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${bindir}

    install -m 755 ${S}/sample-config/nsca.cfg ${CONF_DIR}
    install -m 755 ${S}/sample-config/send_nsca.cfg ${CONF_DIR}
    install -m 755 ${S}/init-script ${D}${sysconfdir}/init.d/nsca

    install -m 755 ${S}/src/nsca ${D}${bindir}
    install -m 755 ${S}/src/send_nsca ${D}${bindir}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/nagios-nsca.service ${D}${systemd_unitdir}/system/
    fi
}

PACKAGES = "${PN}-dbg ${PN}-daemon ${PN}-client"

FILES:${PN}-daemon = "${sysconfdir}/init.d \
                      ${NAGIOS_CONF_DIR}/nsca.cfg \
                      ${bindir}/nsca \
"

FILES:${PN}-client = "${NAGIOS_CONF_DIR}/send_nsca.cfg \
                      ${bindir}/send_nsca \
"

RDEPENDS:${PN}-daemon += "libmcrypt \
                          nagios-base \
"
RDEPENDS:${PN}-client += "libmcrypt \
                          nagios-base \
"

SYSTEMD_PACKAGES = "${PN}-daemon"
SYSTEMD_SERVICE:${PN}-daemon = "nagios-nsca.service"
SYSTEMD_AUTO_ENABLE:${PN}-daemon = "enable"

INITSCRIPT_PACKAGES = "${PN}-daemon"
INITSCRIPT_NAME:${PN}-daemon = "nsca"
INITSCRIPT_PARAMS:${PN}-daemon = "defaults"
