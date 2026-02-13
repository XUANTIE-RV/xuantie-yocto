DESCRIPTION = "Plex86/Bochs LGPL VGABios"
HOMEPAGE = "http://www.nongnu.org/vgabios/"
LICENSE = "LGPL-2.1-only"
SECTION = "firmware"

DEPENDS = "dev86-native"

LIC_FILES_CHKSUM = "file://COPYING;md5=fae731a3adbc92fd8bb1730d1f2455bc"

SRC_URI =  "http://savannah.gnu.org/download/vgabios/${BP}.tgz \
            file://build-cc.patch"
SRC_URI[sha256sum] = "481042240ef0f1c918780c92a6bb42ad4d3f5d989b29502fa7ee7faf13a041b9"

EXTRA_OEMAKE = "HOSTCC="${BUILD_CC}""

do_install() {
    install -d ${D}${datadir}/firmware
    for file in VGABIOS*.bin; do
        target=$(echo $file | sed s/VGABIOS-lgpl-latest/${BP}/)
        install -m0644 $file ${D}${datadir}/firmware/$target
    done
}

FILES:${PN} = "${datadir}/firmware/${BP}*.bin"
FILES:${PN}-dbg = "${datadir}/firmware/${BP}*.debug.bin"
