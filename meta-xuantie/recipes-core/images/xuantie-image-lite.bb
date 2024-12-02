DESCRIPTION = "Linux lite image for Xuantie"
LICENSE = "MIT"

require xuantie-image-minimal.bb

#testing basic tools
IMAGE_INSTALL += " glibc-external-utils time unzip ldd binutils file procps"

#buildroot lite
IMAGE_INSTALL += " hello hexedit memtool phytool os-release "

#remote access services
IMAGE_INSTALL += " inetutils-telnetd haveged nbd-client "
IMAGE_FEATURES:append = " ssh-server-openssh "

#for FPGA
EXTRA_IMAGEDEPENDS += " hw-c9xx "

inherit core-image

export IMAGE_BASENAME = "xuantie-image-lite"
