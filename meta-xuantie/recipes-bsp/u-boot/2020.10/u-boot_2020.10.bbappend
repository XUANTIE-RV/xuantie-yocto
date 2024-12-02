SRC_URI += " \
     file://0001-Add-compile-flags-misa-spec-2.2.patch \
     file://0002-Add-xuantie_qemu-riscv64_spl_defconfig.patch \
     file://0003-Add-xuantie_qemu-riscv32_spl_defconfig.patch \
     file://0001-enable-opensbi-debug-log.patch \
"

DEPENDS += "opensbi"

UBOOT_CONFIG:riscv64 = "general-rv64"
UBOOT_CONFIG[general-rv64] = "xuantie_qemu-riscv64_spl_defconfig"

UBOOT_CONFIG:riscv32 = "general-rv32"
UBOOT_CONFIG[general-rv32] = "xuantie_qemu-riscv32_spl_defconfig"

do_configure:append() {
     cp ${DEPLOY_DIR}/images/${MACHINE}/fw_dynamic.bin ${B}/${config}
}

do_install:append() {
     install -m 0755 ${B}/${config}/u-boot.itb          ${D}/boot
     install -m 0755 ${B}/${config}/spl/u-boot-spl.bin  ${D}/boot
}

do_deploy:append() {
     install -D -m 0755 ${B}/${config}/u-boot.itb           ${DEPLOYDIR}/u-boot.itb-general
     install -D -m 0755 ${B}/${config}/spl/u-boot-spl.bin   ${DEPLOYDIR}/u-boot-spl.bin-general
}

FILES:${PN} += " ${bindir} "
INSANE_SKIP:${PN} += "installed-vs-shipped"
