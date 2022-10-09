DESCRIPTION = "Image for HaaS-UI."

require light-fm-image-security.bb
TOOLCHAIN_TARGET_TASK_append += "glib-2.0-dev dbus-dev alsa-lib-dev"

IMAGE_INSTALL += "packagegroup-core-boot kernel-modules"
IMAGE_INSTALL += "gdb"
IMAGE_INSTALL += "perf"
IMAGE_INSTALL += "strace"

IMAGE_INSTALL += "memtool"
IMAGE_INSTALL += "android-tools android-tools-conf"

IMAGE_INSTALL += " alsa-utils alsa-lib alsa-tools pulseaudio-server "
IMAGE_INSTALL += "lrzsz"
IMAGE_INSTALL += "fb-test"

IMAGE_INSTALL += "haas-ui"

IMAGE_INSTALL += "dhcpcd"
IMAGE_INSTALL += "wpa-supplicant"
IMAGE_INSTALL += "fota initscripts-readonly-rootfs-overlay"
CORE_IMAGE_EXTRA_INSTALL += "ntp"

LICENSE = "MIT"

python do_rootfs_append_riscv64 () {
    import subprocess

    srcdir=d.getVar('IMAGE_ROOTFS')

    cmd = 'rm -rf %s/boot/* ' % (srcdir)
    subprocess.check_call(cmd, shell=True)
}

DISTRO_FEATURES_remove = " ipv6 pcmcia usbgadget usbhost pci 3g nfc vfat "

export IMAGE_BASENAME = "light-fm-image-miniapp"
inherit core-image

