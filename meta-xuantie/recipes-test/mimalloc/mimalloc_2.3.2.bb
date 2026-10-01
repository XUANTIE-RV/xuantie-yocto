SUMMARY = "mimalloc - compact general purpose allocator with excellent performance"
DESCRIPTION = "mimalloc is a general purpose allocator with excellent \
performance characteristics. It is developed by Microsoft Research and \
is a drop-in replacement for malloc/free/new/delete."

HOMEPAGE = "https://github.com/microsoft/mimalloc"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fade5fb11a9703a4216c1b5133efdc7f"

SRC_URI = "git://github.com/microsoft/mimalloc.git;branch=main;protocol=https"
SRCREV = "02a2f5df9d7d46d30263b83832eebeeab62dc5fe"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = " \
    -DMI_BUILD_SHARED=ON \
    -DMI_BUILD_STATIC=ON \
    -DMI_BUILD_TESTS=OFF \
    -DMI_INSTALL_TOPLEVEL=ON \
"

LDFLAGS:append = " -static"

DISABLE_STATIC = ""

FILES:${PN} += "${libdir}/libmimalloc.so*"
FILES:${PN}-dev += "${libdir}/cmake ${includedir}"
FILES:${PN}-staticdev += "${libdir}/libmimalloc.a"
