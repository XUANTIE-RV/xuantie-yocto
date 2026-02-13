LICENSE = "CLOSED"

inherit python3-dir autotools texinfo
SECTION = "devel"
DEPENDS = "expat mpfr zlib ncurses virtual/libiconv bison-native"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.2.0/Xuantie-900-gcc-linux-5.10.4-glibc-native_gdb-rv64gc-lp64d-V3.3.0.tar.gz;sha256sum=f10580dbeabd6e3db7192367d4b21a3c5a6a7d2a0099ba452b164c6331fed4fe"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.2.0/Xuantie-900-gcc-linux-6.6.36-glibc-native_gdb-rv64gc-lp64d-V3.3.0.tar.gz;sha256sum=dd87114c03da7465c0ebb4913f00fcc8d3d37c0187e23b3f0cf5e0153ea51ad8"
FILE_RV32_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.2.0/Xuantie-900-gcc-linux-5.10.4-glibc-native_gdb-rv32gc-ilp32d-V3.3.0.tar.gz;sha256sum=b2a144b73ae64254f1f2eabfefb0cd7678bdb3f65db886bcda7f6749b58f81d7"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.2.0/Xuantie-900-gcc-linux-6.6.36-glibc-native_gdb-rv32gc-ilp32d-V3.3.0.tar.gz;sha256sum=787fdcd02ad580f7ba0216297795d07c4c1426ec436a58c33b740ce971ebe24a"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510}', \
    '${FILE_RV64_66}', d)}"

SRC_URI:riscv32 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV32_510}', \
    '${FILE_RV32_66}', d)}"

PR = "r1"

INSANE_SKIP:${PN} = "already-stripped"
do_compile() {
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${datadir}

    cp -rfd ${WORKDIR}/Xuantie*/bin/*   ${D}${bindir}
    cp -rfd ${WORKDIR}/Xuantie*/share/* ${D}${datadir}
}

RRECOMMENDS:gdb:append:linux = " glibc-thread-db "
RRECOMMENDS:gdb:append:linux-gnueabi = " glibc-thread-db "
RRECOMMENDS:gdbserver:append:linux = " glibc-thread-db "
RRECOMMENDS:gdbserver:append:linux-gnueabi = " glibc-thread-db "

RDEPENDS:${PN}:libc-glibc = " \
    glibc-gconv-utf-32 \
"
