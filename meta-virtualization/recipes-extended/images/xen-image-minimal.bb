DESCRIPTION = "A minimal xen image"

INITRD_IMAGE = "core-image-minimal-initramfs"

XEN_KERNEL_MODULES ?= "kernel-module-xen-blkback kernel-module-xen-gntalloc \
                       kernel-module-xen-gntdev kernel-module-xen-netback kernel-module-xen-wdt \
                       ${@bb.utils.contains('MACHINE_FEATURES', 'pci', "${XEN_PCIBACK_MODULE}", '', d)} \
                       ${@bb.utils.contains('MACHINE_FEATURES', 'acpi', '${XEN_ACPI_PROCESSOR_MODULE}', '', d)} \
                      "

IMAGE_INSTALL += " \
    packagegroup-core-boot \
    packagegroup-core-ssh-openssh \
    ${XEN_KERNEL_MODULES} \
    xen-tools \
    qemu \
    "

# The hypervisor may not be within the dom0 filesystem image but at least
# ensure that it is deployable:
do_build[depends] += "xen:do_deploy"

# Networking for HVM-mode guests (x86/64 only) requires the tun kernel module
IMAGE_INSTALL:append:x86    = " kernel-module-tun"
IMAGE_INSTALL:append:x86-64 = " kernel-module-tun"

# Linux kernel option CONFIG_XEN_PCIDEV_BACKEND depends on X86
XEN_PCIBACK_MODULE = ""
XEN_PCIBACK_MODULE:x86    = "kernel-module-xen-pciback"
XEN_PCIBACK_MODULE:x86-64 = "kernel-module-xen-pciback"
XEN_ACPI_PROCESSOR_MODULE = ""
XEN_ACPI_PROCESSOR_MODULE:x86    = "kernel-module-xen-acpi-processor"
XEN_ACPI_PROCESSOR_MODULE:x86-64 = "kernel-module-xen-acpi-processor"

LICENSE = "MIT"

QB_NETWORK_XEN_BRIDGE = "1"

inherit core-image
# Only inherit the qemuboot classes when building for a qemu machine
QB_QEMU_CLASSES = ""
QB_QEMU_CLASSES:qemuall = "qemuboot-xen-defaults qemuboot-xen-dtb qemuboot-testimage-network"
inherit ${QB_QEMU_CLASSES}

do_check_xen_state() {
    if [ "${@bb.utils.contains('DISTRO_FEATURES', 'xen', ' yes', 'no', d)}" = "no" ]; then
        die "DISTRO_FEATURES does not contain 'xen'"
    fi
}

addtask check_xen_state before do_rootfs

syslinux_iso_populate:append() {
	install -m 0444 ${STAGING_DATADIR}/syslinux/libcom32.c32 ${ISODIR}${ISOLINUXDIR}
	install -m 0444 ${STAGING_DATADIR}/syslinux/mboot.c32 ${ISODIR}${ISOLINUXDIR}
}

syslinux_hddimg_populate:append() {
	install -m 0444 ${STAGING_DATADIR}/syslinux/libcom32.c32 ${HDDDIR}${SYSLINUXDIR}
	install -m 0444 ${STAGING_DATADIR}/syslinux/mboot.c32 ${HDDDIR}${SYSLINUXDIR}
}

grubefi_populate:append() {
	install -m 0644 ${DEPLOY_DIR_IMAGE}/xen-${MACHINE}.gz ${DEST}${EFIDIR}/xen.gz
}

syslinux_populate:append() {
	install -m 0644 ${DEPLOY_DIR_IMAGE}/xen-${MACHINE}.gz ${DEST}/xen.gz
}

SYSLINUX_XEN_ARGS ?= "loglvl=all guest_loglvl=all console=com1,vga com1=115200,8n1"
SYSLINUX_KERNEL_ARGS ?= "ramdisk_size=32768 root=/dev/ram0 rw console=hvc0 earlyprintk=xen console=tty0 panic=10 LABEL=boot debugshell=5"

build_syslinux_cfg () {
	echo "ALLOWOPTIONS 1" > ${SYSLINUX_CFG}
	echo "DEFAULT boot" >> ${SYSLINUX_CFG}
	echo "TIMEOUT 10" >> ${SYSLINUX_CFG}
	echo "PROMPT 1" >> ${SYSLINUX_CFG}
	echo "LABEL boot" >> ${SYSLINUX_CFG}
	echo "  KERNEL mboot.c32" >> ${SYSLINUX_CFG}
	echo "  APPEND /xen.gz ${SYSLINUX_XEN_ARGS} --- /vmlinuz ${SYSLINUX_KERNEL_ARGS} --- /initrd" >> ${SYSLINUX_CFG}
}

# Enable runqemu. eg: runqemu xen-image-minimal nographic slirp
WKS_FILE:x86-64 = "directdisk-xen.wks"
WKS_FILE_DEPENDS_DEFAULT:x86-64 = "syslinux-native"
WKS_FILE:qemux86-64 = "qemuboot-xen-x86-64.wks"
WKS_FILE_DEPENDS_DEFAULT:qemux86-64 = "syslinux-native"
QB_MEM ?= "-m 400"
QB_DEFAULT_KERNEL ?= "none"
QB_DEFAULT_FSTYPE ?= "wic"
QB_DEFAULT_FSTYPE:qemux86-64 = "wic"
QB_FSINFO ?= "wic:kernel-in-fs"
QB_SERIAL_OPT = "-serial mon:stdio"
# qemux86-64 machine does not include 'wic' in IMAGE_FSTYPES, which is needed
# to boot this image, so add it here:
IMAGE_FSTYPES:qemux86-64 += "wic"
# Networking: the qemuboot.bbclass default virtio network device works ok
# and so does the emulated e1000 -- choose according to the network device
# drivers that are present in your dom0 Linux kernel. To switch to e1000:
# QB_NETWORK_DEVICE = "-device e1000,netdev=net0,mac=@MAC@"
