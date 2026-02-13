# Xen on Raspberry Pi 4 settings

# Xen uses u-boot for configuration and uses the uart
RPI_USE_U_BOOT = "1"
ENABLE_UART = "1"

# Build the bootable SD card image
IMAGE_CLASSES:append = " sdcard_image-rpi"
IMAGE_FSTYPES:append = " rpi-sdimg"

# Add Xen to the bootable SD card image
RPI_SDIMG_EXTRA_DEPENDS:append = " xen:do_deploy"
DEPLOYPAYLOAD = "xen-${MACHINE}:xen"

# We need the GIC enabled for Xen to work
RPI_EXTRA_CONFIG:append  = "\nenable_gic=1"

# Increase boot partition for space for kernel and Xen
BOOT_SPACE = "61440"

# Override the meta-raspberrypi default kernel preference
PREFERRED_PROVIDER_virtual/kernel ?= "linux-yocto"
KERNEL_DEVICETREE ?= "broadcom/bcm2711-rpi-4-b.dtb"

PREFERRED_PROVIDER_u-boot-default-script ?= "xen-rpi-u-boot-scr"
