DESCRIPTION = "about optee"
LICENSE = "MIT"

inherit packagegroup
DEPENDS = "linux-xuantie"

RDEPENDS:${PN} += " \
    optee-os \
    optee-client \
    optee-test \
    mbedtls \
    optee-examples \
    trusted-firmware-a \
"
