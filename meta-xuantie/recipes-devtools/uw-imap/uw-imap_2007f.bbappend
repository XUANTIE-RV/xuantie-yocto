FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
inherit xuantie-clang18-compatible

SRC_URI:append = " \
           file://0001-disable-warnings-in-c-client-CFLAGS.patch \
           "

