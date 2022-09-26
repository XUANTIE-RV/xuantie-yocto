DESCRIPTION = "A minimal image for light fm."

IMAGE_INSTALL = "packagegroup-core-boot kernel-modules"
IMAGE_INSTALL += " resizefs initscripts-readonly-rootfs-overlay "

LICENSE = "MIT"

python do_rootfs_append_riscv64 () {
    import subprocess

    srcdir=d.getVar('IMAGE_ROOTFS')

    cmd = 'rm -rf %s/boot/* ' % (srcdir)
    subprocess.check_call(cmd, shell=True)
}

DISTRO_FEATURES_remove = " ipv6 pcmcia usbgadget usbhost pci 3g nfc vfat "

export IMAGE_BASENAME = "light-fm-image-minimal"

inherit core-image
