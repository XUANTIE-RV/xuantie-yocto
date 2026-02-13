DESCRIPTION = "A toolkit to interact with the virtualization capabilities of recent versions of Linux." 
HOMEPAGE = "http://libvirt.org"
LICENSE = "LGPL-2.1-or-later & GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LESSER;md5=4b54a1fd55a448865a0b32d41598759d"
SECTION = "console/tools"

DEPENDS = "bridge-utils gnutls libxml2 lvm2 avahi parted curl libpcap util-linux e2fsprogs pm-utils \
	   iptables dnsmasq readline libtasn1 libxslt-native acl libdevmapper libtirpc \
           python3-docutils-native \
	   ${@bb.utils.contains('PACKAGECONFIG', 'polkit', 'shadow-native', '', d)} \
	   ${@bb.utils.contains('PACKAGECONFIG', 'gnutls', 'gnutls-native', '', d)}"

# libvirt-guests.sh needs gettext.sh
#
RDEPENDS:${PN} = "gettext-runtime"

RDEPENDS:libvirt-libvirtd += "bridge-utils iptables pm-utils dnsmasq netcat-openbsd ebtables"
RDEPENDS:libvirt-libvirtd:append:x86-64 = " dmidecode"
RDEPENDS:libvirt-libvirtd:append:x86 = " dmidecode"

#connman blocks the 53 port and libvirtd can't start its DNS service
RCONFLICTS:${PN}_libvirtd = "connman"

SRC_URI = "http://libvirt.org/sources/libvirt-${PV}.tar.xz;name=libvirt \
           file://libvirtd.sh \
           file://libvirtd.conf \
           file://dnsmasq.conf \
           file://hook_support.py \
           file://gnutls-helper.py \
           file://0001-qemu-segmentation-fault-in-virtqemud-executing-qemuD.patch \
          "

SRC_URI[libvirt.sha256sum] = "3c6c43becffeb34a3f397c616206aa69a893ff8bf5e8208393c84e8e75352934"

inherit meson gettext update-rc.d pkgconfig systemd useradd perlnative
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r qemu; -r kvm"
USERADD_PARAM:${PN} = "-r -g qemu -G kvm qemu"


EXTRA_OEMESON += "--cross-file ${WORKDIR}/meson-${PN}.cross"
do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
iptables = '/usr/sbin/iptables'
ip6tables = '/usr/sbin/ip6tables'
dmidecode = '/usr/sbin/dmidecode'
ebtables = '/sbin/ebtables'
dnsmasq = '/usr/bin/dnsmasq'
EOF
}

ALLOW_EMPTY:${PN} = "1"
INSANE_SKIP:${PN} += "empty-dirs"

PACKAGES =+ "${PN}-libvirtd ${PN}-virsh"

ALLOW_EMPTY:${PN}-libvirtd = "1"

FILES:${PN}-libvirtd = " \
	${sysconfdir}/init.d \
	${sysconfdir}/sysctl.d \
	${sysconfdir}/logrotate.d \
	${sysconfdir}/libvirt/libvirtd.conf \
        /usr/lib/sysctl.d/60-libvirtd.conf \
	/usr/lib/sysctl.d/60-qemu-postcopy-migration.conf \
	${sbindir}/libvirtd \
	${systemd_system_unitdir} \
	${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '', '${libexecdir}/libvirt-guests.sh', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'gnutls', '${sysconfdir}/pki/libvirt/* ${sysconfdir}/pki/CA/*', '', d)} \
        "

FILES:${PN}-virsh = " \
    ${bindir}/virsh \
    ${datadir}/bash-completion/completions/virsh \
"

FILES:${PN} += "${libdir}/libvirt/connection-driver \
	    ${datadir}/augeas \
	    ${@bb.utils.contains('PACKAGECONFIG', 'polkit', '${datadir}/polkit-1', '', d)} \
	    ${datadir}/bash-completion/completions/vsh \
	    ${datadir}/bash-completion/completions/virt-admin \
	    /usr/lib/firewalld/zones/libvirt.xml \
	    "

FILES:${PN}-dbg += "${libdir}/libvirt/connection-driver/.debug ${libdir}/libvirt/lock-driver/.debug"
FILES:${PN}-staticdev += "${libdir}/*.a ${libdir}/libvirt/connection-driver/*.a ${libdir}/libvirt/lock-driver/*.a"

CONFFILES:${PN} += "${sysconfdir}/libvirt/libvirt.conf \
                    ${sysconfdir}/libvirt/lxc.conf \
                    ${sysconfdir}/libvirt/qemu-lockd.conf \
                    ${sysconfdir}/libvirt/qemu.conf \
                    ${sysconfdir}/libvirt/virt-login-shell.conf \
                    ${sysconfdir}/libvirt/virtlockd.conf"

