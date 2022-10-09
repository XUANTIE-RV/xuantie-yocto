DESCRIPTION = "Hello World and Zlib test"
DEPENDS = "zlib"
SECTION = "libs"
LICENSE = "MIT"
PV = "3"
PR = "r0"

SRC_URI = " \
          file://helloYocto.c \
          file://zlibtest.c \
          file://makefile \
          "

LIC_FILES_CHKSUM = "file://helloYocto.c;md5=2dac018fa193620dc085aa1402e0b346"
S = "${WORKDIR}"
do_compile () {
    make
}

do_install () {
	install -d ${D}${bindir}/
	install -m 0755 ${S}/helloYocto ${D}${bindir}/
	install -m 0755 ${S}/zlibtest ${D}${bindir}/
}

FILES_${PN} = "${bindir}/helloYocto \
               ${bindir}/zlibtest "
