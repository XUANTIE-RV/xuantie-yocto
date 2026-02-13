LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""
COMPATIBLE_HOST = "(riscv64|riscv32).*-linux"

XUANTIE_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "${XUANTIE_LINUX_TAG}"

S = "${WORKDIR}"


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

PR = "r9"

INSANE_SKIP:${PN} += "file-rdeps dev-deps infodir"
INSANE_SKIP:${PN} += "dev-so staticdev"
INSANE_SKIP:${PN} += "already-stripped"
INSANE_SKIP:${PN} += "installed-vs-shipped"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
FILES:${PN} += "\
    usr/bin/* \
"
do_install() {
    mkdir -p ${D}/usr/bin
    cp -rf ${S}/usr/bin/* ${D}/usr/bin
    rm -rf ${D}/usr/bin/c++
    rm -rf ${D}/usr/bin/c++filt
    rm -rf ${D}/usr/bin/cpp
    rm -rf ${D}/usr/bin/g++
    rm -rf ${D}/usr/bin/gcc
    rm -rf ${D}/usr/bin/gcc-ar
    rm -rf ${D}/usr/bin/gcc-nm
    rm -rf ${D}/usr/bin/gcc-ranlib
    rm -rf ${D}/usr/bin/gcov
    rm -rf ${D}/usr/bin/gcov-dump
    rm -rf ${D}/usr/bin/gcov-tool
    rm -rf ${D}/usr/bin/gfortran
    rm -rf ${D}/usr/bin/lto-dump
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-c++
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-g++
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-gcc
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-gcc-*
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-gcc-ar
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-gcc-nm
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-ranlib
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-gfortran
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-gcc-ranlib
}

RDEPENDS:${PN} += "elfutils libcurl ca-certificates libopcodes zstd openssl openssl-ossl-module-legacy libbfd"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "strings"
ALTERNATIVE_LINK_NAME[vconfig] = "${bindir}/strings"
