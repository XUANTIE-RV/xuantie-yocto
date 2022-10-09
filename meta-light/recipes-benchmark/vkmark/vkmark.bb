SUMMARY = "Vulkan benchmark"
DESCRIPTION = "vkmark is a benchmark for Vulkan"
HOMEPAGE = "https://github.com/vkmark/vkmark"
BUGTRACKER = "https://github.com/vkmark/vkmark/issues"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING-LGPL2.1;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " glm assimp vulkan-loader vulkan-headers wayland wayland-native wayland-protocols"

SRC_URI = "git://github.com/vkmark/vkmark.git;protocol=https"
SRCREV = "53abc4f660191051fba91ea30de084f412e7c68e"

S = "${WORKDIR}/git"

inherit meson pkgconfig
