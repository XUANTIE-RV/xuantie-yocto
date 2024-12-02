require conf/distro/include/xuantie-base.inc

TUNE_ARCH = "riscv32"
DEFAULTTUNE = "${TUNE_ARCH}"
EXTERNAL_TARGET_SYS = "riscv64-linux"
XUANTIE_MCPU ?= "c9xx-riscv32"
PACKAGE_EXTRA_ARCHS += "riscv32 c9xx-riscv32"
XUANTIE_MARCH ?= "rv32imafdc"
XUANTIE_MABI ?= "ilp32d"
EXTERNAL_TOOLCHAIN_LIB_DIR_NAME ?= "lib32"

hostname:pn-base-files = "riscv32-xuantie"
