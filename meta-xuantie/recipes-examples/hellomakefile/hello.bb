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
    # Force a clean rebuild: Yocto reuses the same WORKDIR across builds and the
    # Makefile only sees source-vs-object timestamps, so an .o produced by a
    # previous TOOLCHAIN / TOOLCHAIN_VENDOR (e.g. XuanTie clang's non-PIC output)
    # can survive into a build with a different toolchain (open-source clang
    # links with -pie by default) and break the link step.
    oe_runmake clean
    oe_runmake
}

do_install () {
	install -d ${D}${bindir}/
	install -m 0755 ${S}/helloYocto ${D}${bindir}/
	install -m 0755 ${S}/zlibtest ${D}${bindir}/
}

FILES:${PN} = "${bindir}/helloYocto \
               ${bindir}/zlibtest "
