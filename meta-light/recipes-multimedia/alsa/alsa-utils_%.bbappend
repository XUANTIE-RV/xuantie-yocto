FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = "\
    file://asound_light.conf \
"

do_install_append() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/asound_light.conf ${D}${sysconfdir}/asound.conf
}

FILES_${PN} += "\
    ${sysconfdir} \
"
