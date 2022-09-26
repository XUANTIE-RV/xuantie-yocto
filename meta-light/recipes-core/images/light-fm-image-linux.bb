DESCRIPTION = "Linux Image for Light FM"
LICENSE = "MIT"

#inherit light bsp related packages
require light-fm-image-bsp.bb
#inherit grapic related packages
require light-fm-image-weston.bb
#inherit security related packages
require light-fm-image-security.bb
#inherit vidio related packages, disable for public release
#require light-fm-image-vision.bb

#WIFI & BT related packages
IMAGE_INSTALL += " wpa-supplicant bluez5 light-bt alsa-tools pulseaudio-server "

#Android tools e.g. adb etc.
IMAGE_INSTALL += " android-tools android-tools-conf "
#debug tools, e.g. collecting crash image for analyzing etc.
IMAGE_INSTALL += " kdump "

# disable for public release
# IMAGE_INSTALL += " khv "

# enable for public release
IMAGE_INSTALL_remove += "csi-hal-vcodec rambus-os-ik-150 gpu-bxm-4-64 npu-ax3386 thead-fce "
IMAGE_INSTALL_remove += "thead-ddr-pmu isp-isp8000l libgal-viv libcsi-g2d vpu-omxil "

# for public release only, disabled by default for internal release
IMAGE_INSTALL += " image-proprietary "

#IMAGE_FSTYPES_remove = "cpio.gz cpio cpio.gz.u-boot cpio.bz2"
export IMAGE_BASENAME = "light-fm-image-linux"
