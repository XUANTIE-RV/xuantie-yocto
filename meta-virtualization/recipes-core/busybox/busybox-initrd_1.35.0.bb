FILESEXTRAPATHS:prepend := "${THISDIR}/busybox-initrd:${COREBASE}/meta/recipes-core/busybox/busybox:${COREBASE}/meta/recipes-core/busybox/files:"

require recipes-core/busybox/busybox_${PV}.bb

SRC_URI += "file://init.cfg \
            file://mdev.cfg \
            file://runx.cfg \
            ${@bb.utils.contains('DISTRO_FEATURES', 'vmsep', 'file://static.cfg', '', d)} \
            file://initrd.cfg"

S = "${WORKDIR}/busybox-${PV}"

# override security-flags settings, we have some warnings to ignore
SECURITY_STRINGFORMAT = ""

# we do this to get the target binary available to recipes that
# depend on busybox
SYSROOT_DIRS += "/bin"
BUSYBOX_SPLIT_SUID = "0"

FILES:${PN} += "${sysconfdir}/init.d/*"
