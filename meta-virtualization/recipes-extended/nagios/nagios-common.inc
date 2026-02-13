NAGIOS_CONF_DIR = "${sysconfdir}/nagios"
NAGIOS_PLUGIN_DIR = "${libdir}/nagios/plugins"
NAGIOS_PLUGIN_CONF_DIR = "${NAGIOS_CONF_DIR}/plugins"
NAGIOS_STATE_DIR = "${localstatedir}/nagios"

NAGIOS_USER = "nagios"
NAGIOS_GROUP = "nagios"

EXTRA_OECONF += "--sysconfdir=${NAGIOS_CONF_DIR} \
                 --libexecdir=${NAGIOS_PLUGIN_DIR} \
                 --localstatedir=${NAGIOS_STATE_DIR} \
"

FILES:${PN}-dbg += "${NAGIOS_PLUGIN_DIR}/.debug"

DEPENDS = "nagios-core"

COMPATIBLE_HOST:aarch64 = "null"

inherit useradd

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r ${NAGIOS_GROUP}; -r nagcmd"
USERADD_PARAM:${PN} = "-r -M -g ${NAGIOS_GROUP} ${NAGIOS_USER}"

FILESYSTEM_PERMS_TABLES:append = " files/fs-perms-nagios.txt"
