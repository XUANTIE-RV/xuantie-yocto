LICENSE = "CLOSED"

inherit python3-dir autotools texinfo
SECTION = "devel"
DEPENDS = "expat mpfr zlib ncurses virtual/libiconv bison-native"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.0/Xuantie-900-gcc-linux-5.10.4-glibc-native_gdb-rv64gc-lp64d-V3.4.0.tar.gz;sha256sum=c1348923512a2b3afbdcbc265117aa0445dab98f39c960c05bf85c738ae8a059"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.0/Xuantie-900-gcc-linux-6.6.36-glibc-native_gdb-rv64gc-lp64d-V3.4.0.tar.gz;sha256sum=1c6f9adfc46e314499e127bdab3e97073866dea88ee819811460a16a3d3807b0"
FILE_RV32_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.0/Xuantie-900-gcc-linux-5.10.4-glibc-native_gdb-rv32gc-ilp32d-V3.4.0.tar.gz;sha256sum=3359422aef355fbfa2e128e742cecca2379cc60413977ce3474a224aca97ab81"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.0/Xuantie-900-gcc-linux-6.6.36-glibc-native_gdb-rv32gc-ilp32d-V3.4.0.tar.gz;sha256sum=868231e7ae8e5bb58b1ee08cfde33993e5fb8a2d96aa24096eabde16fac7e55b"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510}', \
    '${FILE_RV64_66}', d)}"

SRC_URI:riscv32 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV32_510}', \
    '${FILE_RV32_66}', d)}"

PR = "r4"

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
