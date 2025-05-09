SUMMARY = "Clang C Language Family Frontend for LLVM"
HOMEPAGE = "https://clang.llvm.org/"
DESCRIPTION = "Clang is a compiler for the C language family, providing fast compilation and highly useful diagnostics. It aims to deliver a modern and expressive programming environment."
LICENSE = "CLOSED"

XUANTIE_CLANG_SRC_URI_PATH ?= "ftp://11.122.129.163/Test/Test/Toolschain/llvm-riscv/V2.1.0-rc3"
SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${XUANTIE_CLANG_SRC_URI_PATH}/clang_18.1.8_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=21203db5a0cb19ffbd994e140ff99767adff535d6b89ea97dba50608e23bb524;subdir=${BP}', \
    '${XUANTIE_CLANG_SRC_URI_PATH}/clang_18.1.8_riscv64-6.6.0-rv64gc-lp64d.deb;sha256sum=2525b3b78d12fcab2db956c8cd712e81f84148f27a8c1d95a782bb640af4ccc9;subdir=${BP}', d)}"
SRC_URI:riscv32 = "${XUANTIE_CLANG_SRC_URI_PATH}/clang_18.1.8_riscv32-6.6.0-rv32gc-ilp32d.deb;sha256sum=a2f0891e7c284ed4e3f6d241dcaf819f0a8842e471384e7dadc32ae91611549d;subdir=${BP}"

PR = "r4"
INSANE_SKIP:${PN} += " file-rdeps dev-so staticdev already-stripped installed-vs-shipped dev-deps"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
FILES:${PN} += "\
      * \
"
do_install () {
    cp -r ${B}/* ${D}
}
PACKAGES = "${PN} ${PN}-dbg"
VALGRINDARCH ?= "${TARGET_ARCH}"

RDEPENDS:${PN} += "python3 c++headers gcclibs libc6-dev binutils libstdc++-dev"
INSANE_SKIP:${PN} += "debug-files"