CONFFILES:${PN}-libvirtd = "${sysconfdir}/logrotate.d/libvirt ${sysconfdir}/logrotate.d/libvirt.lxc \
                            ${sysconfdir}/logrotate.d/libvirt.qemu ${sysconfdir}/logrotate.d/libvirt.uml \
                            ${sysconfdir}/libvirt/libvirtd.conf \
                            /usr/lib/sysctl.d/libvirtd.conf"

INITSCRIPT_PACKAGES = "${PN}-libvirtd"
INITSCRIPT_NAME:${PN}-libvirtd = "libvirtd"
INITSCRIPT_PARAMS:${PN}-libvirtd = "defaults 72"

SYSTEMD_PACKAGES = "${PN}-libvirtd"
SYSTEMD_SERVICE:${PN}-libvirtd = " \
    libvirtd.service \
    virtlockd.service \
    libvirt-guests.service \
    virtlockd.socket \
    "

# xen-minimal config
#PACKAGECONFIG ??= "xen libxl xen-inotify test remote libvirtd"

# full config
PACKAGECONFIG ??= "gnutls qemu yajl openvz vmware vbox esx lxc test remote \
                   libvirtd netcf udev python fuse firewalld libpcap \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux audit libcap-ng', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'libxl', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'polkit', '', d)} \
                  "

# qemu is NOT compatible with mips64
PACKAGECONFIG:remove:mipsarchn32 = "qemu"
PACKAGECONFIG:remove:mipsarchn64 = "qemu"

# numactl is NOT compatible with arm
PACKAGECONFIG:remove:arm = "numactl"
PACKAGECONFIG:remove:armeb = "numactl"

# enable,disable,depends,rdepends
#
PACKAGECONFIG[gnutls] = ",,,gnutls-bin"
PACKAGECONFIG[qemu] = "-Ddriver_qemu=enabled -Dqemu_user=qemu -Dqemu_group=qemu,-Ddriver_qemu=disabled,qemu,"
PACKAGECONFIG[yajl] = "-Dyajl=enabled,-Dyajl=disabled,yajl,yajl"
PACKAGECONFIG[libxl] = "-Ddriver_libxl=enabled,-Ddriver_libxl=disabled,xen,"
PACKAGECONFIG[openvz] = "-Ddriver_openvz=enabled,-Ddriver_openvz=disabled,,"
PACKAGECONFIG[vmware] = "-Ddriver_vmware=enabled,-Ddriver_vmware=disabled,,"
PACKAGECONFIG[vbox] = "-Ddriver_vbox=enabled,-Ddriver_vbox=disabled,,"
PACKAGECONFIG[esx] = "-Ddriver_esx=enabled,-Ddriver_esx=disabled,,"
PACKAGECONFIG[hyperv] = "-Ddriver_hyperv=enabled,-Ddriver_hyperv=disabled,,"
PACKAGECONFIG[polkit] = "-Dpolkit=enabled,-Dpolkit=disabled,polkit,polkit"
PACKAGECONFIG[lxc] = "-Ddriver_lxc=enabled,-Ddriver_lxc=disabled,lxc,"
PACKAGECONFIG[test] = "-Ddriver_test=enabled,-Ddriver_test=disabled,,"
PACKAGECONFIG[remote] = "-Ddriver_remote=enabled,-Ddriver_remote=disabled,,"
PACKAGECONFIG[libvirtd] = "-Ddriver_libvirtd=enabled,-Ddriver_libvirtd=disabled,,"
PACKAGECONFIG[netcf] = "-Dnetcf=enabled,-Dnetcf=disabled,netcf,netcf"
PACKAGECONFIG[dtrace] = "-Ddtrace=enabled,-Ddtrace=disabled,,"
PACKAGECONFIG[udev] = "-Dudev=enabled -Dpciaccess=enabled,-Dudev=disabled,udev libpciaccess,"
PACKAGECONFIG[selinux] = "-Dselinux=enabled,-Dselinux=disabled,libselinux,"
PACKAGECONFIG[python] = ",,python3,"
PACKAGECONFIG[sasl] = "-Dsasl=enabled,-Dsasl=disabled,cyrus-sasl,cyrus-sasl"
PACKAGECONFIG[numactl] = "-Dnumactl=enabled,-Dnumactl=disabled,numactl,"
PACKAGECONFIG[fuse] = "-Dfuse=enabled,-Dfuse=disabled,fuse,"
PACKAGECONFIG[audit] = "-Daudit=enabled,-Daudit=disabled,audit,"
PACKAGECONFIG[libcap-ng] = "-Dcapng=enabled,-Dcapng=disabled,libcap-ng,"
PACKAGECONFIG[wireshark] = "-Dwireshark_dissector=enabled,-Dwireshark_dissector=disabled,wireshark libwsutil,"
PACKAGECONFIG[apparmor_profiles] = "-Dapparmor_profiles=enabled, -Dapparmor_profiles=disabled,"
PACKAGECONFIG[firewalld] = "-Dfirewalld=enabled, -Dfirewalld=disabled,"
PACKAGECONFIG[libpcap] = "-Dlibpcap=enabled, -Dlibpcap=disabled,libpcap,libpcap"
PACKAGECONFIG[numad] = "-Dnumad=enabled, -Dnumad=disabled,"

