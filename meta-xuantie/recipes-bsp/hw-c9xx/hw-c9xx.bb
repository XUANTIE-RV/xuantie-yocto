SUMMARY = "xuantie hw-c9xx for FPGA"
LICENSE = "CLOSED"

inherit deploy

DEPENDS += "zsb"

SRC_URI = "file://fpga"

do_deploy() {
    cp -rfP ${WORKDIR}/fpga ${DEPLOYDIR}
}

addtask deploy before do_build after do_install
