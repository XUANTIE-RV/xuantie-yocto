DESCRIPTION = "Different utilities from Android - corressponding configuration files"
SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://android-gadget-setup \
            file://android-gadget-start \
            file://android-gadget-init \
            "

PACKAGE_ARCH = "${MACHINE_ARCH}"
DEPENDS:append = " update-rc.d-native"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/android-gadget-setup ${D}${bindir}
    install -m 0755 ${WORKDIR}/android-gadget-start ${D}${bindir}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/android-gadget-init ${D}${sysconfdir}/init.d
        update-rc.d -r ${D} android-gadget-init start 10 5 3 2 .
    fi

}

python () {
    pn = d.getVar('PN')
    profprov = d.getVar("PREFERRED_PROVIDER_" + pn)
    if profprov and pn != profprov:
        raise bb.parse.SkipRecipe("PREFERRED_PROVIDER_%s set to %s, not %s" % (pn, profprov, pn))
}
