SUMMARY = "xuantie hw-c9xx for FPGA"
LICENSE = "CLOSED"

inherit deploy

DEPENDS += "zsb"

SRC_URI = "file://fpga"

do_deploy() {
    cp -rfP ${WORKDIR}/fpga ${DEPLOYDIR}
    # tests/ 是 generator 的 golden 参考集,只服务开发自测,不随 SDK 交付
    rm -rf ${DEPLOYDIR}/fpga/tests
}

addtask deploy before do_build after do_install
