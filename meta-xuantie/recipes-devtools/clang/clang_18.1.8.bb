SUMMARY = "Clang C Language Family Frontend for LLVM"
HOMEPAGE = "https://clang.llvm.org/"
DESCRIPTION = "Clang is a compiler for the C language family, providing fast compilation and highly useful diagnostics. It aims to deliver a modern and expressive programming environment."
LICENSE = "CLOSED"

XUANTIE_CLANG_SRC_URI_PATH ?= "ftp://11.122.129.163/Test/Test/Toolschain/llvm-riscv/V2.1.0"
SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${XUANTIE_CLANG_SRC_URI_PATH}/clang_18.1.8_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=a8d3518c54f60ea6119310b0a7479c80a80929db035d7b45fa6ee296f25527c4;subdir=${BP}', \
    '${XUANTIE_CLANG_SRC_URI_PATH}/clang_18.1.8_riscv64-6.6.0-rv64gc-lp64d.deb;sha256sum=89cf4a541deef037c78cb29124d0acd059cd66fb674ee4873e251366902a33b9;subdir=${BP}', d)}"
SRC_URI:riscv32 = "${XUANTIE_CLANG_SRC_URI_PATH}/clang_18.1.8_riscv32-6.6.0-rv32gc-ilp32d.deb;sha256sum=4bbe452d0202dc96ff139bb55d59a9c19a1fd52c08afdab232cf936683d3f82b;subdir=${BP}"

PR = "r5"
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
