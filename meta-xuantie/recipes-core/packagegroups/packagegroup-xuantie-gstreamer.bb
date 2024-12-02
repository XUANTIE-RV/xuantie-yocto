DESCRIPTION = "Gstreamer packagegroup"
LICENSE = "MIT"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

RDEPENDS:${PN} += " packagegroup-xuantie-debug "

RDEPENDS:${PN} += " libomxil "

RDEPENDS:${PN} += " alsa-utils "

RDEPENDS:${PN} += " \
   gstreamer1.0 \
   gstreamer1.0-plugins-base \
   gstreamer1.0-plugins-bad \
   gstreamer1.0-plugins-good \
   gstreamer1.0-plugins-ugly \
   gstreamer1.0-omx \
   gst-shark \
"

RDEPENDS:${PN} += "${@bb.utils.contains('MACHINECONFIG','public','gstreamer-plugin-private-proprietary','gstreamer-plugin-private',d)}"
