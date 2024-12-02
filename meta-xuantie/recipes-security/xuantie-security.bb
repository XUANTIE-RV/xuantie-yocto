DESCRIPTION = "A security image for light fm."

IMAGE_INSTALL += "packagegroup-core-boot kernel-modules op-tee"
IMAGE_INSTALL += "tzdata"

LICENSE = "MIT"

python do_rootfs:append:riscv64 () {
    import subprocess

    srcdir=d.getVar('IMAGE_ROOTFS')

    cmd = 'rm -rf %s/boot/* ' % (srcdir)
    subprocess.check_call(cmd, shell=True)
}

export IMAGE_BASENAME = "xuantie-security"

inherit core-image
