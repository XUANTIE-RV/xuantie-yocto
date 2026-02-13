# This doesn't work, since it seems to be too late for sanity checking.
# IMAGE_FEATURES[validitems] += ' ${@bb.utils.contains("DISTRO_FEATURES", "virtualization", "virt-unique-hostname; ", "",d)}'

ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("IMAGE_FEATURES", "virt-unique-hostname", "virt_gen_hostname; ", "",d)}'
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("IMAGE_FEATURES", "virt-unique-hostname", "virt_set_hostname; ", "",d)}'

python virt_gen_hostname() {
    import uuid

    targetname = d.getVar("VIRT_TARGETNAME")
    if targetname != None:
        return

    status, date = oe.utils.getstatusoutput("date +%d-%m-%y")
    if status:
        bb.warn("Can't get the date string for target hostname")

    uuid = ':'.join(['{:02x}'.format((uuid.getnode() >> ele) & 0xff) for ele in range(0,8*6,8)][::-1])
    if uuid:
        targetname = "%s-%s" %  (d.getVar("MACHINE"), uuid.split(":")[0])
    else:
        targetname = "%s-%s" %  (d.getVar("MACHINE"), date)

    d.setVar("VIRT_TARGETNAME", targetname)
}

virt_set_hostname() {
    echo "${VIRT_TARGETNAME}" > ${IMAGE_ROOTFS}/etc/hostname
}
