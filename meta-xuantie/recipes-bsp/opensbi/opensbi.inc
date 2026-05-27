DESCRIPTION = "Opensbi Components"
LICENSE = "CLOSED"

inherit deploy

S = "${WORKDIR}/git"

export PLATFORM = "generic"
EXTRA_OEMAKE:append:riscv32 = " PLATFORM_RISCV_XLEN=32 "
EXTRA_OEMAKE:append:toolchain-clang = " LLVM=1"

# GCC's -ftree-loop-distribute-patterns (enabled at -O2) recognizes simple
# loop patterns (e.g. sbi_strlen's while loop) and replaces them with calls
# to libc functions (strlen, memset, etc.). This is incorrect for OpenSBI
# which is a bare-metal firmware with no libc. The flag only affects GCC;
# clang does not perform this transformation.
OPENSBI_FREESTANDING_FIX = " -fno-tree-loop-distribute-patterns"
OPENSBI_FREESTANDING_FIX:toolchain-clang = ""

do_configure:append() {
	if [ -n "${OPENSBI_FREESTANDING_FIX}" ]; then
		sed -i 's/-ffreestanding/-ffreestanding${OPENSBI_FREESTANDING_FIX}/' ${S}/Makefile
	fi
}

do_deploy () {
	install -m 0755 ${S}/build/platform/generic/firmware/fw_dynamic.bin ${DEPLOYDIR}
	install -m 0755 ${S}/build/platform/generic/firmware/fw_dynamic.elf ${DEPLOYDIR}
	install -m 0755 ${S}/build/platform/generic/firmware/fw_jump.bin ${DEPLOYDIR}
	install -m 0755 ${S}/build/platform/generic/firmware/fw_jump.elf ${DEPLOYDIR}
}

addtask deploy before do_build after do_install
