require binutils-src_2.42.bb

LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""
COMPATIBLE_HOST = "(riscv64|riscv32).*-linux"

XUANTIE_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "ca98d362a6ec40da2867bd00509c538424bb6c6e"

XUANTIE_GIT = "git://git@gitee.com/xuantie-yocto/binutils-gdb-prebuilt.git"
XUANTIE_GIT_MIRRORS = "git://git@github.com/XUANTIE-RV/binutils-gdb-prebuild.git"
MIRRORS += "${XUANTIE_GIT} ${XUANTIE_GIT_MIRRORS}"

SRC_URI = " \
    ${XUANTIE_GIT_MIRRORS};branch=1.0;protocol=https \
    ${XUANTIE_GIT};branch=1.0;protocol=https \
"

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
