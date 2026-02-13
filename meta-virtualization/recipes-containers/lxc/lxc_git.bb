DESCRIPTION = "lxc aims to use these new functionnalities to provide an userspace container object"
SECTION = "console/utils"
LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c \
                    file://LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe \
"

DEPENDS = "libxml2 libcap"
RDEPENDS:${PN} = " \
		rsync \
		curl \
		gzip \
		xz \
		tar \
		libcap-bin \
		bridge-utils \
		dnsmasq \
		perl-module-strict \
		perl-module-getopt-long \
		perl-module-vars \
		perl-module-exporter \
		perl-module-constant \
		perl-module-overload \
		perl-module-exporter-heavy \
		gmp \
		libidn \
		gnutls \
		nettle \
		util-linux-mountpoint \
		util-linux-getopt \
"

RDEPENDS:${PN}:append:libc-glibc = " glibc-utils"

RDEPENDS:${PN}-ptest += "file make gmp nettle gnutls bash libgcc"

RDEPENDS:${PN}-networking += "iptables"

SRC_URI = "git://github.com/lxc/lxc.git;branch=main;protocol=https \
	file://lxc-1.0.0-disable-udhcp-from-busybox-template.patch \
	file://run-ptest \
	file://templates-actually-create-DOWNLOAD_TEMP-directory.patch \
	file://template-make-busybox-template-compatible-with-core-.patch \
	file://templates-use-curl-instead-of-wget.patch \
	file://0001-download-don-t-try-compatbility-index.patch \
	file://tests-our-init-is-not-busybox.patch \
	file://0001-use-sd_bus_call_method_async-to-replace-the-asyncv-o.patch \
	file://dnsmasq.conf \
	file://lxc-net \
	file://0001-Patching-an-incoming-CVE-CVE-2022-47952.patch \
	"

SRCREV = "133aa416ca2a5996090ec0e697e253646364d274"
PV = "5.0.1+git${SRCPV}"

S = "${WORKDIR}/git"

# Let's not configure for the host distro.
#
PTEST_CONF = "${@bb.utils.contains('DISTRO_FEATURES', 'ptest', '-Dtests=true', '', d)}"

# No meson equivalent for --with-distro
# EXTRA_OECONF += "--with-distro=${DISTRO} ${PTEST_CONF}"
EXTRA_OEMESON += "${PTEST_CONF}"
# No meson equivalent for these yet
# EXTRA_OECONF += "--enable-log-src-basename --disable-werror"

PACKAGECONFIG ??= "templates \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'seccomp', 'seccomp', '', d)} \
"

# Meson doesn't seem to be as fine grained as the autotools releases
# PACKAGECONFIG[doc] = "--enable-doc --enable-api-docs,--disable-doc --disable-api-docs,,"
PACKAGECONFIG[doc] = "-Dman=true,-Dman=false,,"
# No meson equiv found for rpath yet
# PACKAGECONFIG[rpath] = "--enable-rpath,--disable-rpath,,"
PACKAGECONFIG[apparmor] = "-Dapparmor=true,-Dapparmor=false,apparmor,apparmor"
PACKAGECONFIG[templates] = ",,, ${PN}-templates"
PACKAGECONFIG[selinux] = "-Dselinux=true,-Dselinux=false,libselinux,libselinux"
PACKAGECONFIG[seccomp] ="-Dseccomp=true,-Dseccomp=false,libseccomp,libseccomp"
# meson equiv for the unitdir found yet
# PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/,--without-systemdsystemunitdir,systemd,"
PACKAGECONFIG[systemd] = "-Dinit-script=systemd,-Dinit-script=sysvinit,systemd,"

# required by python3 to run setup.py
export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

inherit meson pkgconfig ptest update-rc.d systemd python3native

SYSTEMD_PACKAGES = "${PN} ${PN}-networking"
SYSTEMD_SERVICE:${PN} = "lxc.service lxc-monitord.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"
SYSTEMD_SERVICE:${PN}-networking = "lxc-net.service"
SYSTEMD_AUTO_ENABLE:${PN}-networking = "enable"

INITSCRIPT_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '', '${PN}', d)} ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '', '${PN}-networking',d)}"
INITSCRIPT_NAME:${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '', 'lxc-containers', d)}"
INITSCRIPT_PARAMS:${PN} = "defaults"
INITSCRIPT_NAME:${PN}-networking = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '', 'lxc-net', d)}"
INITSCRIPT_PARAMS:${PN}-networking = "defaults"

