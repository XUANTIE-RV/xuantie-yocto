DESCRIPTION = "Linux lite image for Xuantie"
LICENSE = "MIT"

require xuantie-image-minimal.bb

DEPENDS += "cpio-native"

#testing basic tools
IMAGE_INSTALL += " time unzip ldd file procps"
IMAGE_INSTALL += " wget util-linux-mount grep nfs-utils-mount"
IMAGE_INSTALL += " ntfs-3g ntfs-3g-ntfsprogs ntfsprogs"

#buildroot lite
IMAGE_INSTALL += " hello hexedit memtool phytool os-release "

#remote access services
IMAGE_INSTALL += " inetutils-telnetd haveged nbd-client "

#modules.dep generation
IMAGE_INSTALL += " kmod "

# ntpdate
IMAGE_INSTALL += " ntpdate "

# coreutils
IMAGE_INSTALL:append = " ${@bb.utils.contains('XUANTIE_MARCH', 'rva23u64', 'coreutils', '', d)}"

IMAGE_FEATURES:append = " ssh-server-openssh "

# libc utils
IMAGE_INSTALL:append:libc-glibc = " glibc-external-utils"
IMAGE_INSTALL:append:libc-musl = " musl-external-utils"

#for FPGA
EXTRA_IMAGEDEPENDS += " hw-c9xx "

inherit core-image

export IMAGE_BASENAME = "xuantie-image-lite"
