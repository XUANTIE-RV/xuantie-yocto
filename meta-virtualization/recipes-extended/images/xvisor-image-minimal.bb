DESCRIPTION = "A minimal Xvisor image"

INITRD_IMAGE = "core-image-minimal-initramfs"

IMAGE_INSTALL += " \
    packagegroup-core-boot \
    packagegroup-core-ssh-openssh \
    "

# The hypervisor may not be within the dom0 filesystem image but at least
# ensure that it is deployable:
do_build[depends] += "xvisor:do_deploy"

LICENSE = "MIT"

inherit core-image

# Enable runqemu. eg: runqemu xvisor-image-minimal nographic slirp
QB_DEFAULT_KERNEL = "vmm.bin"
QB_OPT_APPEND:append:riscv64 = " -cpu rv64,x-h=true "
