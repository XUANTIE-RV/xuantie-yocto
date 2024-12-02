DESCRIPTION = "GNOME packagegroup"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} += " \
    packagegroup-core-x11 \
    packagegroup-core-x11-base \
    packagegroup-core-x11-xserver \
    packagegroup-gnome-desktop \
    glmark2 \
    mixbench \
    net-tools \
    alsa-utils \
    gpgme \
    gnupg \
    utouch-evemu \
    kmscube \
    haveged \
    gnome-settings-daemon \
    packagegroup-gnome-apps-dev \
    packagegroup-gnome-desktop-dev \
"
# gnome-shell_42.0 need install
RDEPENDS:${PN} += "gnome-shell-gsettings"

# remove "vkmark \" after glmark2 because build error, to fix it,
# /usr/include/vulkan/vulkan.hpp should exists on build host,
# and fix it by install libvulkan-dev packet on host.
