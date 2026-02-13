DESCRIPTION = "klitmus7 native external"
LICENSE = "CLOSED"
inherit native
SRC_URI = "ftp://10.63.42.21/CPU-SIT/standalone/klitmus7.tar.gz \
           "
SRC_URI[sha256sum] = "ad4dea40330c8ac1d5fda7176fdf2f634b573c9389450daadfa207725aae98f5"
S = "${WORKDIR}"
do_install() {
    install -d ${D}${prefix}/bin
    install -d ${D}${prefix}/share
    cp -r -d ${S}/klitmus7 ${D}${prefix}/bin
    cp -r ${S}/herdtools7/litmus ${D}${prefix}/share
}
INSANE_SKIP:${PN} += "already-stripped invalid-packageconfig"
