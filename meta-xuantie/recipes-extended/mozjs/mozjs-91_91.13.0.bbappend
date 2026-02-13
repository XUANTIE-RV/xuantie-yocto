
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

DEPENDS += " m4-native"

SRC_URI:append:toolchain-clang = " file://0001-Fix-clang-build-error.patch"
