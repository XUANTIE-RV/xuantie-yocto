DESCRIPTION = "qemu native external"
LICENSE = "CLOSED"

inherit native

DEPENDS = "glib-2.0-native zlib-native ninja-native meson-native"

PV = "5.2.4"
XUANTIE_QEMU_NATIVE_SRC_URI_PATH ?= "ftp://swftp01.damo.xrvm.cn/Test/Test/qemu/V5.2.4"
SRC_URI = "${XUANTIE_QEMU_NATIVE_SRC_URI_PATH}/Xuantie-qemu-x86_64-Ubuntu-20.04-V5.2.4-B20250322-0925.tar.gz;subdir=git"
SRC_URI[sha256sum] = "e9a1ee4f39485c1c47688553b1ec28eb7b807b46b507489f044c88da830dbf63"
S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${prefix}

    if [ -d ${S}/install ]; then
        cp -r -d ${S}/install/* ${D}${prefix}
    else
        cp -r -d ${S}/* ${D}${prefix}
    fi
}

INSANE_SKIP:${PN} += "already-stripped invalid-packageconfig"
