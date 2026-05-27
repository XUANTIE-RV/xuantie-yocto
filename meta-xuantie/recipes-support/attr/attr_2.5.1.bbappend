FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:libc-musl = " file://0001-tools-attr.c-include-libgen.h-for-basename.patch"