# Enable the Python tool support
require libvirt-python.inc

do_compile() {
	cd ${B}/src
	# There may be race condition, but without creating these directories
	# in the source tree, generation of files fails.
	for i in access admin logging esx locking rpc hyperv lxc \
		    remote network storage interface nwfilter node_device \
		    secret vbox qemu; do
		mkdir -p $i;
	done

	cd ${B}
	export PKG_CONFIG_PATH="$PKG_CONFIG_PATH:${B}/src:"
	ninja all
}

do_install:prepend() {
	# so the install routines can find the libvirt.pc in the source dir
	export PKG_CONFIG_PATH="$PKG_CONFIG_PATH:${B}/src:"
}

do_install:append() {
	install -d ${D}/etc/init.d
	install -d ${D}/etc/libvirt
	install -d ${D}/etc/dnsmasq.d

	install -m 0755 ${WORKDIR}/libvirtd.sh ${D}/etc/init.d/libvirtd
	install -m 0644 ${WORKDIR}/libvirtd.conf ${D}/etc/libvirt/libvirtd.conf

	if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)}; then
	    # This will wind up in the libvirtd package, but will NOT be invoked by default.
	    #
	    mv ${D}/${libexecdir}/libvirt-guests.sh ${D}/${sysconfdir}/init.d
	fi

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
            if [ "${systemd_system_unitdir}" != "${prefix}/lib/systemd/system" ] ; then
                # ./src/meson.build:    systemd_unit_dir = prefix / 'lib' / 'systemd' / 'system'
                # ./tools/meson.build:    install_dir: prefix / 'lib' / 'systemd' / 'system',
                mkdir -p ${D}${systemd_system_unitdir}
                mv ${D}${prefix}/lib/systemd/system/* ${D}${systemd_system_unitdir}
                rmdir ${D}${prefix}/lib/systemd/system ${D}${prefix}/lib/systemd
            fi

	    # We can't use 'notify' when we don't support 'sd_notify' dbus capabilities.
	    # Change default LIBVIRTD_ARGS to start libvirtd in the right mode.
	    sed -i -e 's/Type=notify/Type=forking/' \
	           -e '/Type=forking/a PIDFile=/run/libvirtd.pid' \
	           -e 's/\(Environment=LIBVIRTD_ARGS="--timeout 120"\)/#\1\nEnvironment=LIBVIRTD_ARGS="--listen --daemon"/' \
		   ${D}/${systemd_system_unitdir}/libvirtd.service
	fi

	# The /run/libvirt directories created by the Makefile are 
	# wiped out in volatile, we need to create these at boot.
	rm -rf ${D}/run
	install -d ${D}${sysconfdir}/default/volatiles
	echo "d root root 0755 /run/libvirt none" \
	     > ${D}${sysconfdir}/default/volatiles/99_libvirt
	echo "d root root 0755 /run/libvirt/lockd none" \
	     >> ${D}${sysconfdir}/default/volatiles/99_libvirt
	echo "d root root 0755 /run/libvirt/lxc none" \
	     >> ${D}${sysconfdir}/default/volatiles/99_libvirt
	echo "d root root 0755 /run/libvirt/network none" \
	     >> ${D}${sysconfdir}/default/volatiles/99_libvirt
	echo "d root root 0755 /run/libvirt/qemu none" \
	     >> ${D}${sysconfdir}/default/volatiles/99_libvirt

	# Manually set permissions and ownership to match polkit recipe
	if ${@bb.utils.contains('PACKAGECONFIG', 'polkit', 'true', 'false', d)}; then
		install -d -m 0700 ${D}/${datadir}/polkit-1/rules.d
		chown polkitd ${D}/${datadir}/polkit-1/rules.d
		chgrp root ${D}/${datadir}/polkit-1/rules.d
	else
		rm -rf ${D}/${datadir}/polkit-1
	fi

	# disable seccomp_sandbox
        if [ -e ${D}${sysconfdir}/libvirt/qemu.conf ] ; then
	   sed -i '/^#seccomp_sandbox = 1/aseccomp_sandbox = 0' \
	       ${D}${sysconfdir}/libvirt/qemu.conf
        fi

	# Add hook support for libvirt
	mkdir -p ${D}/etc/libvirt/hooks
	for hook in "daemon" "lxc" "network" "qemu"
	do
		install -m 0755 ${WORKDIR}/hook_support.py ${D}/etc/libvirt/hooks/${hook}
	done

	# Force the main dnsmasq instance to bind only to specified interfaces and
	# to not bind to virbr0. Libvirt will run its own instance on this interface.
	install -m 644 ${WORKDIR}/dnsmasq.conf ${D}/${sysconfdir}/dnsmasq.d/libvirt-daemon

	# remove .la references to our working diretory
	for i in `find ${D}${libdir} -type f -name *.la`; do
	    sed -i -e 's#-L${B}/src/.libs##g' $i
	done

	sed -i -e 's/^\(unix_sock_group\ =\ \).*/\1"kvm"/' ${D}/etc/libvirt/libvirtd.conf
	sed -i -e 's/^\(unix_sock_rw_perms\ =\ \).*/\1"0776"/' ${D}/etc/libvirt/libvirtd.conf

	case ${MACHINE_ARCH} in
		*mips*)
			break
			;;
		*)
			if ${@bb.utils.contains('PACKAGECONFIG', 'qemu', 'true', 'false', d)}; then
				chown -R qemu:qemu ${D}/${localstatedir}/lib/libvirt/qemu
				echo "d qemu qemu 0755 ${localstatedir}/cache/libvirt/qemu none" \
				    >> ${D}${sysconfdir}/default/volatiles/99_libvirt
				break
			fi
			;;
	esac

	if ${@bb.utils.contains('PACKAGECONFIG','gnutls','true','false',d)}; then
	    # Generate sample keys and certificates.
	    cd ${WORKDIR}
	    ${WORKDIR}/gnutls-helper.py -y

	    # Deploy all sample keys and certificates of CA, server and client
	    # to target so that libvirtd is able to boot successfully and local
	    # connection via 127.0.0.1 is available out of box.
	    install -d ${D}/etc/pki/CA
	    install -d ${D}/etc/pki/libvirt/private
            install -m 0755 ${WORKDIR}/gnutls-helper.py ${D}/${bindir}
	    install -m 0644 ${WORKDIR}/cakey.pem ${D}/${sysconfdir}/pki/libvirt/private/cakey.pem
	    install -m 0644 ${WORKDIR}/cacert.pem ${D}/${sysconfdir}/pki/CA/cacert.pem
	    install -m 0644 ${WORKDIR}/serverkey.pem ${D}/${sysconfdir}/pki/libvirt/private/serverkey.pem
	    install -m 0644 ${WORKDIR}/servercert.pem ${D}/${sysconfdir}/pki/libvirt/servercert.pem
	    install -m 0644 ${WORKDIR}/clientkey.pem ${D}/${sysconfdir}/pki/libvirt/private/clientkey.pem
	    install -m 0644 ${WORKDIR}/clientcert.pem ${D}/${sysconfdir}/pki/libvirt/clientcert.pem

	    # Force the connection to be tls.
	    sed -i -e 's/^\(listen_tls\ =\ .*\)/#\1/' -e 's/^\(listen_tcp\ =\ .*\)/#\1/' ${D}/etc/libvirt/libvirtd.conf
	fi

	# virt-login-shell needs to run with setuid permission
	chmod 4755 ${D}${bindir}/virt-login-shell
}

EXTRA_OEMESON += " \
    -Dinit_script=${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','none', d)} \
    -Drunstatedir=/run \
    -Dtests=enabled \
    "

# gcc9 end up mis-compiling qemuxml2argvtest.o with Og which then
# crashes on target, so remove -Og and use -O2 as workaround
SELECTED_OPTIMIZATION:remove:virtclass-multilib-lib32:mipsarch = "-Og"
SELECTED_OPTIMIZATION:append:virtclass-multilib-lib32:mipsarch = " -O2"

pkg_postinst:${PN}() {
        if [ -z "$D" ] && [ -e /etc/init.d/populate-volatile.sh ] ; then
                /etc/init.d/populate-volatile.sh update
        fi
        mkdir -m 711 -p $D/data/images
}

python () {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        d.setVar("INHIBIT_UPDATERCD_BBCLASS", "1")
}
