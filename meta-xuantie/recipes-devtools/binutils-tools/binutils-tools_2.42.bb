LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""
COMPATIBLE_HOST = "(riscv64|riscv32).*-linux"

XUANTIE_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "${XUANTIE_LINUX_TAG}"

S = "${WORKDIR}"


FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.1/gcc_14.3.0_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=e8af6b561b9b25e6e66e62816399567d223876bdad78cccab1ed9d0f695adf5c"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.1/gcc_14.3.0_riscv64-6.6.36-rv64gc-lp64d.deb;sha256sum=484fb47a78044e69330d3729963029cb7fcce0287b83f0407cdef60d544091fa"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.1/gcc_14.3.0_riscv32-6.6.36-rv32gc-ilp32d.deb;sha256sum=1fa2b574d4d994560c3ff0c45aac79d00ad2ff9a07b8fc5ec646a6efd7a1e98d"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510}', \
    '${FILE_RV64_66}', d)}"

SRC_URI:riscv32 = "${FILE_RV32_66}"

PR = "r14"

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
