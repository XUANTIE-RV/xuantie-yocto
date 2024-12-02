SUMMARY = "xuantie hw-c9xx for FPGA"
LICENSE = "CLOSED"

inherit deploy

DEPENDS += "zsb"

SRC_URI = "file://fpga"

do_deploy() {
    cp -rfP ${WORKDIR}/fpga ${DEPLOYDIR}
    cp $EXTERNAL_TOOLCHAIN_EXPORT/bin/${EXTERNAL_TARGET_SYS}-gdb ${DEPLOYDIR}/fpga/riscv64-linux-gdb
}

addtask deploy before do_build after do_install
