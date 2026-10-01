SUMMARY = "XuanTie BPF/LLVM development package (target-side static libs, headers and cmake configs)"
DESCRIPTION = "Prebuilt target-architecture LLVM/Clang development files used to build \
BPF tooling (bcc/bpftrace) against the XuanTie LLVM release. Ships libLLVM*/libclang* \
static archives, LLVM/Clang/lld headers and cmake configs. Shared libs already provided \
by the clang recipe are not shipped to avoid file conflicts."
HOMEPAGE = "https://gitee.com/xuantie-yocto/"
LICENSE = "CLOSED"
PV = "18.1.8-V2.5.0"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.4.0/bpf_18.1.8_riscv64-5.10.4-rv64gc-lp64d.tar.gz;sha256sum=544360586c0eeeaface5a6ce969c382e321ddab4b59b3d0026de51d30e8a65b7"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.4.0/bpf_18.1.8_riscv64-6.6.36-rv64gc-lp64d.tar.gz;sha256sum=f9ed92ff1b9510dfb08ff0de7160bab56610ff5ffda56591ee30181410ca0d6b"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.4.0/bpf_18.1.8_riscv32-6.6.36-rv32gc-ilp32d.tar.gz;sha256sum=29ce27f6e8073cc8b10af9a682f4ddc7636c6f08e267361116974507dfe1d476"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510}', \
    '${FILE_RV64_66}', d)}"

SRC_URI:riscv32 = "${FILE_RV32_66}"

S = "${WORKDIR}"

PR = "r0"

COMPATIBLE_HOST = "(riscv64|riscv32).*-linux"

INHIBIT_DEFAULT_DEPS = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

INSANE_SKIP:${PN} += "staticdev already-stripped arch file-rdeps dev-so"

do_install() {
    install -d ${D}${includedir} ${D}${libdir}
    cp -r --no-preserve=ownership ${S}/include/* ${D}${includedir}/
    cp -r --no-preserve=ownership ${S}/lib/* ${D}${libdir}/

    # The clang recipe already ships these files; drop them here to keep
    # the two packages stageable into the same recipe sysroot.
    # libclang-cpp.so is kept: the clang deb does not ship it, but the
    # ClangTargets.cmake configs (used by bpftrace) reference it.
    rm -f ${D}${libdir}/libclang.so* \
          ${D}${libdir}/libLTO.so* \
          ${D}${libdir}/libRemarks.so* \
          ${D}${libdir}/LLVMgold.so \
          ${D}${libdir}/LLVMPolly.so
    rm -rf ${D}${includedir}/clang-c
    rm -f ${D}${includedir}/clang/Basic/riscv_vector_common.td
    rm -rf ${D}${libdir}/clang \
           ${D}${libdir}/libear \
           ${D}${libdir}/libscanbuild
    rm -f ${D}${libdir}/cmake/llvm/LLVMConfigExtensions.cmake
    rm -f ${D}${includedir}/llvm-c/lto.h \
          ${D}${includedir}/llvm-c/Remarks.h
    rm -f ${D}${includedir}/llvm/Target/RISCV/RISCVGenRegisterInfo.inc \
          ${D}${includedir}/llvm/Target/RISCV/RISCVGenXCEAsmWriterRegister.inc \
          ${D}${includedir}/llvm/Target/RISCV/RISCVInstrFormats.td \
          ${D}${includedir}/llvm/Target/RISCV/RISCVOperandCommon.h \
          ${D}${includedir}/llvm/Target/RISCV/RISCVRegisterInfo.td \
          ${D}${includedir}/llvm/Target/RISCV/RISCVSystemOperands.td \
          ${D}${includedir}/llvm/Target/RISCV/THEAD/THEADSystemOperands.td
}

FILES:${PN} = "${includedir} ${libdir}"
