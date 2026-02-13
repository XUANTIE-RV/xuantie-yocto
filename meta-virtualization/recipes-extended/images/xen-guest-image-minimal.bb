DESCRIPTION = "A Xen guest image."

inherit core-image features_check

IMAGE_INSTALL += " \
    packagegroup-core-boot \
    ${@bb.utils.contains('MACHINE_FEATURES', 'acpi', '${XEN_ACPI_PROCESSOR_MODULE}', '', d)} \
    "

XEN_ACPI_PROCESSOR_MODULE = ""
XEN_ACPI_PROCESSOR_MODULE:x86    = "kernel-module-xen-acpi-processor"
XEN_ACPI_PROCESSOR_MODULE:x86-64 = "kernel-module-xen-acpi-processor"

IMAGE_INSTALL += "${@bb.utils.contains('IMAGE_FEATURES', 'x11', ' xf86-video-fbdev', '', d)}"

# Install xf86-video-vesa on x86 platforms.
IMAGE_INSTALL:append:x86-64 = "${@bb.utils.contains('IMAGE_FEATURES', 'x11', ' xf86-video-vesa', '', d)}"
IMAGE_INSTALL:append:x86    = "${@bb.utils.contains('IMAGE_FEATURES', 'x11', ' xf86-video-vesa', '', d)}"

REQUIRED_DISTRO_FEATURES += "${@bb.utils.contains('IMAGE_FEATURES', 'x11', ' x11', '', d)} xen"

LICENSE = "MIT"

# Send console messages to xen console
APPEND += "console=hvc0"
