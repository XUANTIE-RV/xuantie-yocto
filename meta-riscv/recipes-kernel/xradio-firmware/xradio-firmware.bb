DESCRIPTION = "Xradio xr829 WiFi firmware"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=0ceb3372c9595f0a8067e55da801e4a1"

PV = "1.0"
PR = "r0"

COMPATIBLE_MACHINE = "(^d1*)"

SRC_URI = "\
    file://boot_xr829.bin \
    file://etf_xr829.bin \
    file://fw_xr829.bin \
    file://fw_xr829_bt_40M.bin \
    file://sdd_xr829_40M.bin \
    file://fw_xr829_bt.bin \
    file://sdd_xr829.bin \
    file://xr829.conf \
    file://xr829_bt_firmware_v7.14.18_e_40m.bin \
    file://fw_xr829_C09.08.52.73_DBG_02_122.bin \
    "


do_install() {
    install -d ${D}${base_libdir}/firmware
    install -m 0644 ${WORKDIR}/boot_xr829.bin ${D}${base_libdir}/firmware/boot_xr829.bin
    install -m 0644 ${WORKDIR}/etf_xr829.bin ${D}${base_libdir}/firmware/etf_xr829.bin
    # install -m 0644 ${WORKDIR}/fw_xr829.bin ${D}${base_libdir}/firmware/fw_xr829.bin
    install -m 0644 ${WORKDIR}/fw_xr829_C09.08.52.73_DBG_02_122.bin ${D}${base_libdir}/firmware/fw_xr829.bin
    # CONFIG_XR829_USE_40M_SDD=y
    install -m 0644 ${WORKDIR}/sdd_xr829_40M.bin ${D}${base_libdir}/firmware/sdd_xr829.bin
    # install -m 0644 ${WORKDIR}/fw_xr829_bt_40M.bin ${D}${base_libdir}/firmware/fw_xr829_bt.bin
    install -m 0644 ${WORKDIR}/xr829_bt_firmware_v7.14.18_e_40m.bin ${D}${base_libdir}/firmware/fw_xr829_bt.bin
    # CONFIG_XR829_USE_40M_SDD=n
    # install -m 0644 ${WORKDIR}/sdd_xr829.bin ${D}${base_libdir}/firmware/sdd_xr829.bin
    # install -m 0644 ${WORKDIR}/fw_xr829_bt.bin ${D}${base_libdir}/firmware/fw_xr829_bt.bin

    install -d ${D}${sysconfdir}/modules-load.d
    install -m 0644 ${WORKDIR}/xr829.conf ${D}${sysconfdir}/modules-load.d

}

FILES_${PN} = "${base_libdir}/* \
        ${sysconfdir}/modules-load.d/*"

PACKAGES = "${PN}"
