FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " kms"

SRC_URI:append = " \
    file://0001-WIP-kmssink-Skip-sync-when-atomic-driver-is-detected.patch \
    file://0001-waylandsink-Double-check-the-frame-callback-before-d.patch \
"
