DESCRIPTION = "Simple Vulkan compute shader peak performance tester"
HOMEPAGE = "https://github.com/nihui/vkpeak"
LICENSE = "CLOSED"

SRC_URI = "gitsm://github.com/nihui/vkpeak.git;branch=master;protocol=https"
SRCREV = "67f8ae18bf5e162278ebad48808250849ff443d7"

S = "${WORKDIR}/git"

inherit cmake

DEPENDS = "vulkan-headers vulkan-loader"

do_install:append () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/vkpeak ${D}${bindir}
}
