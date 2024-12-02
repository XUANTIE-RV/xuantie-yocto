FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:class-native = " \
           file://0001-Fix-the-filename-can-t-be-longer-than-255.patch \
           "

SRC_URI:append = " \
           file://0002-Fix-issue-with-acquiring-locks-on-riscv32.patch \
           "
