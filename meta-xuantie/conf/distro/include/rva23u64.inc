require conf/distro/include/rv64.inc
TOOLCHAIN = "clang"

XUANTIE_MARCH = "rva23u64"
# RVA23 profile (and rvb23u64 which requires this file) contains extensions
# such as zimop that are still marked experimental in clang. Set this signal
# so xuantie-passthrough.inc / xuantie-clang.inc inject
# `-menable-experimental-extensions` whenever TOOLCHAIN=clang, regardless of
# TOOLCHAIN_VENDOR (open-source riscv-gnu clang and XuanTie clang both need it).
XUANTIE_ARCH_NEEDS_EXP_EXTENSIONS = "1"

PREFERRED_VERSION_spec2006 = "1.2"
PREFERRED_VERSION_openmpbench = "4.0"

XUANTIE_PACKAGE_FEED_ARCH ?= "rva23"
