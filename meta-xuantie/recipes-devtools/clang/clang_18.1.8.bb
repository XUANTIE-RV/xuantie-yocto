SUMMARY = "Clang C Language Family Frontend for LLVM"
HOMEPAGE = "https://clang.llvm.org/"
DESCRIPTION = "Clang is a compiler for the C language family, providing fast compilation and highly useful diagnostics. It aims to deliver a modern and expressive programming environment."
LICENSE = "CLOSED"
PV = "18.1.8-V2.5.0"
DEFAULT_PREFERENCE = "100"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.4.0/clang_18.1.8_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=ac8f324325b74fd97af25253d01e86e8f2556e0c5bb90c8521d2453b4a1d0640"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.4.0/clang_18.1.8_riscv64-6.6.36-rv64gc-lp64d.deb;sha256sum=d5449619646a9a02059a1e473d4439d68b5e13d8dd983400aeada1cbac9ebcfe"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.4.0/clang_18.1.8_riscv32-6.6.36-rv32gc-ilp32d.deb;sha256sum=ea2bd6f4be3e2e8a6217ef67b86a588c6aea361b4110a0e9a5e2d4f932cd0656"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510};subdir=${BP}', \
    '${FILE_RV64_66};subdir=${BP}', d)}"

SRC_URI:riscv32 = "${FILE_RV32_66};subdir=${BP}"

PR = "r1"

INSANE_SKIP:${PN} += " file-rdeps dev-so staticdev already-stripped installed-vs-shipped dev-deps"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
FILES:${PN} += "\
      * \
"
do_install () {
    cp -r ${B}/* ${D}
}
PACKAGES = "${PN} ${PN}-dbg ${PN}-tools ${PN}-dev ${PN}-lldb-python lldb-server"
FILES:${PN}-tools = ""
FILES:${PN}-dev = ""
FILES:${PN}-lldb-python = ""
FILES:lldb-server = ""
ALLOW_EMPTY:${PN}-tools = "1"
ALLOW_EMPTY:${PN}-dev = "1"
ALLOW_EMPTY:${PN}-lldb-python = "1"
ALLOW_EMPTY:lldb-server = "1"
VALGRINDARCH ?= "${TARGET_ARCH}"

RDEPENDS:${PN} += "python3 c++headers gcclibs libgcc libgcc-dev libc6-dev binutils"
# rv32 clang deb ships no libgcc_s; it relies on gcc exposing the rv32 libgcc_s
# under ${libdir}. Pull gcc so the native linker finds -lgcc_s on clang-only images.
RDEPENDS:${PN}:append:riscv32 = " gcc"
INSANE_SKIP:${PN} += "debug-files"
