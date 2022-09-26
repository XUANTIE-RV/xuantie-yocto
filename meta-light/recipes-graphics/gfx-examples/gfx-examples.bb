SUMMARY = "Graphics Examples for Weston and GTK demo & verifications"
DESCRIPTION = "Some examples to check Weston's features with typical usages"
HOMEPAGE = "https://github.com/examples/gfx-examples.git"
BUGTRACKER = "https://github.com/examples/gfx-examples.git"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3ec9956c919ff2e84914fcefc443760b"

THEAD_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_LINUX_TAG}"

SRC_URI = "git://git@gitee.com/thead-yocto/gfx-examples.git;branch=master;protocol=http"
S = "${WORKDIR}/git"

DEPENDS = "gtk+3 wayland wayland-protocols wayland-native libdrm"
RDEPENDS_${PN} = " gtk+3-demo adwaita-icon-theme-cursors adwaita-icon-theme-symbolic-hires adwaita-icon-theme-symbolic adwaita-icon-theme-hires adwaita-icon-theme weston-examples "

inherit meson pkgconfig
