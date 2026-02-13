FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# add dev/shm to avoid LTP (remap_file_pages01) failing
SRC_URI += "file://sysfs-shm.sh"


do_install:append() {
    # Don't symbol link '/tmp'->'/var/tmp' to avoid LTP (symlink01 -T chdir01) failing
	sed -i '/l root root 1777 \/tmp \/var\/tmp/d' \
		${D}${sysconfdir}/default/volatiles/00_core

    # add dev/shm to avoid LTP (remap_file_pages01) failing
    install -m 0755    ${WORKDIR}/sysfs-shm.sh		${D}${sysconfdir}/init.d
    update-rc.d -r ${D} sysfs-shm.sh start 02 S .
}

