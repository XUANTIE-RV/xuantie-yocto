DESCRIPTION = "HaaS UI Files"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://GPL-2;md5=ef841af1f51e0456cc3c472bed908d71"

inherit systemd
DEPENDS_append = " update-rc.d-native"

RDEPENDS_${PN} += " dbus curl gmp nettle libidn2 gnutls libunistring "
DEPENDS = "libevent"

COMPATIBLE_MACHINE = "(^light*)"

SRC_URI = "\
    file://haasui_app.service \
    file://haasui_app.sh \
    file://usr/lib/libJQuickBase.so \
    file://usr/lib/libfalcon.so \
    file://usr/lib/libjsapi_proxy.so \
    file://usr/lib/libquickjs.so \
    \
    file://usr/bin/appx \
    \
    file://etc/miniapp/resources/fonts/DroidSansFallback.ttf \
    file://etc/miniapp/resources/fonts/falcon-icons.ttf \
    file://etc/miniapp/resources/framework/js-framework.min.bin \
    file://etc/miniapp/resources/framework/js_modules/events.js \
    file://etc/miniapp/resources/framework/js_modules/qjs-dbus.js \
    file://etc/miniapp/resources/framework/js_modules/util.js \
    file://etc/miniapp/resources/cfg.json \
    file://etc/miniapp/resources/local_deviceinfo.json \
    file://etc/miniapp/resources/local_file.json \
    file://etc/miniapp/resources/local_packages.json \
    file://etc/miniapp/resources/presetpkgs/8080211650566964.amr \
    file://etc/miniapp/resources/presetpkgs/8080251822789980.amr \
    file://etc/miniapp/resources/presetpkgs/8180000000000022.amr \
    \
    file://GPL-2 \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${systemd_system_unitdir}

    install -d ${D}${sysconfdir}/miniapp/resources/fonts
    install -d ${D}${sysconfdir}/miniapp/resources/framework/js_modules
    install -d ${D}${sysconfdir}/miniapp/resources/icu
    install -d ${D}${sysconfdir}/miniapp/resources/presetpkgs

    install -m 0755 ${WORKDIR}/usr/bin/appx                                          ${D}${bindir}/appx
    install -m 0644 ${WORKDIR}/usr/lib/lib*.so*                                      ${D}${libdir}/
    install -m 0644 ${WORKDIR}/etc/miniapp/resources/*.json                          ${D}${sysconfdir}/miniapp/resources/
    install -m 0644 ${WORKDIR}/etc/miniapp/resources/fonts/*                         ${D}${sysconfdir}/miniapp/resources/fonts/
    install -m 0644 ${WORKDIR}/etc/miniapp/resources/framework/js-framework.min.bin  ${D}${sysconfdir}/miniapp/resources/framework/js-framework.min.bin
    install -m 0644 ${WORKDIR}/etc/miniapp/resources/framework/js_modules/*          ${D}${sysconfdir}/miniapp/resources/framework/js_modules/
    install -m 0644 ${WORKDIR}/etc/miniapp/resources/presetpkgs/*                    ${D}${sysconfdir}/miniapp/resources/presetpkgs/

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -m 0644 ${WORKDIR}/haasui_app.service                                ${D}${systemd_system_unitdir}/haasui_app.service
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/haasui_app.sh ${D}${sysconfdir}/init.d
        update-rc.d -r ${D} haasui_app.sh start 03 S .
    fi

}

SYSTEMD_SERVICE_${PN} = "haasui_app.service"
# SYSTEMD_AUTO_ENABLE_${PN} = "enable"

FILES_${PN} += " ${bindir} "
FILES_${PN} += " ${libdir} "
FILES_${PN} += " ${systemd_system_unitdir} "
FILES_${PN} += " ${sysconfdir} "

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files already-stripped rpaths "
