DESCRIPTION = "A Linux guest image for the uXen type-2 hypervisor."
LICENSE = "MIT"

inherit core-image

IMAGE_INSTALL += " \
    packagegroup-core-boot \
    uxen-guest-tools \
    "
