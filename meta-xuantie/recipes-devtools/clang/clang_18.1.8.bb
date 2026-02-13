SUMMARY = "Clang C Language Family Frontend for LLVM"
HOMEPAGE = "https://clang.llvm.org/"
DESCRIPTION = "Clang is a compiler for the C language family, providing fast compilation and highly useful diagnostics. It aims to deliver a modern and expressive programming environment."
LICENSE = "CLOSED"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.2.0/clang_18.1.8_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=a4a9c90f0546a41c5924611264fb84a433c941d95df793b38b9cb848f4a0bc8a"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.2.0/clang_18.1.8_riscv64-6.6.36-rv64gc-lp64d.deb;sha256sum=f44862ae6f74c92249f8b7d00ebf319d389873af951b97d52d0d32472183b484"
FILE_RV32_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.2.0/clang_18.1.8_riscv32-5.10.4-rv32gc-ilp32d.deb;sha256sum=91c36414f4d11af1e5d1e0791a213568100183666db8bd636dd6f30d02808cfc"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.2.0/clang_18.1.8_riscv32-6.6.36-rv32gc-ilp32d.deb;sha256sum=7f86bf9bd246d33805f37600a14aef3637450b1c0bc2f3cbdd7ee323673f1064"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510};subdir=${BP}', \
    '${FILE_RV64_66};subdir=${BP}', d)}"

SRC_URI:riscv32 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV32_510};subdir=${BP}', \
    '${FILE_RV32_66};subdir=${BP}', d)}"

PR = "r14"

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
