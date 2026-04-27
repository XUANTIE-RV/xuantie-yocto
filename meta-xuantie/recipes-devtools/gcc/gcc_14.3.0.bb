SUMMARY = "GNU cc and gcc C compilers"
HOMEPAGE = "http://gcc.org/"
DESCRIPTION = "GNU cc and gcc C compilers."
LICENSE = "CLOSED"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.0/gcc_14.3.0_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=36ab871f00a0507fc49a636bdf281b5c1545da3f7372355e69c9c6a99ead299a"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.0/gcc_14.3.0_riscv64-6.6.36-rv64gc-lp64d.deb;sha256sum=960df75dd3ecda23eab41e62a758da6b97e95be09057ca3495841eb204d4bc0e"
FILE_RV32_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.0/gcc_14.3.0_riscv32-5.10.4-rv32gc-ilp32d.deb;sha256sum=b65c169c1af97c23314ded77a28f7054a6628f20a94893fcccd49412a30af882"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.0/gcc_14.3.0_riscv32-6.6.36-rv32gc-ilp32d.deb;sha256sum=de43ed42da3f6ded487302b89d1a7c22235cdfaf2b26c43f6ca7893e6eb4ca91"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510}', \
    '${FILE_RV64_66}', d)}"

SRC_URI:riscv32 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV32_510}', \
    '${FILE_RV32_66}', d)}"

PR = "r11"

INSANE_SKIP:${PN} += "file-rdeps dev-deps infodir libdir"
INSANE_SKIP:${PN} += "dev-so staticdev"
INSANE_SKIP:${PN} += "already-stripped"
INSANE_SKIP:${PN} += "installed-vs-shipped"
INSANE_SKIP:gcclibs += "staticdev"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

FILES:${PN} += "\
    usr/bin/* \
    usr/libexec/* \
    usr/lib/bfd-plugins/* \
    usr/riscv64-unknown-linux-gnu/bin/* \
    usr/riscv64-unknown-linux-gnu/lib/* \
    usr/share/* \ 
"
FILES:gcclibs += "\
    usr/lib/gcc/* \
"
FILES:c++headers += "\
    usr/include/* \
"

PACKAGES = "gcclibs ${PN} c++headers"
do_install () {
    cp -rfd ${WORKDIR}/usr ${D}
    rm -rf ${D}/usr/riscv64-unknown-linux-gnu/lib/libstdc++.so*
    rm -rf ${D}/usr/riscv64-unknown-linux-gnu/lib/libgcc_s.so.1
    rm -rf ${D}/usr/bin/addr2line
    rm -rf ${D}/usr/bin/ar
    rm -rf ${D}/usr/bin/as
    rm -rf ${D}/usr/bin/elfedit
    rm -rf ${D}/usr/bin/gprof
    rm -rf ${D}/usr/bin/ld
    rm -rf ${D}/usr/bin/ld.bfd
    rm -rf ${D}/usr/bin/nm
    rm -rf ${D}/usr/bin/objcopy
    rm -rf ${D}/usr/bin/objdump
    rm -rf ${D}/usr/bin/ranlib
    rm -rf ${D}/usr/bin/readelf
    rm -rf ${D}/usr/bin/size
    rm -rf ${D}/usr/bin/strings
    rm -rf ${D}/usr/bin/strip
}

RDEPENDS:${PN} += "libstdc++ libstdc++-dev c++headers gcclibs binutils libc6-dev"
