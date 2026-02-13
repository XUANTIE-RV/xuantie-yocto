FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

DEPENDS += "u-boot-mkimage-native"
DEPENDS += "dtc-native"

XENIMAGE_KERNEL_LOADADDRESS ?= "0x6000000"
do_deploy:append() {
       if [ -f ${DEPLOYDIR}/xen-${MACHINE} ]; then
           uboot-mkimage -A arm64 -T kernel -a ${XENIMAGE_KERNEL_LOADADDRESS} -e ${XENIMAGE_KERNEL_LOADADDRESS} -C none -d ${DEPLOYDIR}/xen-${MACHINE} ${DEPLOYDIR}/xen.ub
       fi
}
