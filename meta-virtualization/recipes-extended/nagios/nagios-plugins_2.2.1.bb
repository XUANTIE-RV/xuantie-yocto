require nagios-common.inc

DESCRIPTION = "A host/service/network monitoring and management system plugins"
HOMEPAGE = "http://www.nagios-plugins.org"
SECTION = "console/network"
PRIORITY = "optional"
LICENSE = "GPL-3.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://www.nagios-plugins.org/download/${BPN}-${PV}.tar.gz \
"

SRC_URI[md5sum] = "fb521d5c05897f165b0b1862c1e5cb27"
SRC_URI[sha256sum] = "647c0ba4583d891c965fc29b77c4ccfeccc21f409fdf259cb8af52cb39c21e18"

S = "${WORKDIR}/${BPN}-${PV}"

inherit autotools gettext

SKIP_RECIPE[nagios-plugins] ?= "${@bb.utils.contains('BBFILE_COLLECTIONS', 'webserver', '', 'Depends on nagios-core which depends on apache2 from meta-webserver which is not included', d)}"

EXTRA_OECONF += "--with-sysroot=${STAGING_DIR_HOST} \
                 --with-nagios-user=${NAGIOS_USER} \
                 --with-nagios-group=${NAGIOS_GROUP} \
                 --without-apt-get-command \
                 --with-trusted-path=/bin:/sbin:/usr/bin:/usr/sbin \
                 ac_cv_path_PERL=${bindir}/perl \
"

# IPv6
PACKAGECONFIG[ipv6] = "--with-ipv6,--without-ipv6,,"

# Enable check_ldaps, check_http --ssl, check_tcp --ssl
PACKAGECONFIG[ssl] = "--with-openssl=${STAGING_DIR_HOST},--without-openssl,openssl,libssl"

# Enable check_ldaps
PACKAGECONFIG[ldap] = "--with-ldap,--without-ldap,openldap"

# Enable check_smtp --starttls
PACKAGECONFIG[gnutls] = "--with-gnutls=${STAGING_DIR_HOST},--without-gnutls,gnutls,gnutls"

# Enable check_pgsql
PACKAGECONFIG[pgsql] = "--with-pgsql=${STAGING_DIR_HOST},--without-pgsql,postgresql,libpq"

# Enable check_mysql, check_mysql_query
PACKAGECONFIG[mysql] = "--with-mysql=${STAGING_DIR_HOST},--without-mysql,mysql5,libmysqlclient"

# Enable check_snmp
PACKAGECONFIG[snmp] = "\
    --with-snmpget-command=${bindir}/snmpget --with-snmpgetnext-command=${bindir}/snmpgetnext, \
    --without-snmpget-command --without-snmpgetnext-command, \
    , net-snmp-utils \
"

PACKAGECONFIG ??= "ssl gnutls"

do_configure() {
    oe_runconf || die "make failed"
}

do_install:append() {
     sed -i '1s,#! /usr/bin/perl -w.*,#! ${bindir}/env perl,' ${D}${libdir}/nagios/plugins/*
}

RDEPENDS:${PN} += "\
    iputils \
    nagios-base \
    perl \
    bash \
"

FILES:${PN} += "${datadir} \
                ${NAGIOS_PLUGIN_DIR} \
"
