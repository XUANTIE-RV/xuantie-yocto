SUMMARY = "Clang C Language Family Frontend for LLVM"
HOMEPAGE = "https://clang.llvm.org/"
DESCRIPTION = "Clang is a compiler for the C language family, providing fast compilation and highly useful diagnostics. It aims to deliver a modern and expressive programming environment."
LICENSE = "CLOSED"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.0/clang_18.1.8_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=377bd53ce72ae7c4fdbf851a5ef7364fdcdb1940984edb4a4fe90cacc42ffc55"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.0/clang_18.1.8_riscv64-6.6.36-rv64gc-lp64d.deb;sha256sum=5a40c5a9f1ac04deb3ae35be9bbcd8b0237528b4bb56cc425d64adf3b4932319"
FILE_RV32_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.0/clang_18.1.8_riscv32-5.10.4-rv32gc-ilp32d.deb;sha256sum=537cd1d6e9aef1d68153e44406d46b7751f00b4a2b7218c12a9cf8366e22d745"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.0/clang_18.1.8_riscv32-6.6.36-rv32gc-ilp32d.deb;sha256sum=b1c8a440206ae583d3532de852b46d36bd6e9b57d92e84a7f75abe21cac2420f"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510};subdir=${BP}', \
    '${FILE_RV64_66};subdir=${BP}', d)}"

SRC_URI:riscv32 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV32_510};subdir=${BP}', \
    '${FILE_RV32_66};subdir=${BP}', d)}"

PR = "r23"

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
