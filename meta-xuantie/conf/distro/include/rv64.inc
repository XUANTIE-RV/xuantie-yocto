require conf/distro/include/xuantie-base.inc

XUANTIE_MCPU ?= "c9xx-riscv64"
PACKAGE_EXTRA_ARCHS += "riscv64 c9xx-riscv64 c9xx-riscv64_external"
XUANTIE_MARCH ?= "rv64imafdc"
XUANTIE_MABI ?= "lp64d"

hostname:pn-base-files = "riscv64-xuantie"
