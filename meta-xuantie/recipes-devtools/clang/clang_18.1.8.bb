SUMMARY = "Clang C Language Family Frontend for LLVM"
HOMEPAGE = "https://clang.llvm.org/"
DESCRIPTION = "Clang is a compiler for the C language family, providing fast compilation and highly useful diagnostics. It aims to deliver a modern and expressive programming environment."
LICENSE = "CLOSED"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.2/clang_18.1.8_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=acfb87c0fa563b9e54aa03bd9fb3c2cf25c023035847dd7b9b8a30be82721da8"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.2/clang_18.1.8_riscv64-6.6.36-rv64gc-lp64d.deb;sha256sum=41808cdccb585f67f2b4d5f8fbdf0c5c79022a6de7e784c8ac43d3593d6e9113"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.3.2/clang_18.1.8_riscv32-6.6.36-rv32gc-ilp32d.deb;sha256sum=00fdc88e53208865db3210740b8239dd457b329b6500a5999fe9f2b3c76afcbc"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510};subdir=${BP}', \
    '${FILE_RV64_66};subdir=${BP}', d)}"

SRC_URI:riscv32 = "${FILE_RV32_66};subdir=${BP}"

PR = "r29"

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
