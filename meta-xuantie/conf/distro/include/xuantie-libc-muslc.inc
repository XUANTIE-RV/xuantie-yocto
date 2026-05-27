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

# musl does not support glibc locale system
IMAGE_LINGUAS = ""
