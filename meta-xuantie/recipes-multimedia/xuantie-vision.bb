DESCRIPTION = "xuantie-vision image."

LICENSE = "CLOSED"

IMAGE_INSTALL += "packagegroup-core-boot kernel-modules"
IMAGE_INSTALL += "npu-ax3386-gpl"
IMAGE_INSTALL += "ffmpeg opencv openh264 util-linux zeromq protobuf rapidjson syslog-ng libc-mtrace"

IMAGE_INSTALL += "tzdata"

IMAGE_INSTALL += " ${@bb.utils.contains('MACHINECONFIG','public','th1520-bsp-proprietary','libcsi-g2d csi-hal-vcodec npu-ax3386',d)} "

export IMAGE_BASENAME = "xuantie-vision"

inherit core-image
