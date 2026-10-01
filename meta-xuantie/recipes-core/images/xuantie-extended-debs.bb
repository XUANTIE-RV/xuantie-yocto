DESCRIPTION = "Linux extended debs for Xuantie"
LICENSE = "MIT"

require xuantie-common-packages.bb

IMAGE_INSTALL:append = " \
     \
     \
     \
     \
"

IMAGE_INSTALL:append = " ${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', ' ', '', d)}"
IMAGE_INSTALL:append:riscv64 = " ${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', ' ', 'bcc bpftrace', d)}"
IMAGE_INSTALL:append:riscv64 = " \
     \
     \
     \
     \
     \
     \
    kvm-unit-tests \
"

IMAGE_INSTALL:append:riscv64:libc-glibc = " "

IMAGE_INSTALL:remove = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    ' kvm-unit-tests  ', ' ', d)}"

inherit core-image
export IMAGE_BASENAME = "xuantie-extended-debs"

do_rootfs[noexec] = "1"
do_image[noexec] = "1"
do_image_complete[noexec] = "1"
