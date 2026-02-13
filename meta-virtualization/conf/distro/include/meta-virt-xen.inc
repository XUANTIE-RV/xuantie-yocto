# Make sure Xen mode is enabled in qemu
PACKAGECONFIG:append:pn-qemu = " xen"

# For builds for ARM platforms, enable fdt
PACKAGECONFIG:append:pn-qemu:arm = " fdt"
PACKAGECONFIG:append:pn-qemu:aarch64 = " fdt"

# Xen on Raspberry Pi 4 64-bit support
XEN_RPI4_64_CONFIG_PATH ??= \
    'dynamic-layers/raspberrypi/conf/distro/include/xen-raspberrypi4-64.inc'
include ${@bb.utils.contains('MACHINE', 'raspberrypi4-64', \
    '${XEN_RPI4_64_CONFIG_PATH}', '', d)}

# Set serial for working qemuboot console
SERIAL_CONSOLES:qemuarm64 ?= "115200;ttyAMA0"
