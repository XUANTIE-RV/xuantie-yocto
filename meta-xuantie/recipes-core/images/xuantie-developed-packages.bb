DESCRIPTION = "Linux developed packages for Xuantie"
LICENSE = "MIT"

require ../packagegroups/xuantie-benchmark.bb
require ../packagegroups/xuantie-debug-tools.bb
require ../packagegroups/xuantie-domain-libs.bb
require ../packagegroups/xuantie-domain-components.bb

IMAGE_FSTYPES:remove = "cpio.gz cpio cpio.gz.u-boot cpio.bz2"
export IMAGE_BASENAME = "xuantie-developed-packages"

inherit core-image
do_image_qa[noexec] = "1"
do_image[noexec] = "1"
do_image_ext4[noexec] = "1"
do_image_complete[noexec] = "1"
do_populate_lic_deploy[noexec] = "1"
