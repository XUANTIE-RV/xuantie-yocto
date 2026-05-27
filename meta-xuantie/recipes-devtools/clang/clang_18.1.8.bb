SUMMARY = "Clang C Language Family Frontend for LLVM"
HOMEPAGE = "https://clang.llvm.org/"
DESCRIPTION = "Clang is a compiler for the C language family, providing fast compilation and highly useful diagnostics. It aims to deliver a modern and expressive programming environment."
LICENSE = "CLOSED"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.1/clang_18.1.8_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=48c6bfe75aeeedbc5516e5c54b8ed84ecc44d1ae9a3af7df2f94a093af0c3aab"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.1/clang_18.1.8_riscv64-6.6.36-rv64gc-lp64d.deb;sha256sum=0116603324fe99628334071bab885b713fe1306b56389d02b2552aaaf578c16e"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.1/clang_18.1.8_riscv32-6.6.36-rv32gc-ilp32d.deb;sha256sum=db14236d1edbde75e817a8e3eeef2a26b675bf0487afd643df78b6be4cba9ba7"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510};subdir=${BP}', \
    '${FILE_RV64_66};subdir=${BP}', d)}"

SRC_URI:riscv32 = "${FILE_RV32_66};subdir=${BP}"

PR = "r28"

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
