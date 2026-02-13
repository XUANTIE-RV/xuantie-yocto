SUMMARY = "OpenvSwitch"
DESCRIPTION = "\
	Open vSwitch is a production quality, multilayer virtual switch \
	licensed under the open source Apache 2.0 license. It is designed \
	to enable massive network automation through programmatic extension, \
	while still supporting standard management interfaces and protocols \
	(e.g. NetFlow, sFlow, SPAN, RSPAN, CLI, LACP, 802.1ag) \
	"

HOMEPAGE = "http://openvswitch.org/"
SECTION = "networking"
LICENSE = "Apache-2.0"

DEPENDS += "bridge-utils openssl python3 perl python3-six-native coreutils-native"

RDEPENDS:${PN} += "util-linux-uuidgen util-linux-libuuid coreutils \
        python3 perl perl-module-strict ${PN}-switch \
        bash python3-twisted python3-six"
RDEPENDS:${PN}-testcontroller = "${PN} ${PN}-pki"
RDEPENDS:${PN}-switch = "${PN} openssl procps util-linux-uuidgen"
RDEPENDS:${PN}-pki = "${PN}"
RDEPENDS:${PN}-brcompat = "${PN} ${PN}-switch"

# Some installers will fail because of an install order based on
# rdeps.  E.g. ovs-pki calls sed in the postinstall.  sed may be
# queued for install later.
RDEPENDS:${PN} += "sed gawk grep"

SRC_URI = "\
	file://openvswitch-switch \
	file://openvswitch-switch-setup \
	file://openvswitch-testcontroller \
	file://openvswitch-testcontroller-setup \
	"

EXTRA_OECONF += "\
	PYTHON=python3 \
	PYTHON3=python3 \
	PERL=${bindir}/perl \
	"
CONFIGUREOPT_DEPTRACK = ""

# Don't compile kernel modules by default since it heavily depends on
# kernel version. Use the in-kernel module for now.
# distro layers can enable with EXTRA_OECONF_pn_openvswitch += ""
# EXTRA_OECONF = "--with-linux=${STAGING_KERNEL_DIR} KARCH=${TARGET_ARCH}"

ALLOW_EMPTY:${PN}-pki = "1"
PACKAGES =+ "${PN}-testcontroller ${PN}-switch ${PN}-brcompat ${PN}-pki"

FILES:${PN}-testcontroller = "\
	${sysconfdir}/init.d/openvswitch-testcontroller \
	${sysconfdir}/default/openvswitch-testcontroller \
	${sysconfdir}/openvswitch-testcontroller \
	${bindir}/ovs-testcontroller \
	"

FILES:${PN}-brcompat = "${sbindir}/ovs-brcompatd"

FILES:${PN}-switch = "\
	${sysconfdir}/init.d/openvswitch-switch \
	${sysconfdir}/default/openvswitch-switch \
	${systemd_unitdir}/system/ovs-vswitchd.service \
	${systemd_unitdir}/system/openvswitch.service \
	${systemd_unitdir}/system/ovsdb-server.service \
	${sysconfdir}/sysconfig/openvswitch \
	${sysconfdir}/openvswitch/default.conf \
	"

# silence a warning
FILES:${PN} += "${datadir}/ovsdbmonitor"
FILES:${PN} += "/run"

FILES:${PN} += "${libdir}/python${PYTHON_BASEVERSION}/"
inherit pkgconfig autotools update-rc.d systemd python3native

SYSTEMD_PACKAGES = "${PN}-switch"
SYSTEMD_SERVICE:${PN}-switch = " \
    ovsdb-server.service \
    ovs-vswitchd.service \
    openvswitch.service \
"

INITSCRIPT_PACKAGES = "${PN}-switch ${PN}-testcontroller"
INITSCRIPT_NAME:${PN}-switch = "openvswitch-switch"
INITSCRIPT_PARAMS:${PN}-switch = "defaults 71"

INITSCRIPT_NAME:${PN}-testcontroller = "openvswitch-testcontroller"
INITSCRIPT_PARAMS:${PN}-testcontroller = "defaults 72"

