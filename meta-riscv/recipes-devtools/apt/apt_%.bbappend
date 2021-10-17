FILESEXTRAPATHS_prepend := "${THISDIR}/apt:"

SRC_URI_append_ice = " \
            file://sources.list  \
           "
do_install_append_ice() {
    if [ -f "${WORKDIR}/sources.list" ]; then
        cp -rf ${WORKDIR}/sources.list ${D}/etc/apt/ 
    fi
}
