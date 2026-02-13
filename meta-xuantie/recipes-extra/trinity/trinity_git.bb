SUMMARY = "a Linux System call fuzz teste."
DESCRIPTION = "Trinity, a Linux System call fuzz tester."
HOMEPAGE = "http://codemonkey.org.uk/projects/trinity/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=96094d47cfbd2cc45eb46ce0fc423c04"

COMPATIBLE_HOST = "(x86_64|arm|aarch64|riscv64).*-linux"

PV = "2023.01"
SRCREV = "ba2360ed84a8c521d9c34af9c909315ea7c62aad"
SRC_URI = " \
        git://github.com/kernelslacker/trinity.git;protocol=https;branch=master \
        file://0001-riscv64-sv39-to-sv57.patch \
        file://trinity_run \
        "

S = "${WORKDIR}/git"

inherit useradd
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --create-home --shell /bin/sh ${PN} "

inherit autotools pkgconfig
B = "${S}"

do_install() {
    oe_runmake install DESTDIR=${D}${prefix}
    install -o ${PN} -d -m 0755 ${D}/${datadir}/${PN}
    install -o ${PN} -m 0755 ${S}/scripts/* ${D}/${datadir}/${PN}
    install -o ${PN} -m 0755 ${WORKDIR}/trinity_run ${D}/${datadir}/${PN}
}

PACKAGES =+ "${PN}-example"

FILES:${PN} = "${bindir}/trinity"
FILES:${PN}-example = "${datadir}/${PN}"

RDEPENDS:${PN}-example += "${PN} bash"
