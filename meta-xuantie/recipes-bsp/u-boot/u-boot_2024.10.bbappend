SRC_URI += " \
	file://0001-set-bootcmd-for-qemu.patch \
"
DEPENDS += "opensbi"

UBOOT_CONFIG:riscv64 = "general-rv64"
UBOOT_CONFIG[general-rv64] = "qemu-riscv64_smode_defconfig"

UBOOT_CONFIG:riscv32 = "general-rv32"
UBOOT_CONFIG[general-rv32] = "qemu-riscv32_smode_defconfig"


FILES:${PN} += " ${bindir} "
INSANE_SKIP:${PN} += "installed-vs-shipped"
