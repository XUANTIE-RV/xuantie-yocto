COMPATIBLE_MACHINE = "light-*"

SRC_URI = " \
     git://git@gitee.com/thead-yocto/u-boot.git;branch=master;protocol=http \ 
     file://fw_env.config \
     file://0001-no-strip-fw_printenv.patch \
     file://0002-Add-factory-reset-env-to-uboot.patch \
     file://0003-LIGHT-Automatic-version-rollback-when-upgrade-fails.patch \
"

THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"
LICENSE = "CLOSED"

do_configure_append() {
	mkdir ${B}/lib/
	cp ${S}/lib/sec_library ${B}/lib/ -rf
}

do_compile_append () {
	oe_runmake ${UBOOT_MACHINE}
	oe_runmake envtools 
}

SRC_URI += "file://fw_env.config"

do_install_append() {
    install -d ${D}${sysconfdir}
    install -d ${D}${bindir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}
    install -m 0755 ${B}/tools/env/fw_printenv ${D}${bindir}
    ln -rsf ${D}${bindir}/fw_printenv ${D}${bindir}/fw_setenv
}

FILES_${PN} += " ${bindir} "