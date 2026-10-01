SUMMARY = "Optimized zlib/deflate/inflate library"
DESCRIPTION = "libdeflate is a library for fast, whole-buffer DEFLATE-based \
compression and decompression, heavily optimized for performance."
HOMEPAGE = "https://github.com/ebiggers/libdeflate"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=7b6977026437092191e9da699ed9f780"

SRC_URI = "https://github.com/ebiggers/libdeflate/archive/refs/tags/v${PV}.tar.gz;downloadfilename=${BP}.tar.gz"
SRC_URI[sha256sum] = "27bf62d71cd64728ff43a9feb92f2ac2f2bf748986d856133cc1e51992428c25"

S = "${WORKDIR}/libdeflate-${PV}"

inherit cmake

EXTRA_OECMAKE = "-DLIBDEFLATE_BUILD_SHARED_LIB=ON -DLIBDEFLATE_BUILD_STATIC_LIB=OFF"

BBCLASSEXTEND = "native nativesdk"
