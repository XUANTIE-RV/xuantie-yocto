require nagios-common.inc

DESCRIPTION = "Nagios Remote Plugin Executor"
HOMEPAGE = "http://www.nagios.com"
SECTION = "console/network"
PRIORITY = "optional"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://src/nrpe.c;beginline=1;endline=35;md5=0dadd78599abbc737af81432702e9161"

SRCNAME = "nrpe"

SRC_URI = "https://github.com/NagiosEnterprises/nrpe/releases/download/${SRCNAME}-${PV}/${SRCNAME}-${PV}.tar.gz \
           file://check_nrpe.cfg \
           file://nagios-nrpe.service \
           file://0001-Should-fix-235-nasty_metachars-was-not-being-returne.patch \
"

SRC_URI[md5sum] = "37b9e23b3e8d75308f8b31f3b61ee8a4"
SRC_URI[sha256sum] = "c5d9d7023eaa49e6fe8cf95c6d101731f07972cf0f8818fa130c171bc9eabd55"
SRC_URI[sha1sum] = "2150e274fc7f24905c63b8a996ed7218e2967478"
SRC_URI[sha384sum] = "0ece79fb312c8d1ee0e6bde1be499f8090a5a86cf90b0b8dcbebb95c5f8f70b2cf9ac0a4064f726bee091c4147b61d82"
SRC_URI[sha512sum] = "4d7cf6abc974bc79df54afc42644418e3f086a279c8c17d0fd104f19e3c21c0f3dae4fb4268dd134446ff9fe505159b0446372c5cac71cfe03a97479ed41c09b"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit autotools-brokensep update-rc.d systemd update-alternatives

SKIP_RECIPE[nagios-nrpe] ?= "${@bb.utils.contains('BBFILE_COLLECTIONS', 'webserver', '', 'Depends on nagios-core which depends on apache2 from meta-webserver which is not included', d)}"

# IP address of server which proxy should connect to
MONITORING_PROXY_SERVER_IP ??= "192.168.7.2"

# IP address of server which agent should connect to
MONITORING_AGENT_SERVER_IP ??= "192.168.7.4"

EXTRA_OECONF += "--with-nrpe-user=${NAGIOS_USER} \
                 --with-nrpe-group=${NAGIOS_GROUP} \
                 ac_cv_lib_wrap_main=no \
                 ac_cv_path_PERL=${bindir}/perl \
"

EXTRA_OECONF_SSL = "--with-ssl=${STAGING_DIR_HOST} \
                    --with-ssl-inc=${STAGING_DIR_HOST}${includedir} \
                    --with-ssl-lib=${STAGING_DIR_HOST}${libdir} \
"

PACKAGECONFIG[ssl] = "${EXTRA_OECONF_SSL},--disable-ssl,openssl-native openssl,"
PACKAGECONFIG[cmdargs] = "--enable-command-args,--disable-command-args,,"
PACKAGECONFIG[bashcomp] = "--enable-bash-command-substitution,--disable-bash-command-substitution,,"

PACKAGECONFIG ??= "cmdargs bashcomp"

do_configure() {
    oe_runconf || die "make failed"
}

do_compile() {
    oe_runmake all
}

do_install:append() {
    oe_runmake 'DESTDIR=${D}' install-daemon
    oe_runmake 'DESTDIR=${D}' install-config

    install -d ${D}${sysconfdir}/init.d
    install -m 755 ${B}/startup/debian-init ${D}${sysconfdir}/init.d/nrpe

    install -d ${D}${NAGIOS_CONF_DIR}/nrpe.d
    echo "include_dir=${NAGIOS_CONF_DIR}/nrpe.d" >> ${D}${NAGIOS_CONF_DIR}/nrpe.cfg

    sed -e "s/^allowed_hosts=.*/allowed_hosts=${MONITORING_AGENT_SERVER_IP}/g" \
        -i ${D}${NAGIOS_CONF_DIR}/nrpe.cfg

    install -d ${D}${NAGIOS_PLUGIN_CONF_DIR}
    install -m 664 ${WORKDIR}/check_nrpe.cfg ${D}${NAGIOS_PLUGIN_CONF_DIR}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/nagios-nrpe.service ${D}${systemd_unitdir}/system/
    fi
}

PACKAGES = "${PN}-dbg ${PN}-plugin ${PN}-daemon"

FILES:${PN}-plugin = "${NAGIOS_PLUGIN_DIR} \
                      ${NAGIOS_PLUGIN_CONF_DIR} \
"

FILES:${PN}-daemon = "${sysconfdir} \
                      ${bindir} \
                      ${nonarch_libdir}/tmpfiles.d/ \
                      ${localstatedir} \
"

RDEPENDS:${PN}-daemon = "nagios-base"
RDEPENDS:${PN}-plugin = "nagios-base"

SYSTEMD_PACKAGES = "${PN}-daemon"
SYSTEMD_SERVICE:${PN}-daemon = "nagios-nrpe.service"
SYSTEMD_AUTO_ENABLE:${PN}-daemon = "enable"

INITSCRIPT_PACKAGES = "${PN}-daemon"
INITSCRIPT_NAME:${PN}-daemon = "nrpe"
INITSCRIPT_PARAMS:${PN}-daemon = "defaults"

ALTERNATIVE:${PN}-daemon = "nagios"
ALTERNATIVE_LINK_NAME[nagios] = "${localstatedir}/nagios"
