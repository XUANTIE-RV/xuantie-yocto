DESCRIPTION = "A security image for light fm."

IMAGE_INSTALL += "packagegroup-core-boot kernel-modules op-tee initscripts-readonly-rootfs-overlay"

LICENSE = "MIT"

python do_rootfs_append_riscv64 () {
    import subprocess

    srcdir=d.getVar('IMAGE_ROOTFS')

    cmd = 'rm -rf %s/boot/* ' % (srcdir)
    subprocess.check_call(cmd, shell=True)
}

export IMAGE_BASENAME = "light-fm-image-security"

inherit core-image
