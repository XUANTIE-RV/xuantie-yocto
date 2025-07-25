DESCRIPTION = "Linux toolchain image for Xuantie"
LICENSE = "MIT"

require xuantie-image-minimal.bb

# install packages
# IMAGE_INSTALL += "hello"

IMAGE_FEATURES:append = " ssh-server-openssh "

#for FPGA
EXTRA_IMAGEDEPENDS += " hw-c9xx "

inherit core-image

export IMAGE_BASENAME = "xuantie-image-toolchain"
