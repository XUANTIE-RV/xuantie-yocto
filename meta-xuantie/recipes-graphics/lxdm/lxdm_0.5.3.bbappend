FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
                  file://0001-lxdm-fix-the-display-of-garbled-characters.patch \
"
