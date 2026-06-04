LICENSE = "CLOSED"

inherit python3-dir autotools texinfo
SECTION = "devel"
DEPENDS = "expat mpfr zlib ncurses virtual/libiconv bison-native"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.2/Xuantie-900-gcc-linux-5.10.4-glibc-native_gdb-rv64gc-lp64d-V3.4.1.tar.gz;sha256sum=6a0c1b5ea6e11d1671905b48c7c19d573367ab3a7744939b8783d96bfc444df9"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.2/Xuantie-900-gcc-linux-6.6.36-glibc-native_gdb-rv64gc-lp64d-V3.4.1.tar.gz;sha256sum=95fc04e459a2a9e280d42924b80f066c5f397d234d38b42d0985988a45f6cca2"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.2/Xuantie-900-gcc-linux-6.6.36-glibc-native_gdb-rv32gc-ilp32d-V3.4.1.tar.gz;sha256sum=3cdeb0b5b3cc50096936eb43d3903cf52f244f202478745d729a3cf77d820b34"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510}', \
    '${FILE_RV64_66}', d)}"

SRC_URI:riscv32 = "${FILE_RV32_66}"

PR = "r7"

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
