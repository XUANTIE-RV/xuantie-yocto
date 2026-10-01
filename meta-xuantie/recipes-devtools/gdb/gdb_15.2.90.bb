LICENSE = "CLOSED"
PV = "15.2.90-V3.5.0"

inherit python3-dir autotools texinfo
SECTION = "devel"
DEPENDS = "expat mpfr zlib ncurses virtual/libiconv bison-native"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.4.0/Xuantie-900-gcc-linux-5.10.4-glibc-native_gdb-rv64gc-lp64d-V3.5.0.tar.gz;sha256sum=bda3f571579778dd00876ea09b0803842a7201af0f631f91673d23e592c09de7"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.4.0/Xuantie-900-gcc-linux-6.6.36-glibc-native_gdb-rv64gc-lp64d-V3.5.0.tar.gz;sha256sum=85e06daa8b950ecfab6249319e45cf81052cb25d7562c9217f58ac890f5832ec"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.4.0/Xuantie-900-gcc-linux-6.6.36-glibc-native_gdb-rv32gc-ilp32d-V3.5.0.tar.gz;sha256sum=5742038921d816cfdfb4aafe1f7405632be9d0617a968772447fe45aaad9dd44"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510}', \
    '${FILE_RV64_66}', d)}"

SRC_URI:riscv32 = "${FILE_RV32_66}"

PR = "r0"

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
