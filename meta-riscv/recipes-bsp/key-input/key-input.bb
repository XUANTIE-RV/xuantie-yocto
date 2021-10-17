SUMMARY = "TouchScreen Simulation"
DESCRIPTION = "D1 board touchscreen simulation"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "\
    file://ts-send.c\
    file://input \
"

# inherit bin_package

S = "${WORKDIR}"

do_compile() {
    ${CC} ts-send.c -o ts-send
}

do_install () {
    install -d ${D}${bindir}
    install -m0755 ${S}/input   ${D}${bindir}
    install -m0755 ${S}/ts-send ${D}${bindir}
}

FILES_${PN} += " ${bindir} "
FILES_${PN}-dbg += " ${bindir}/.debug "
