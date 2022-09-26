DESCRIPTION = "THead fota service for light"
LICENSE = "CLOSED"

DEPENDS = " dbus update-rc.d-native "
RDEPENDS_${PN} += " dbus-lib glib-2.0 bluez5 zlib bash u-boot util-linux-blkid e2fsprogs-mke2fs initscripts-readonly-rootfs-overlay "

SRC_URI = " \
            git://git@gitee.com/thead-yocto/fota.git;branch=master;protocol=http \
            file://0001-fota.patch \
            file://thead_fota.service \
            file://thead_fota.sh \
            file://ota-burnuboot \
            file://ota-burndiff \
            file://kvtool \
          "

THEAD_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_LINUX_TAG}"

S = "${WORKDIR}/git"


inherit cmake systemd

do_configure() {
    rm -rf ${S}/solutions/fota-service/build
    mkdir -p ${S}/solutions/fota-service/build
    cd ${S}/solutions/fota-service/build
    cmake ..
}

do_compile() {
    cd ${S}/solutions/fota-service/build
    make -j${BB_NUMBER_THREADS}
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/dbus-1/system.d
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${base_sbindir}
    install -d ${D}${base_libdir}

    install -m 0755 ${S}/solutions/fota-service/build/fota-service     ${D}${bindir}/
    install -m 0755 ${WORKDIR}/kvtool                                  ${D}${bindir}/
    install -m 0644 ${S}/solutions/fota-service/fota.conf              ${D}${sysconfdir}/
    install -m 0644 ${S}/solutions/fota-service/fota-dbus.conf         ${D}${sysconfdir}/dbus-1/system.d/

    install -m 0755 ${WORKDIR}/ota-burnuboot                           ${D}${base_sbindir}/
    install -m 0755 ${WORKDIR}/ota-burndiff                            ${D}${base_sbindir}/

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -m 0644 ${WORKDIR}/thead_fota.service                  ${D}${systemd_system_unitdir}/
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/thead_fota.sh ${D}${sysconfdir}/init.d
        update-rc.d -r ${D} thead_fota.sh start 90 5 3 2 .
    fi
}

FILES_${PN} += " ${bindir} ${sysconfdir} ${systemd_system_unitdir} ${base_sbindir} ${base_libdir} "

PACKAGES = "${PN}"

SYSTEMD_SERVICE_${PN} = "thead_fota.service"
