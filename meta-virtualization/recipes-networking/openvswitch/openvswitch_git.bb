require openvswitch.inc

DEPENDS += "virtual/kernel"

PACKAGE_ARCH = "${MACHINE_ARCH}"

RDEPENDS:${PN}-ptest += "\
	python3-logging python3-syslog python3-io python3-core \
	python3-fcntl python3-shell python3-xml python3-math \
	python3-datetime python3-netclient python3 sed \
	ldd perl-module-socket perl-module-carp perl-module-exporter \
	perl-module-xsloader python3-netserver python3-threading \
	python3-resource findutils which diffutils \
	"

S = "${WORKDIR}/git"
PV = "2.17.1+${SRCPV}"
CVE_VERSION = "2.17.1"

FILESEXTRAPATHS:append := "${THISDIR}/${PN}-git:"

SRCREV = "41bb202fb37f184b0a8820a029c62d03c118614e"
SRC_URI += "git://github.com/openvswitch/ovs.git;protocol=https;branch=branch-2.17 \
            file://openvswitch-add-ptest-71d553b995d0bd527d3ab1e9fbaf5a2ae34de2f3.patch \
            file://run-ptest \
            file://disable_m4_check.patch \
            file://kernel_module.patch \
            file://systemd-update-tool-paths.patch \
            file://systemd-create-runtime-dirs.patch \
            file://0001-lldp-Fix-bugs-when-parsing-malformed-AutoAttach.patch \
           "

LIC_FILES_CHKSUM = "file://LICENSE;md5=1ce5d23a6429dff345518758f13aaeab"

PACKAGECONFIG ?= "libcap-ng"
PACKAGECONFIG[dpdk] = "--with-dpdk=shared,,dpdk,dpdk"
PACKAGECONFIG[libcap-ng] = "--enable-libcapng,--disable-libcapng,libcap-ng,"
PACKAGECONFIG[ssl] = ",--disable-ssl,openssl,"

# Don't compile kernel modules by default since it heavily depends on
# kernel version. Use the in-kernel module for now.
# distro layers can enable with EXTRA_OECONF_pn_openvswitch += ""
# EXTRA_OECONF += "--with-linux=${STAGING_KERNEL_BUILDDIR} --with-linux-source=${STAGING_KERNEL_DIR} KARCH=${TARGET_ARCH}"

# silence a warning
FILES:${PN} += "/lib/modules"

inherit ptest

EXTRA_OEMAKE += "TEST_DEST=${D}${PTEST_PATH} TEST_ROOT=${PTEST_PATH}"

do_install_ptest() {
	oe_runmake test-install
}

do_install:append() {
	oe_runmake modules_install INSTALL_MOD_PATH=${D}${root_prefix}
}
