SUMMARY = "GNU cc and gcc C compilers"
HOMEPAGE = "http://gcc.org/"
DESCRIPTION = "GNU cc and gcc C compilers."
LICENSE = "CLOSED"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.2.0/gcc_14.3.0_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=0d338c4a4d83b2ec04582b19d295f5645f92e67cb62a84f6b96582bd86320de6"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.2.0/gcc_14.3.0_riscv64-6.6.36-rv64gc-lp64d.deb;sha256sum=d149bd076740fb05b80cf8ad4aa3f3051b18d2ab3f1bde14c30afd7c685a2106"
FILE_RV32_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.2.0/gcc_14.3.0_riscv32-5.10.4-rv32gc-ilp32d.deb;sha256sum=1f7e066ce6ceb63f0150f2bae01fb443a323ebc719593a6bd815df1b1a2259a7"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.2.0/gcc_14.3.0_riscv32-6.6.36-rv32gc-ilp32d.deb;sha256sum=e5ba10b6f7466389cdeb3f9e78cdce8500edb99da1ab0d4831d4209b234795f6"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510}', \
    '${FILE_RV64_66}', d)}"

SRC_URI:riscv32 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV32_510}', \
    '${FILE_RV32_66}', d)}"

PR = "r8"

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
