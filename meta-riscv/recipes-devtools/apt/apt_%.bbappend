FILESEXTRAPATHS:prepend := "${THISDIR}/apt:"

SRC_URI:append_ice = " \
            file://sources.list  \
           "
do_install:append_ice() {
    if [ -f "${WORKDIR}/sources.list" ]; then
        cp -rf ${WORKDIR}/sources.list ${D}/etc/apt/ 
    fi
}
