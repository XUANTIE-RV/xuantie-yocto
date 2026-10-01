# musl libc 的 toolchain 路径覆盖.
# 期望值见 docs/toolchain-matrix.md, 审计 meta-xuantie/scripts/validate-toolchain-matrix.sh.
# vendor-riscv-gnu 场景下 :libc-musl:vendor-riscv-gnu:archN 优先级更高 (见 xuantie-vendor-riscv-gnu.inc).

TARGET_VENDOR = "-musl"
TCLIBC = "musl"

# Override EXTERNAL_TOOLCHAIN_GCC to point to muslc toolchain based on target arch.
EXTERNAL_TOOLCHAIN_GCC:libc-musl:riscv64 = "${EXTERNAL_TOOLCHAIN_GCC_MUSLC}/rv64"
EXTERNAL_TOOLCHAIN_GCC:libc-musl:riscv32 = "${EXTERNAL_TOOLCHAIN_GCC_MUSLC}/rv32"

# Set correct compiler prefix for muslc toolchains
EXTERNAL_TARGET_SYS:libc-musl:riscv64 = "riscv64-unknown-linux-musl"
EXTERNAL_TARGET_SYS:libc-musl:riscv32 = "riscv32-unknown-linux-musl"

# Provider overrides
PREFERRED_PROVIDER_glibc = "musl"
PREFERRED_PROVIDER_virtual/libc = "musl"
PREFERRED_PROVIDER_virtual/libintl = "musl"
PREFERRED_PROVIDER_virtual/libiconv = "musl"
PREFERRED_PROVIDER_virtual/crypt = "musl"

# sysbench 1.0.x 依赖 LuaJIT(无 rv32 后端)且预编译 MySQL 需 glibc; musl 下显式锁 0.5(纯 Lua 参考实现)
PREFERRED_VERSION_sysbench = "0.5"

# musl does not support glibc locale system
IMAGE_LINGUAS = ""
