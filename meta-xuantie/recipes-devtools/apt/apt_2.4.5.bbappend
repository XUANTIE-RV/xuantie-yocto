FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:class-native = " \
           file://0001-Fix-the-filename-can-t-be-longer-than-255.patch \
           "

SRC_URI:append = " \
           file://0002-Fix-issue-with-acquiring-locks-on-riscv32.patch \
           file://update-apt-sources-list.sh \
           "

inherit update-rc.d
INITSCRIPT_NAME = "update-apt-sources-list.sh"
INITSCRIPT_PARAMS = "start 99 S ."

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${INIT_D_DIR}
        install -m 0755 ${WORKDIR}/update-apt-sources-list.sh ${D}${INIT_D_DIR}
    fi
}
