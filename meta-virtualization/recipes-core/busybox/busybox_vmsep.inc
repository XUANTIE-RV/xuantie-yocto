FILESEXTRAPATHS:prepend := "${THISDIR}/busybox:"
SRC_URI += " \
            file://static.cfg \
           "

# we do this to get the target binary available to recipes that
# depend on busybox
SYSROOT_DIRS += "/bin"
BUSYBOX_SPLIT_SUID = "0"
