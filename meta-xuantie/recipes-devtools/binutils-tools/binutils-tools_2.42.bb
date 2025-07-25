LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""
COMPATIBLE_HOST = "(riscv64|riscv32).*-linux"

XUANTIE_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "${XUANTIE_LINUX_TAG}"

S = "${WORKDIR}"

XUANTIE_GCC_SRC_URI_PATH ?= "ftp://11.122.129.163/Test/Test/Toolschain/gnu-riscv/V3.1.0"
SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${XUANTIE_GCC_SRC_URI_PATH}/gcc_14.1.1_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=9882dfecd9817d1bb1f903480ede6d29033d6f4ba60a6bb865fad6ab1bce86e8', \
    '${XUANTIE_GCC_SRC_URI_PATH}/gcc_14.1.1_riscv64-6.6.0-rv64gc-lp64d.deb;sha256sum=51a24c5a7981e25897112b08b27777fd31693b747655d3cb2c80485c6c732477', d)}"
SRC_URI:riscv32 = "${XUANTIE_GCC_SRC_URI_PATH}/gcc_14.1.1_riscv32-6.6.0-rv32gc-ilp32d.deb;sha256sum=808823570c32d4078d18164d5427a2201482b8342c5059bfc4b5e9772a5d7ddc"

PR = "r3"
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
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-gcc-14.1.1
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-gcc-ar
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-gcc-nm
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-ranlib
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-gfortran
    rm -rf ${D}/usr/bin/riscv*-unknown-linux-gnu-gcc-ranlib
}

RDEPENDS:${PN} += "elfutils libcurl ca-certificates libopcodes zstd openssl openssl-ossl-module-legacy libbfd"
