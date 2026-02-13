SUMMARY = "Embedded Microprocessor Benchmark Consortium Coremark-pro"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=0a18b17ae63deaa8a595035f668aebe1"

SRCREV="d5fad6bd094899101a4e5fd53af7298160ced6ab"
SRC_URI = " \
    git://github.com/eembc/coremark.git;protocol=https;branch=main \
    file://0001-coremark-ignore-RUN.patch \
    file://0001-coremark-add-cflags.patch \
"
PV = "1.01"
PR="r2"
S = "${WORKDIR}/git"
inherit xuantie-set-package-arch-with-mcpu-benchmark
EXTRA_OEMAKE = " PORT_DIR=linux"
PACKAGE_ARCH = "${XUANTIE_MCPU}_external"
do_compile() {
	oe_runmake
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/coremark.exe ${D}${bindir}
}

FILES:${PN} += " ${bindir} "
FILES:${PN} += " ${includedir} "
FILES:${PN} += " ${libdir} "

PROVIDES = "${PN}"
PACKAGES = "${PN}"

INSANE_SKIP:${PN} += "debug-files file-rdeps dev-deps installed-vs-shipped"
