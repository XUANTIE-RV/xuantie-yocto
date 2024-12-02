DESCRIPTION = "Unix benchmark suite"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRCREV="a07fcc03264915c624f0e4818993c5b4df3fa703"
SRC_URI = " \
    git://github.com/kdlucas/byte-unixbench.git;protocol=https;branch=master \
    file://0001-adaption-for-rv32-and-rv64-mode.patch \
"
PV = "5.1.3"
inherit xuantie-set-package-arch-with-mcpu
PACKAGE_ARCH = "${MACHINE_ARCH}"
# Extend cpu configuration for different xuantie cpu
python () {
    if   d.getVar('CPU_MODEL'):
        d.setVar('CFLAGS', d.getVar('CFLAGS') + " -mcpu={}".format(d.getVar('CPU_MODEL')))
        d.setVar('CXXFLAGS', d.getVar('CXXFLAGS') + " -mcpu={}".format(d.getVar('CPU_MODEL')))
}

S = "${WORKDIR}/git"

EXTRA_OEMAKE = '\
    TUNE_ARCH=${TUNE_ARCH} \
'

do_compile() {
	cd UnixBench && oe_runmake
}

do_install() {
	install -d ${D}${prefix}/src
	cp -r ${S}/UnixBench ${D}${prefix}/src/
}

DEPENDS = "virtual/libgl"
RDEPENDS:${PN} = "fontconfig freetype libx11 libxext libxt libxcb libice expat libxdmcp libxau libsm libxrender libxmuu libxrender libxft libxmu x11perf perl perl-module-strict perl-module-posix perl-module-time-hires perl-module-exporter-heavy perl-module-io-handle perl-module-io perl-module-findbin make perl-module-constant perl-module-file-path perl-module-file-spec"

FILES:${PN} += " ${bindir} "
FILES:${PN} += " ${includedir} "
FILES:${PN} += " ${libdir} "
FILES:${PN} += " ${prefix}/src "

PROVIDES = "${PN}"
PACKAGES = "${PN}"

INSANE_SKIP:${PN} += "debug-files file-rdeps dev-deps installed-vs-shipped"
