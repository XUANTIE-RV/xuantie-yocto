#
# Shared uclibc-ng compiler flags configuration
# This file contains common compiler flags and workarounds for uclibc-ng
# that apply to both open-source toolchain and XuanTie toolchain scenarios.
#
# Include this file from toolchain-specific uclibc configurations:
#   - tclibc-uclibc.inc (open-source toolchain with uclibc-ng-external)
#   - xuantie-libc-uclibc-ng.inc (XuanTie toolchain)
#

# 1. C23 compatibility: GCC 15 defaults to C23 which breaks older packages
#    with old-style function definitions.
#    (-std=gnu11 is compatible with GCC 14.x and 15.x)
TARGET_CFLAGS:append = " -std=gnu11"

# 2. Stack protector: uclibc-ng has incomplete stack protector support,
#    which can cause build failures. Disable it for stability.
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
