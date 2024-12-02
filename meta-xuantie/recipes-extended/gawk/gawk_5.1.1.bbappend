FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:class-target = " file://0001-Fix-negative-NaN-issue-on-RiscV.patch"
