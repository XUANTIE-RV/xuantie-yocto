SUMMARY = "A very basic image with a terminal"


CORE_IMAGE_EXTRA_INSTALL += "\
    packagegroup-core-full-cmdline-utils \
    "

CORE_IMAGE_EXTRA_INSTALL += " lrzsz "

python do_rootfs_append_riscv64 () {
    import subprocess

    srcdir=d.getVar('IMAGE_ROOTFS')
    
    cmd = 'rm -rf %s/boot/* ' % (srcdir)
    subprocess.check_call(cmd, shell=True)
}

DISTRO_FEATURES_remove = " alsa bluetooth ext2 ipv4 ipv6 largefile pcmcia usbgadget usbhost wifi xattr nfs zeroconf pci 3g x11 nfc vfat pulseaudio "

LICENSE = "MIT"

inherit core-image
