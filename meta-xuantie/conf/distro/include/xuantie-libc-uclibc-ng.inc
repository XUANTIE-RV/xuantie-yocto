# uclibc-ng 的 toolchain 路径覆盖.
# 期望值见 docs/toolchain-matrix.md, 审计 meta-xuantie/scripts/validate-toolchain-matrix.sh.
# vendor-riscv-gnu 场景下 :libc-uclibc-ng:vendor-riscv-gnu:archN 优先级更高 (见 xuantie-vendor-riscv-gnu.inc).

TARGET_VENDOR = "-uclibc"
TCLIBC = "uclibc"

# tclibc-uclibc.inc is automatically included by defaultsetup.conf based on TCLIBC=uclibc

# Override EXTERNAL_TOOLCHAIN_GCC to point to uclibc-ng toolchain based on target arch.
EXTERNAL_TOOLCHAIN_GCC:libc-uclibc-ng:riscv64 = "${EXTERNAL_TOOLCHAIN_GCC_UCLIBC_NG}/rv64"
EXTERNAL_TOOLCHAIN_GCC:libc-uclibc-ng:riscv32 = "${EXTERNAL_TOOLCHAIN_GCC_UCLIBC_NG}/rv32"

# Set correct compiler prefix for uclibc-ng toolchains
EXTERNAL_TARGET_SYS:libc-uclibc-ng:riscv64 = "riscv64-unknown-linux-uclibc"
EXTERNAL_TARGET_SYS:libc-uclibc-ng:riscv32 = "riscv32-unknown-linux-uclibc"

# Provider overrides
PREFERRED_PROVIDER_glibc = "uclibc-ng"
PREFERRED_PROVIDER_virtual/libc = "uclibc-ng"
PREFERRED_PROVIDER_virtual/libintl = "uclibc-ng"
PREFERRED_PROVIDER_virtual/libiconv = "uclibc-ng"
PREFERRED_PROVIDER_virtual/crypt = "uclibc-ng"

# uclibc-ng does not support glibc locale system
IMAGE_LINGUAS = ""

# uclibc-ng compiler flags configuration:
# 1. C23 compatibility: GCC 15 defaults to C23 which breaks older packages
#    (-std=gnu11 is compatible with GCC 14.x and 15.x)
TARGET_CFLAGS:append = " -std=gnu11"

# 2. Stack protector: uclibc-ng has incomplete stack protector support
SECURITY_CFLAGS:remove = "-fstack-protector-strong"
SECURITY_CFLAGS:remove = "-fstack-protector"
SECURITY_CFLAGS:append = " -fno-stack-protector"

# 3. GNU extensions: Enable GNU-specific functions (fork, etc.)
#    Note: Not all glibc GNU extensions are available in uclibc-ng
TARGET_CFLAGS:append = " -D_GNU_SOURCE"

# 4. RV32 workaround for missing section symbols in __uClibc_main.os:
#    The prebuilt rv32 uclibc-ng toolchain's __uClibc_main.os was compiled
#    without -DSHARED, causing undefined references to __preinit_array_start,
#    __init_array_start, __fini_array_start, etc. when linking shared libraries.
#    This workaround provides weak symbols to satisfy the linker.
TARGET_LDFLAGS:append:riscv32 = " -Wl,--defsym=__preinit_array_start=0,--defsym=__preinit_array_end=0,--defsym=__init_array_start=0,--defsym=__init_array_end=0,--defsym=__fini_array_start=0,--defsym=__fini_array_end=0"
