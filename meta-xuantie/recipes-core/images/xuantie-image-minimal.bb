DESCRIPTION = "A minimal image for xuantie."
LICENSE = "MIT"

IMAGE_INSTALL = "packagegroup-core-boot"
EXTRA_IMAGEDEPENDS += "u-boot"

# fix do_populate_sdk error: "target-sdk-provides-dummy : Conflicts: coreutils"
TOOLCHAIN_TARGET_TASK:remove = " target-sdk-provides-dummy"


python do_rootfs:append:riscv64 () {
    import subprocess

    srcdir=d.getVar('IMAGE_ROOTFS')

    imagename = 'IMAGE_BASENAME={0}\n'.format(d.getVar('IMAGE_BASENAME'))
    with open('%s%s/os-release' % (srcdir, d.getVar('nonarch_libdir')), 'a+') as f:
        f.seek(0)
        lines = f.read()
        if imagename.strip() not in lines:
            f.write(imagename)

    # rm /usr/lib/systemd/
    if 'systemd' not in d.getVar('DISTRO_FEATURES', True).split():
        systemddir = srcdir + '/usr/lib/systemd/'
        if systemddir and os.path.exists(systemddir):
            import shutil
            shutil.rmtree(systemddir)
}

DISTRO_FEATURES:remove = " ipv6 pcmcia usbgadget usbhost pci 3g nfc vfat "

export IMAGE_BASENAME = "xuantie-image-minimal"

inherit core-image
