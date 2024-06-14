SUMMARY = "Simple Hello World Cmake application"
SECTION = "examples"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "ffmpeg"

SRC_URI = "\
            file://CMakeLists.txt \
            file://A/CMakeLists.txt \
            file://A/main.c \
            file://B/CMakeLists.txt \
            file://B/add.c \
            file://B/add.h \
            file://C/CMakeLists.txt \
            file://C/sub.c \
            file://C/sub.h \
        "

S = "${WORKDIR}"

inherit cmake

#OECMAKE_GENERATOR = "Unix Makefiles"
#PARALLEL_MAKEINST = "-j1"

EXTRA_OECMAKE = ""


do_install() {
        mkdir -p ${D}${bindir}/
        mkdir -p ${D}${libdir}/
        cp ${WORKDIR}/build/A/hellocmake ${D}${bindir}/
        cp ${WORKDIR}/build/B/libadd.so ${D}${libdir}/
        cp ${WORKDIR}/build/C/libsub.so ${D}${libdir}/
}


INSANE_SKIP_${PN} += "debug-files file-rdeps dev-deps"
INSANE_SKIP_${PN}-dev += "dev-elf"

