FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:toolchain-clang = " \
    file://0001-Disable-Wcast-function-type-strict.patch"

inherit xuantie-clang18-compatible
