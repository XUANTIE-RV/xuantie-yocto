PACKAGE_ARCH = "${XUANTIE_MCPU_PACKAGE_ARCH}"
XUANTIE_PACKAGE_ARCH_SET = "YES"

XUANTIE_NATIVE_TUNE_CCARGS = " -march=${XUANTIE_NATIVE_MARCH} -mabi=${XUANTIE_MABI} -mtune=${XUANTIE_NATIVE_MTUNE}"
# requires '-menable-experimental-extensions' for experimental extension 'zfbfmin zvfbfmin zvfbfwma'
XUANTIE_NATIVE_TUNE_CCARGS:append:toolchain-clang = " -menable-experimental-extensions"

XUANTIE_TUNE_CCARGS_GENERIC ?= " ${XUANTIE_TUNE_CCARGS}"

XUANTIE_MCPU_GENERIC:riscv32 ?= "c9xx-riscv32"
XUANTIE_MCPU_GENERIC:riscv64 ?= "c9xx-riscv64"
