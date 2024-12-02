FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
           file://0001-Fix-issue-with-acquiring-locks-on-riscv32.patch \
           "
