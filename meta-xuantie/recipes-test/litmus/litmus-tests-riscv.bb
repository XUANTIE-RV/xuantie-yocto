DESCRIPTION = "This litmus-tests-riscv repository contains litmus tests for the RISC-V concurrency architecture"
HOMEPAGE = "https://github.com/litmus-tests/litmus-tests-riscv"
SECTION = "tests"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENCE;md5=3be2c767ef4a97a907bc00ea2b62e582"
S = "${WORKDIR}/git"

SRC_URI = " \
    git://github.com/litmus-tests/litmus-tests-riscv.git;branch=master;protocol=https \
    file://instructions.excl \
    "
SRCREV = "1fe2afdcc121566fda2c97d324c26c58a33ea56d"

S = "${WORKDIR}/git"

inherit xuantie-clang18-compatible

DEPENDS += "bc-native herdtools7-native"

# fix SB+fence.rw.rw+pos-pos-ctrlfencei.c:288: Error: unrecognized opcode `fence.i', extension `zifencei' required
XUANTIE_MARCH:append = "_zifencei"

do_compile:riscv32() {
    oe_runmake instructions.excl
    cp -f ${WORKDIR}/instructions.excl ${S}
    oe_runmake hw-tests CORES=2 GCC="${CC} -Wno-incompatible-pointer-types"
    cp -ar ./hw-tests/run.exe litmus-tests-riscv-run-2core.exe
    rm -rf hw-tests
    oe_runmake hw-tests CORES=4 GCC="${CC} -Wno-incompatible-pointer-types"
    cp -ar ./hw-tests/run.exe litmus-tests-riscv-run-4core.exe
    cp -ar ./hw-tests/run.sh litmus-tests-riscv-run.sh

}

do_compile:riscv64() {
    oe_runmake hw-tests CORES=2 GCC="${CC} -Wno-incompatible-pointer-types"
    cp -ar ./hw-tests/run.exe litmus-tests-riscv-run-2core.exe
    rm -rf hw-tests
    oe_runmake hw-tests CORES=4 GCC="${CC} -Wno-incompatible-pointer-types"
    cp -ar ./hw-tests/run.exe litmus-tests-riscv-run-4core.exe
    cp -ar ./hw-tests/run.sh litmus-tests-riscv-run.sh
}

do_install() {
    install -d ${D}${datadir}/${PN}
    cp -r ${S}/litmus-tests-riscv-run.sh ${D}${datadir}/${PN}
    cp -r ${S}/litmus-tests-riscv-run-2core.exe ${D}${datadir}/${PN}
    cp -r ${S}/litmus-tests-riscv-run-4core.exe ${D}${datadir}/${PN}
}
