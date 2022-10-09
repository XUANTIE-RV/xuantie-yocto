require conf/machine/include/riscv/tune-riscv.inc

EXTERNAL_TOOLCHAIN ?= "${TOPDIR}/../../riscv-toolchain"
TCMODE="external"
TARGET_PREFIX="riscv64-linux-"
TARGET_ARCH="riscv64"
TARGET_OS="linux"
QEMU_TOOLCHAIN_PATH="${TOPDIR}/../../riscv-toolchain/csky-qemu/bin/"
SYSROOT_PREFIX ??= " riscv64-buildroot-linux-gnu"
EXTERNAL_TOOLCHAIN_SYSROOT="${EXTERNAL_TOOLCHAIN}/${SYSROOT_PREFIX}/sysroot"

INSANE_SKIP_${PN} += "glibc-so gobject-introspection-so glibc-external"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-thead"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot"
PREFERRED_VERSION_openocd-native = "riscv"
PREFERRED_VERSION_openocd = "riscv"

# KERNEL_CLASSES  = "kernel-uimage"
# KERNEL_IMAGETYPES = "itbImage"

KERNEL_IMAGETYPE = "Image"
KERNEL_IMAGETYPES += "uImage"
KEEPUIMAGE = "no"

# MACHINE_FEATURES = "usbgadget usbhost vfat alsa touchscreen"
MACHINE_FEATURES = "screen keyboard ext2 ext3 serial"

## This sets u-boot as the default OpenSBI payload
RISCV_SBI_PAYLOAD ?= "u-boot.bin"

EXTRA_IMAGEDEPENDS += "opensbi"
RISCV_SBI_PLAT = "thead/c910"

EXTRA_IMAGEDEPENDS += "u-boot"
UBOOT_MACHINE = "qemu-riscv64_defconfig"

## Do not update fstab file when using wic images
WIC_CREATE_EXTRA_ARGS ?= "--no-fstab-update"

## wic default support
WKS_FILE_DEPENDS ?= " \
    opensbi \
    e2fsprogs-native \
    bmap-tools-native \
"

IMAGE_BOOT_FILES ?= " \
    fw_payload.bin \
    fitImage \
    boot.scr.uimg \
"

WKS_FILE ?= "xuantie-opensbi.wks"

BAD_RECOMMENDATIONS += "\
    libcxx-dev \
    libcxx-staticdev \
    compiler-rt-dev \
    compiler-rt-staticdev \
"
ASSUME_PROVIDED += "\
    libcxx-dev \
    libcxx-staticdev \
    compiler-rt-dev \
    compiler-rt-staticdev \
"
