require binutils-src_2.42.bb

LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""
COMPATIBLE_HOST = "(riscv64|riscv32).*-linux"

XUANTIE_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "ca98d362a6ec40da2867bd00509c538424bb6c6e"

SRC_URI = "git://git@gitee.com/xuantie-yocto/binutils-gdb-prebuilt.git;branch=1.0;protocol=ssh"
S:riscv32 = "${WORKDIR}/git/binutils-gdb-prebuilt_riscv32/usr"
S:riscv64 = "${WORKDIR}/git/binutils-gdb-prebuilt_riscv64/usr"

do_configure() {
}
do_compile() {
}
do_install() {
    cp -rf ${S} ${D}
    rm -rf ${D}/usr/build.riscv*
}
