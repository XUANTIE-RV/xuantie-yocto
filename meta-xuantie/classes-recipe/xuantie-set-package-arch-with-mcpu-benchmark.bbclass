PACKAGE_ARCH = "${XUANTIE_MCPU}"
XUANTIE_PACKAGE_ARCH_SET = "YES"

XUANTIE_TUNE_CCARGS_GENERIC ?= " ${XUANTIE_TUNE_CCARGS}"

XUANTIE_MCPU_GENERIC:riscv32 ?= "c9xx-riscv32"
XUANTIE_MCPU_GENERIC:riscv64 ?= "c9xx-riscv64"

CC = "riscv64-linux-gcc"
CFLAGS = " -O2 "
CFLAGS += " -Wno-implicit-int -Wno-deprecated-non-prototype -Wno-implicit-function-declaration -Wno-return-type -Wno-return-mismatch "
LDFLAGS = " -O2 -static"
FULL_OPTIMIZATION = ""

# Extend cpu configuration for different xuantie cpu
python () {
    if d.getVar('XUANTIE_MCPU') and d.getVar('XUANTIE_MCPU') != "c9xx-riscv32" and d.getVar('XUANTIE_MCPU') != "c9xx-riscv64":
        d.setVar('CC', d.getVar('CC') + " -mcpu=" + d.getVar('XUANTIE_MCPU'))
        d.setVar('CXX', d.getVar('CXX') + " -mcpu=" + d.getVar('XUANTIE_MCPU'))
}

