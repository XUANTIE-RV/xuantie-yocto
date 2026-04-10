SUMMARY = "xuantie Zero Stage Bootloader"
LICENSE = "CLOSED"

inherit deploy

SRC_URI = " \
    ${@bb.utils.contains('MACHINECONFIG', 'public', \
    '${XUANTIE_GIT}/zero_stage_boot;protocol=https;branch=master', \
    'git://github.com/XUANTIE-RV/zero_stage_boot;protocol=https;branch=master', d)} \
"

# ci: Bump opensbi to v1.7
SRCREV = "4952ac88e4fde9a0530a9995cf34b0caafcba010"
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
