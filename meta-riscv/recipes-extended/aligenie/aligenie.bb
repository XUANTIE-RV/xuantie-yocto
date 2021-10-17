DESCRIPTION = "Aligenie UI Files"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://GPL-2;md5=ef841af1f51e0456cc3c472bed908d71"

inherit systemd

RDEPENDS_${PN} += " curl gnutls"
DEPENDS = "libevent"

COMPATIBLE_MACHINE = "d1"

SRC_URI = "\
    file://haasui_app.service \
    \
    file://test_file \
    \
    file://GPL-2 \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${systemd_system_unitdir}

    # install -d ${D}${sysconfdir}/miniapp/resources/fonts
    # install -d ${D}${sysconfdir}/miniapp/resources/framework/js_modules
    # install -d ${D}${sysconfdir}/miniapp/resources/icu
    # install -d ${D}${sysconfdir}/miniapp/resources/presetpkgs

    # install -m 0644 ${WORKDIR}/haasui_app.service                                    ${D}${systemd_system_unitdir}/haasui_app.service
    # install -m 0755 ${WORKDIR}/usr/bin/appx                                          ${D}${bindir}/appx
    # install -m 0644 ${WORKDIR}/usr/lib/lib*.so*                                      ${D}${libdir}/
    # install -m 0644 ${WORKDIR}/etc/miniapp/resources/*.json                          ${D}${sysconfdir}/miniapp/resources/
    # install -m 0644 ${WORKDIR}/etc/miniapp/resources/fonts/*                         ${D}${sysconfdir}/miniapp/resources/fonts/
    # install -m 0644 ${WORKDIR}/etc/miniapp/resources/framework/js-framework.min.bin  ${D}${sysconfdir}/miniapp/resources/framework/js-framework.min.bin
    # install -m 0644 ${WORKDIR}/etc/miniapp/resources/framework/js_modules/*          ${D}${sysconfdir}/miniapp/resources/framework/js_modules/
    # install -m 0644 ${WORKDIR}/etc/miniapp/resources/presetpkgs/*                    ${D}${sysconfdir}/miniapp/resources/presetpkgs/


    install -m 0755 ${WORKDIR}/gaopan_file               ${D}${bindir}/gaopan_file


}

# SYSTEMD_SERVICE_${PN} = "haasui_app.service"
# SYSTEMD_AUTO_ENABLE_${PN} = "enable"

FILES_${PN} += " ${bindir} "
FILES_${PN} += " ${libdir} "
FILES_${PN} += " ${systemd_system_unitdir} "
FILES_${PN} += " ${sysconfdir} "

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files already-stripped"
