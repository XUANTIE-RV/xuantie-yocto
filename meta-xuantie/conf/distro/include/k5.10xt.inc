PREFERRED_PROVIDER_virtual/kernel ?= "linux-xuantie"
KBUILD_DEFCONFIG ?= "defconfig"

PREFERRED_VERSION_linux-xuantie = "5.10.y"

SERIAL_CONSOLES = "115200;ttyS0"

# Set OpenSBI version to 0.9 for Kernel 5.10
PREFERRED_VERSION_opensbi = "0.9"

XUANTIE_KERNEL_VERSION = "kernel_5.10"
