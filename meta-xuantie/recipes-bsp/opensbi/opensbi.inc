DESCRIPTION = "Opensbi Components"
LICENSE = "CLOSED"

inherit deploy

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/git"

export PLATFORM = "generic"
EXTRA_OEMAKE:append:riscv32 = " PLATFORM_RISCV_XLEN=32 "

do_deploy () {
	install -m 0755 ${S}/build/platform/generic/firmware/fw_dynamic.bin ${DEPLOYDIR}
	install -m 0755 ${S}/build/platform/generic/firmware/fw_dynamic.elf ${DEPLOYDIR}
	install -m 0755 ${S}/build/platform/generic/firmware/fw_jump.bin ${DEPLOYDIR}
	install -m 0755 ${S}/build/platform/generic/firmware/fw_jump.elf ${DEPLOYDIR}
}

addtask deploy before do_build after do_install
