FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"

SRC_URI_append_ice = " \
             file://tftp-mmc-boot.txt \
            "
SRC_URI = " \
     git://gitee.com/thead-linux/u-boot.git;protocol=http \ 
"

SRCREV = "4f01a69ec30fdfa63413dd958314cb10008e32f6"
