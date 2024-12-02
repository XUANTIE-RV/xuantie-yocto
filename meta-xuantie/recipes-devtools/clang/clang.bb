SUMMARY = "Clang C Language Family Frontend for LLVM"
HOMEPAGE = "https://clang.llvm.org/"
DESCRIPTION = "Clang is a compiler for the C language family, providing fast compilation and highly useful diagnostics. It aims to deliver a modern and expressive programming environment."
LICENSE = "CLOSED"
PV = "18.1.8"

SRC_URI = "ftp://swftp01.damo.xrvm.cn/Test/Test/Toolschain/llvm-riscv/V2.0.0-rc2/clang_18.1.8_riscv64-6.6.0-rv64gc-lp64d.deb;subdir=${BP}"
SRC_URI[sha256sum] = "2d8600ae97d69bd7a56b7259073b4761b184865371c8fb38da33254801fe8b45"

INSANE_SKIP:${PN} += " file-rdeps dev-so staticdev already-stripped installed-vs-shipped "
do_configure[noexec] = "1"
do_compile[noexec] = "1"
FILES:${PN} += "\
      * \
"
do_install () {
    cp -r ${B}/* ${D}
}
PACKAGES = "${PN}"
VALGRINDARCH ?= "${TARGET_ARCH}"

RDEPENDS:${PN} += "python3"
