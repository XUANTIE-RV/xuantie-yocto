SUMMARY = "Limited Error Raster Compression (LERC) library"
DESCRIPTION = "LERC is an open-source image or raster compression format \
that is fast and efficient for floating point and integer data, developed \
by Esri."
HOMEPAGE = "https://github.com/Esri/lerc"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=06209abe95b90904e809b2e6f3ba4ea7"

SRC_URI = "https://github.com/Esri/lerc/archive/refs/tags/v${PV}.tar.gz;downloadfilename=${BP}.tar.gz"
SRC_URI[sha256sum] = "91431c2b16d0e3de6cbaea188603359f87caed08259a645fd5a3805784ee30a0"

S = "${WORKDIR}/lerc-${PV}"

inherit cmake

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON"

BBCLASSEXTEND = "native nativesdk"
