FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = " file://0001-Add-special-workaround-to-gdm-service-for-fpga-test.patch"
