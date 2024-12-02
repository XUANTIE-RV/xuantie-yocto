require conf/distro/include/xuantie-base.inc

XUANTIE_MCPU ?= "c9xx-riscv64"
PACKAGE_EXTRA_ARCHS += "riscv64 c9xx-riscv64"
XUANTIE_MARCH ?= "rv64imafdc"
XUANTIE_MABI ?= "lp64d"
EXTERNAL_TOOLCHAIN_LIB_DIR_NAME ?= "lib64xthead"
EXTERNAL_TOOLCHAIN_ABI_DIR_NAME ?= "${XUANTIE_MABI}"

hostname:pn-base-files = "riscv64-xuantie"
