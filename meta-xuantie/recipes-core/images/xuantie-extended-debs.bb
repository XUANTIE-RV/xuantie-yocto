DESCRIPTION = "Linux extended debs for Xuantie"
LICENSE = "MIT"

require xuantie-common-packages.bb

IMAGE_INSTALL:append = " \
    cpufp \
    lzbench \
    lzbench-xuantie-test \
    smoketest-xt \
"

IMAGE_INSTALL:append = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', ' ', 'klitmus', d)}"
IMAGE_INSTALL:append:riscv64 = " \
    bcc \
    bpftrace \
    core-to-core-latency \
    dragonwell \
    kvmtool \
    qemu-kvm \
    qemu-test \
    specjbb2015 \
    kvm-unit-tests \
"

IMAGE_INSTALL:append:riscv64:libc-glibc = " glibc-benchtests"

IMAGE_INSTALL:remove = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    'kvmtool kvm-unit-tests qemu-kvm qemu-test', ' ', d)}"

inherit core-image
export IMAGE_BASENAME = "xuantie-extended-debs"

do_rootfs[noexec] = "1"
do_image[noexec] = "1"
do_image_complete[noexec] = "1"
