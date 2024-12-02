SRC_URI += " \
	file://0001-set-bootcmd-for-qemu.patch \
	file://0002-add-xuantie-fpga-defconfig.patch \
	file://0003-add-xuantie-fpga-dts.patch \
	file://0004-fix-uart-32bit-reg-and-do-not-relocate-kernel-image.patch \
        file://0005-add-xuantie-fpga-board.patch \
        file://0006-modify-mmu-type-and-sys-mem-in-dts.patch \
"
DEPENDS += "opensbi"

UBOOT_CONFIG:riscv64 = "general-rv64-fpga"
UBOOT_CONFIG[general-rv64-fpga] = "fpga_xuantie_riscv64_smode_defconfig"

UBOOT_CONFIG:riscv32 = "general-rv32-fpga"
UBOOT_CONFIG[general-rv32-fpga] = "fpga_xuantie_riscv32_smode_defconfig"


FILES:${PN} += " ${bindir} "
INSANE_SKIP:${PN} += "installed-vs-shipped"
