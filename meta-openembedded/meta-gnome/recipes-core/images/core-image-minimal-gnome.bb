DESCRIPTION = "A GNOME minimal demo image."

IMAGE_INSTALL += "packagegroup-core-boot \
    packagegroup-core-x11 \
    packagegroup-gnome-desktop \
    weston \
    glmark2 \
    gnome-calculator \
    gnome-calendar \
    gnome-settings-daemon \
    packagegroup-core-x11-base \
    packagegroup-core-x11-xserver \
    kernel-modules \
"

inherit features_check
REQUIRED_DISTRO_FEATURES = "x11"

IMAGE_LINGUAS ?= " "

LICENSE = "MIT"

export IMAGE_BASENAME = "core-image-minimal-gnome"

inherit core-image
