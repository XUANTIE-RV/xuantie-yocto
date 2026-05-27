SUMMARY = "xuantie Zero Stage Bootloader"
LICENSE = "CLOSED"

inherit deploy

SRC_URI = " \
    ${@bb.utils.contains('MACHINECONFIG', 'public', \
    '${XUANTIE_GIT}/zero_stage_boot;protocol=https;branch=master', \
    'git://github.com/XUANTIE-RV/zero_stage_boot;protocol=https;branch=master', d)} \
"

SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"

CROSS_LDFLAGS:riscv32 = " -melf32lriscv"
CC  = "${CCACHE}${HOST_PREFIX}gcc"
CC:toolchain-clang  = "${CCACHE}${HOST_PREFIX}clang"
LD:toolchain-clang = "${CCACHE}${HOST_PREFIX}ld.lld"
AR:toolchain-clang = "${CCACHE}${HOST_PREFIX}llvm-ar"
CFLAGS = "${XUANTIE_TUNE_CCARGS} -fPIC -fno-stack-protector -misa-spec=2.2"
CFLAGS:toolchain-clang = "${XUANTIE_TUNE_CCARGS} -fPIC -fno-stack-protector"

do_configure:prepend() {
    sed -i '/^CFLAGS = -fPIC -fno-stack-protector$/d' ${S}/Makefile
    sed -i 's/^CC /#CC /g' ${S}/Makefile
    sed -i 's/^CPP /#CPP /g' ${S}/Makefile
    sed -i 's/^AR /#AR /g' ${S}/Makefile
    sed -i 's/^LD /#LD /g' ${S}/Makefile
    sed -i 's/^OBJCOPY /#OBJCOPY /g' ${S}/Makefile
    sed -i 's/^OBJDUMP /#OBJDUMP /g' ${S}/Makefile
}

do_compile:prepend() {
    export CFLAGS="${CFLAGS}"
    export CROSS_LDFLAGS="${CROSS_LDFLAGS}"
}

do_deploy() {
    ARCH_SUFFIX="${@d.getVar('TARGET_ARCH').replace('riscv', 'rv')}"
    install -m 0755 ${S}/zero_stage_boot.bin ${DEPLOYDIR}/zero_stage_boot-v${PV}-${ARCH_SUFFIX}.bin
    install -m 0755 ${S}/zero_stage_boot.elf ${DEPLOYDIR}/zero_stage_boot-v${PV}-${ARCH_SUFFIX}.elf
}

addtask deploy before do_build after do_install
