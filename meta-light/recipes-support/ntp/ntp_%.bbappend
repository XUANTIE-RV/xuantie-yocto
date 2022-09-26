FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

COMPATIBLE_MACHINE = "(^d1*)"

SRC_URI_append = " file://ntp.conf \
                   file://ntpd \
                 "
