SUMMARY = "A very basic Wayland image with a terminal"


IMAGE_FEATURES += "package-management ssh-server-dropbear hwcodecs"

IMAGE_INSTALL += "initscripts-readonly-rootfs-overlay "

CORE_IMAGE_EXTRA_INSTALL += "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    "

CORE_IMAGE_EXTRA_INSTALL += "vim python3"

inherit core-image
