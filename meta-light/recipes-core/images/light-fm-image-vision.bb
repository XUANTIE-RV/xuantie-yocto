DESCRIPTION = "Thead-vision image."

LICENSE = "CLOSED"

IMAGE_INSTALL += "packagegroup-core-boot kernel-modules"
IMAGE_INSTALL += "pnna npu-ax3386-gpl"
IMAGE_INSTALL += "ffmpeg util-linux zeromq protobuf rapidjson syslog-ng libc-mtrace"

# disable below packages for public release
# IMAGE_INSTALL += "libcsi-g2d thead-fce csi-hal-vcodec npu-ax3386"
# IMAGE_INSTALL += "tmedia visual-ai initscripts-readonly-rootfs-overlay"

export IMAGE_BASENAME = "light-fm-image-vision"

inherit core-image
