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

python do_menuconfig() {
    import shutil
    if d.getVar('TUNE_ARCH') == "riscv32":
        build_path = "/build/fpga_xuantie_riscv32_smode_defconfig/"
    elif d.getVar('TUNE_ARCH') == "riscv64":
        build_path = "/build/fpga_xuantie_riscv64_smode_defconfig/"
    os.system("ln -sf /usr/bin/cc {}".format(d.getVar('WORKDIR')+"/recipe-sysroot-native/usr/bin/riscv64-oe-linux/cc"))
    d.setVar('KCONFIG_CONFIG_ROOTDIR', (d.getVar('WORKDIR') + build_path))
    config = os.path.join(d.getVar('KCONFIG_CONFIG_ROOTDIR'), ".config")
    configorig = os.path.join(d.getVar('KCONFIG_CONFIG_ROOTDIR'), ".config.orig")

    try:
        mtime = os.path.getmtime(config)
        shutil.copy(config, configorig)
    except OSError:
        mtime = 0

    # setup native pkg-config variables (kconfig scripts call pkg-config directly, cannot generically be overriden to pkg-config-native)
    d.setVar("PKG_CONFIG_DIR", "${STAGING_DIR_NATIVE}${libdir_native}/pkgconfig")
    d.setVar("PKG_CONFIG_PATH", "${PKG_CONFIG_DIR}:${STAGING_DATADIR_NATIVE}/pkgconfig")
    d.setVar("PKG_CONFIG_LIBDIR", "${PKG_CONFIG_DIR}")
    d.setVarFlag("PKG_CONFIG_SYSROOT_DIR", "unexport", "1")
    # ensure that environment variables are overwritten with this tasks 'd' values
    d.appendVar("OE_TERMINAL_EXPORTS", " PKG_CONFIG_DIR PKG_CONFIG_PATH PKG_CONFIG_LIBDIR PKG_CONFIG_SYSROOT_DIR")

    oe_terminal("sh -c \"cd %s && make menuconfig; if [ \\$? -ne 0 ]; then echo 'Command failed.'; printf 'Press any key to continue... '; read r; fi\"" % (d.getVar('WORKDIR') + build_path), d.getVar('PN') + ' Configuration', d)

    # FIXME this check can be removed when the minimum bitbake version has been bumped
    if hasattr(bb.build, 'write_taint'):
        try:
            newmtime = os.path.getmtime(config)
        except OSError:
            newmtime = 0

        if newmtime > mtime:
            bb.note("Configuration changed, recompile will be forced")
            bb.build.write_taint('do_compile', d)
}

# Ensure do_menuconfig runs after do_configure
addtask do_menuconfig after do_configure

FILES:${PN} += " ${bindir} "
INSANE_SKIP:${PN} += "installed-vs-shipped"
