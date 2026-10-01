FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:class-native = " file://0001-gspawn-handle-EPERM-from-close_range-on-older-kernel.patch"
