LICENSE = "CLOSED"
DEPENDS += "zlib perl-native"

XUANTIE_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "${XUANTIE_LINUX_TAG}"
INSANE_SKIP:${PN} = "debug-files file-rdeps already-stripped textrel staticdev dev-so"

SRC_URI = "git://git@gitee.com/xuantie-yocto/binutils-gdb-prebuilt.git;branch=1.0;protocol=ssh"
S:riscv32 = "${WORKDIR}/git/binutils-gdb-prebuilt_riscv32/usr"
S:riscv64 = "${WORKDIR}/git/binutils-gdb-prebuilt_riscv64/usr"
DEPENDS = "flex-native bison-native zlib-native gnu-config-native autoconf-native"
FILES:${PN} +="\
   /usr/lib64 \
   /usr/riscv64-oe-linux \
   /usr/lib32 \
   /usr/riscv32-oe-linux \
"

do_compile() {
}
do_install() {
    cp -rf ${S} ${D}
    mv ${D}/usr/lib/libbfd-2.42.0.20240618.so ${D}/usr/lib/libbfd-2.42.0.20240618.so.0.0
    ln -sr ${D}/usr/lib/libbfd-2.42.0.20240618.so.0.0 ${D}/usr/lib/libbfd-2.42.0.20240618.so
    mv ${D}/usr/lib/libopcodes-2.42.0.20240618.so ${D}/usr/lib/libopcodes-2.42.0.20240618.so.0.0
    ln -sr ${D}/usr/lib/libopcodes-2.42.0.20240618.so.0.0 ${D}/usr/lib/libopcodes-2.42.0.20240618.so
    rm -rf ${D}/usr/build.riscv*
}

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'debuginfod', d)}"
PACKAGECONFIG[debuginfod] = "--with-debuginfod, --without-debuginfod, elfutils"



