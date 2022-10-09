DESCRIPTION = "This image used for compiling test tools only."

LICENSE = "MIT"

IMAGE_INSTALL += " util-linux openssl e2fsprogs "
IMAGE_INSTALL += " cpupower cpufrequtils "
IMAGE_INSTALL += " kmscube glmark2 vkmark mixbench "
IMAGE_INSTALL += " memtool curl tcpdump usbutils mmc-utils net-tools iw perf iperf2 gdb strace netperf "
IMAGE_INSTALL += "initscripts-readonly-rootfs-overlay "

python do_rootfs_append_riscv64 () {
    import subprocess

    srcdir=d.getVar('IMAGE_ROOTFS')

    cmd = 'rm -rf %s/boot/* ' % (srcdir)
    subprocess.check_call(cmd, shell=True)
}

#DISTRO_FEATURES_remove = " ipv6 pcmcia usbgadget usbhost pci 3g nfc vfat "


export IMAGE_BASENAME = "light-fm-image-tools"

inherit core-image