FILES:${PN}-doc = "${mandir} ${infodir}"
# For LXC the docdir only contains example configuration files and should be included in the lxc package
FILES:${PN} += "${docdir}"
FILES:${PN} += "${libdir}/python3*"
FILES:${PN} += "${datadir}/bash-completion"
FILES:${PN}-dbg += "${libexecdir}/lxc/.debug"
FILES:${PN}-dbg += "${libexecdir}/lxc/hooks/.debug"
PACKAGES =+ "${PN}-templates ${PN}-networking ${PN}-lua"
FILES:lua-${PN} = "${datadir}/lua ${libdir}/lua"
FILES:lua-${PN}-dbg += "${libdir}/lua/lxc/.debug"
FILES:${PN}-templates += "${datadir}/lxc/templates"
RDEPENDS:${PN}-templates += "bash"

FILES:${PN}-networking += " \
    ${sysconfdir}/init.d/lxc-net \
    ${sysconfdir}/default/lxc-net \
"

# Not needed for meson
# CACHED_CONFIGUREVARS += " \
#     ac_cv_path_PYTHON='${STAGING_BINDIR_NATIVE}/python3-native/python3' \
#     am_cv_python_pyexecdir='${PYTHON_SITEPACKAGES_DIR}' \
#     am_cv_python_pythondir='${PYTHON_SITEPACKAGES_DIR}' \
#"

do_install:append() {
	# The /var/cache/lxc directory created by the Makefile
	# is wiped out in volatile, we need to create this at boot.
	rm -rf ${D}${localstatedir}/cache
	install -d ${D}${sysconfdir}/default/volatiles
	echo "d root root 0755 ${localstatedir}/cache/lxc none" \
	     > ${D}${sysconfdir}/default/volatiles/99_lxc

	for i in `grep -l "#! */bin/bash" ${D}${datadir}/lxc/hooks/*`; do \
	    sed -e 's|#! */bin/bash|#!/bin/sh|' -i $i; done

	if "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}"; then
	    # nothing special for systemd at the moment
	    true
	else
	    # with meson, these aren't built unless sysvinit is the enabled
	    # init system.
	    install -d ${D}${sysconfdir}/init.d
	    install -m 755 config/init/sysvinit/lxc* ${D}${sysconfdir}/init.d
	fi

	# since python3-native is used for install location this will not be
	# suitable for the target and we will have to correct the package install
	if ${@bb.utils.contains('PACKAGECONFIG', 'python', 'true', 'false', d)}; then
	    if [ -d ${D}${exec_prefix}/lib/python* ]; then mv ${D}${exec_prefix}/lib/python* ${D}${libdir}/; fi
	    rmdir --ignore-fail-on-non-empty ${D}${exec_prefix}/lib
	fi

	# /etc/default/lxc sources lxc-net, this allows lxc bridge when lxc-networking
	# is not installed this results in no lxcbr0, but when lxc-networking is installed
	# lxcbr0 will be fully configured.
	install -m 644 ${WORKDIR}/lxc-net ${D}${sysconfdir}/default/

	# Force the main dnsmasq instance to bind only to specified interfaces and
	# to not bind to virbr0. Libvirt will run its own instance on this interface.
	install -d ${D}/${sysconfdir}/dnsmasq.d
	install -m 644 ${WORKDIR}/dnsmasq.conf ${D}/${sysconfdir}/dnsmasq.d/lxc
}

EXTRA_OEMAKE += "TEST_DIR=${D}${PTEST_PATH}/src/tests"

do_install_ptest() {
	# Move tests to the "ptest directory"
	install -d ${D}/${PTEST_PATH}/tests
	mv ${D}/usr/bin/lxc-test-* ${D}/${PTEST_PATH}/tests/.
}

pkg_postinst:${PN}() {
	if [ -z "$D" ] && [ -e /etc/init.d/populate-volatile.sh ] ; then
		/etc/init.d/populate-volatile.sh update
	fi
}

pkg_postinst:${PN}-networking() {
if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
cat >> $D/etc/network/interfaces << EOF

auto lxcbr0
iface lxcbr0 inet dhcp
	bridge_ports eth0
	bridge_fd 0
	bridge_maxwait 0
EOF

cat<<EOF>$D/etc/network/if-pre-up.d/lxcbr0
#! /bin/sh

if test "x\$IFACE" = xlxcbr0 ; then
        brctl show |grep lxcbr0 > /dev/null 2>/dev/null
        if [ \$? != 0 ] ; then
                brctl addbr lxcbr0
                brctl addif lxcbr0 eth0
                ip addr flush eth0
                ifconfig eth0 up
        fi
fi
EOF
chmod 755 $D/etc/network/if-pre-up.d/lxcbr0
fi
}
