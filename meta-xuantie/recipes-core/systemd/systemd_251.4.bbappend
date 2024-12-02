
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0100-Drop-OnFailure-from-local-fs.target.patch \
"

do_install:append () {
    echo "DefaultLimitCORE=infinity" >> ${D}${sysconfdir}/systemd/system.conf
}
