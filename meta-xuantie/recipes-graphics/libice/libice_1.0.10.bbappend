FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# 0001-Fix-null-ptr-function-call-in-_IceTransWrite.patch
PR = "r1"

SRC_URI += "file://0001-Fix-null-ptr-function-call-in-_IceTransWrite.patch"
