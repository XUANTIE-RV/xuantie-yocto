DESCRIPTION = "qemu native external"
LICENSE = "CLOSED"

inherit native

DEPENDS = "glib-2.0-native zlib-native ninja-native meson-native"

PV = "4.1.0"

do_install() {
    install -d ${D}${prefix}

    cp -r -d ${QEMU_TOOLCHAIN_PATH}../* ${D}${prefix}
}

INSANE_SKIP:${PN} += "already-stripped"
