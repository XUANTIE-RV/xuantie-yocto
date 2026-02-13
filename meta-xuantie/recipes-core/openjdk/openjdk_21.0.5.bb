DESCRIPTION = "OpenJDK"
LICENSE = "CLOSED"


DEPENDS += "alsa-utils"

SRC_URI = " \
    https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.5%2B11/OpenJDK21U-jdk_${TUNE_ARCH}_linux_hotspot_21.0.5_11.tar.gz \
"

SRC_URI[sha256sum] = "2f1b3e401e36de803398dfb9818861f9f14ca8ae7db650ea0946ab048fefe3b9"

B = "${WORKDIR}/jdk-21.0.5+11"

do_install() {
    mkdir -p ${D}${bindir}
    mkdir -p ${D}${libdir}/${PN}-${PV}
    cp -r -d ${B}/* ${D}${libdir}/${PN}-${PV}
    for i in ${D}${libdir}/${PN}-${PV}/bin/*; do
        if [ -f "$i" ]; then
            filename=$(basename "$i")
            ln -sf "${libdir}/${PN}-${PV}/bin/$filename" "${D}${bindir}/$filename"
        fi
    done
}


INSANE_SKIP:${PN} = "libdir"

FILES:${PN} += "${libdir} ${bindir}"
