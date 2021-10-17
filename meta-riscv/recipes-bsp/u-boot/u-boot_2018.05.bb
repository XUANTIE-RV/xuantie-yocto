require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"
PE = "1"

DEPENDS += "bc-native dtc-native"

SRCREV="${AUTOREV}"

SRC_URI = " \
    git://git@gitlab.alibaba-inc.com/thead-linux/d1_u-boot.git;branch=d1-miniapp;protocol=ssh"

COMPATIBLE_MACHINE = "(^d1*)"

do_compile_prepend() {
    cp -af ${S}/scripts/dtc ${B}/scripts/
}
