PR = "rc1"

# add iommu
TEST_LIST:append = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', ' ', ' iommu', d)}"
KERNEL_SELFTEST_SRC:append = " drivers"
