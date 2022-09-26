DESCRIPTION = "This image includes test utils."

LICENSE = "MIT"

IMAGE_INSTALL += " python3 python3-numpy python3-pip python3-psutil python3-ruamel-yaml python3-pytest python3-lxml "
IMAGE_INSTALL += " memtester dhrystone whetstone tinymembench sysbench linpack nbench-byte "
IMAGE_INSTALL += " iozone3 bonnie++ "
IMAGE_INSTALL += " cpupower cpufrequtils "
IMAGE_INSTALL += " kmscube glmark2"
IMAGE_INSTALL += " iperf2 "
IMAGE_INSTALL += " packagegroup-core-boot kernel-modules util-linux bash openssl "
IMAGE_INSTALL += " android-tools android-tools-conf "
IMAGE_INSTALL += " wpa-supplicant bluez5 light-bt "
IMAGE_INSTALL += " memtool curl iw tcpdump gdb strace usbutils mmc-utils e2fsprogs perf "
IMAGE_INSTALL += " alsa-utils alsa-lib alsa-tools pulseaudio-server "
IMAGE_INSTALL += " lrzsz "
IMAGE_INSTALL += " zlib boost libatomic libatomic-dev libatomic-ops initscripts-readonly-rootfs-overlay "

python do_rootfs_append_riscv64 () {
    import subprocess

    srcdir=d.getVar('IMAGE_ROOTFS')

    cmd = 'rm -rf %s/boot/* ' % (srcdir)
    subprocess.check_call(cmd, shell=True)
}

#DISTRO_FEATURES_remove = " ipv6 pcmcia usbgadget usbhost pci 3g nfc vfat "


export IMAGE_BASENAME = "light-fm-image-test"

inherit core-image
