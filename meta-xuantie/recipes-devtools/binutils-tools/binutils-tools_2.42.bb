LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""
COMPATIBLE_HOST = "(riscv64|riscv32).*-linux"

XUANTIE_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "${XUANTIE_LINUX_TAG}"

S = "${WORKDIR}"


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
