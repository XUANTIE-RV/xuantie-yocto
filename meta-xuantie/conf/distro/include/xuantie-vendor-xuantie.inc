# XuanTie vendor toolchain - default configuration
#
# 本文件设置 EXTERNAL_TOOLCHAIN_GCC / EXTERNAL_TOOLCHAIN_CLANG / EXTERNAL_TARGET_SYS
# 的 vendor-xuantie 路径覆盖. 同变量也在 xuantie-libc-muslc.inc / xuantie-libc-uclibc-ng.inc
# 用 :libc-* 覆盖, 最终值由 OVERRIDES 顺序决定.
# 期望矩阵: docs/toolchain-matrix.md
# 审计脚本: meta-xuantie/scripts/validate-toolchain-matrix.sh

# Enable vendor-xuantie override for conditional variable resolution
OVERRIDES:append = ":vendor-xuantie"

# GCC_VERSION is auto-detected by tcmode-external.inc via:
#   GCC_VERSION = "${@external_run(d, d.getVar('EXTERNAL_CC'), '-dumpversion').rstrip()}"
# This executes ${EXTERNAL_TOOLCHAIN_BIN}/${EXTERNAL_TARGET_SYS}-gcc -dumpversion
# No need to override here unless the external toolchain mechanism fails.

# Target triplet: XuanTie toolchain uses riscv64-unknown-linux-gnu-* binaries
# for both rv64 and rv32 targets (rv32 is supported via multilib flags)
EXTERNAL_TARGET_SYS:vendor-xuantie = "riscv64-unknown-linux-gnu"

# muslc toolchain: XuanTie musl toolchain uses riscv{32,64}-unknown-linux-musl-* prefix
EXTERNAL_TARGET_SYS:libc-musl:vendor-xuantie:riscv64 = "riscv64-unknown-linux-musl"
EXTERNAL_TARGET_SYS:libc-musl:vendor-xuantie:riscv32 = "riscv32-unknown-linux-musl"

# uclibc-ng toolchain: XuanTie uclibc toolchain uses riscv{32,64}-unknown-linux-uclibc-* prefix
EXTERNAL_TARGET_SYS:libc-uclibc-ng:vendor-xuantie:riscv64 = "riscv64-unknown-linux-uclibc"
EXTERNAL_TARGET_SYS:libc-uclibc-ng:vendor-xuantie:riscv32 = "riscv32-unknown-linux-uclibc"

# Toolchain base directories - auto-select based on KERNEL version
# KERNEL=5.10-default/5.10-debug → uses k5.10 toolchain path
# KERNEL=6.6-default/6.6-debug → uses default (k6.6) toolchain path
EXTERNAL_TOOLCHAIN_GCC = "${YOCTOROOT}/xuantie-toolchain-gcc"
EXTERNAL_TOOLCHAIN_GCC:kernel-5.10 = "${YOCTOROOT}/xuantie-toolchain-gcc-k5.10"

EXTERNAL_TOOLCHAIN_CLANG = "${YOCTOROOT}/xuantie-toolchain-clang"
EXTERNAL_TOOLCHAIN_CLANG:kernel-5.10 = "${YOCTOROOT}/xuantie-toolchain-clang-k5.10"

# muslc toolchain base directory (used by xuantie-libc-muslc.inc)
EXTERNAL_TOOLCHAIN_GCC_MUSLC = "${YOCTOROOT}/xuantie-toolchain-gcc-muslc"
EXTERNAL_TOOLCHAIN_GCC_MUSLC:kernel-5.10 = "${YOCTOROOT}/xuantie-toolchain-gcc-muslc-k5.10"