do_configure:prepend() {
	# Work around the for Makefile CC=$(if ....) by swapping out any
	# "-Wa," assembly directives with "-Xassembler
	CC=`echo '${CC}' | sed 's/-Wa,/-Xassembler /g'`
}

do_install:prepend() {
	SERVICE_FILE="${S}/rhel/usr_lib_systemd_system_ovs-vswitchd.service"
	${S}/build-aux/dpdkstrip.py \
	    ${@bb.utils.contains('PACKAGECONFIG','dpdk','--dpdk','--nodpdk',d)} \
	    < ${S}/rhel/usr_lib_systemd_system_ovs-vswitchd.service.in \
	    > ${SERVICE_FILE}
}

do_install:append() {
	install -d ${D}/${sysconfdir}/default/
	install -m 660 ${WORKDIR}/openvswitch-switch-setup ${D}/${sysconfdir}/default/openvswitch-switch
	install -d ${D}/${sysconfdir}/openvswitch-testcontroller
	install -m 660 ${WORKDIR}/openvswitch-testcontroller-setup ${D}/${sysconfdir}/default/openvswitch-testcontroller

	install -d ${D}/${sysconfdir}/init.d/
	install -m 755 ${WORKDIR}/openvswitch-testcontroller ${D}/${sysconfdir}/init.d/openvswitch-testcontroller
	install -m 755 ${WORKDIR}/openvswitch-switch ${D}/${sysconfdir}/init.d/openvswitch-switch
	true || rm -fr ${D}/${datadir}/${PN}/pki

	install -d ${D}/${sysconfdir}/sysconfig
	install -m 644 ${S}/rhel/usr_share_openvswitch_scripts_systemd_sysconfig.template \
		${D}/${sysconfdir}/sysconfig/openvswitch

	install -d ${D}/${sysconfdir}/openvswitch
	install -m 644 ${S}/rhel/etc_openvswitch_default.conf \
		${D}/${sysconfdir}/openvswitch/default.conf

	install -d ${D}/${systemd_unitdir}/system/
	install -m 644 ${S}/rhel/usr_lib_systemd_system_ovs-vswitchd.service \
		${D}/${systemd_unitdir}/system/ovs-vswitchd.service
	install -m 644 ${S}/rhel/usr_lib_systemd_system_openvswitch.service \
		${D}/${systemd_unitdir}/system/openvswitch.service
	install -m 644 ${S}/rhel/usr_lib_systemd_system_ovsdb-server.service \
		${D}/${systemd_unitdir}/system/ovsdb-server.service

	install -d ${D}/usr/share/openvswitch/scripts/
	install -m 755 ${S}/rhel/usr_share_openvswitch_scripts_ovs-systemd-reload ${D}/usr/share/openvswitch/scripts/ovs-systemd-reload

	oe_runmake modules_install INSTALL_MOD_PATH=${D}${root_prefix}
	install -d ${D}${libdir}/python${PYTHON_BASEVERSION}/site-packages
	cp -r ${S}/python/ovstest/ ${D}${libdir}/python${PYTHON_BASEVERSION}/site-packages/
}

pkg_postinst_ontarget:${PN}-pki () {
	if test ! -d $D/${datadir}/${PN}/pki; then
		ovs-pki init --dir=$D/${datadir}/${PN}/pki
	fi
}

pkg_postinst_ontarget:${PN}-testcontroller () {
	if test ! -d $D/${datadir}/${PN}/pki; then
		ovs-pki init --dir=$D/${datadir}/${PN}/pki
	fi

	cd $D/${sysconfdir}/openvswitch-testcontroller
	if ! test -e cacert.pem; then
		ln -s $D/${datadir}/${PN}/pki/switchca/cacert.pem cacert.pem
	fi
	if ! test -e privkey.pem || ! test -e cert.pem; then
		oldumask=$(umask)
		umask 077
		ovs-pki req+sign --dir=$D/${datadir}/${PN}/pki tmp controller >/dev/null
		mv tmp-privkey.pem privkey.pem
		mv tmp-cert.pem cert.pem
		mv tmp-req.pem req.pem
		chmod go+r cert.pem req.pem
		umask $oldumask
	fi
}
