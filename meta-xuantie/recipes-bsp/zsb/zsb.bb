SUMMARY = "xuantie Zero Stage Bootloader"
LICENSE = "CLOSED"

inherit deploy

SRCREV = "0844f754ba7f7f86afea99c8423d48ea989d9a8a"
XUANTIE_GIT = "git://git@gitee.com/xuantie-yocto/zero_stage_boot.git"
XUANTIE_GIT_MIRRORS = "git://git@github.com/XUANTIE-RV/zero_stage_boot.git"
MIRRORS += "${XUANTIE_GIT} ${XUANTIE_GIT_MIRRORS}"
SRC_URI = " \
    ${XUANTIE_GIT_MIRRORS};branch=master;protocol=https \
    ${XUANTIE_GIT};branch=master;protocol=https \
"
PV = "1.2"
S = "${WORKDIR}/git"

CROSS_LDFLAGS:riscv32 = " -melf32lriscv"

do_configure:prepend() {
    sed -i '/^CFLAGS = -fPIC -fno-stack-protector$/d' ${S}/Makefile
}

do_compile:prepend() {
    export CROSS_COMPILE=${CCACHE}${HOST_PREFIX}
    export CFLAGS="${XUANTIE_TUNE_CCARGS} -fPIC -fno-stack-protector -misa-spec=2.2"
    export CROSS_LDFLAGS="${CROSS_LDFLAGS}"
}

do_deploy() {
    install -m 0755 ${S}/zero_stage_boot.bin ${DEPLOYDIR}
    install -m 0755 ${S}/zero_stage_boot.elf ${DEPLOYDIR}
}

addtask deploy before do_build after do_install
