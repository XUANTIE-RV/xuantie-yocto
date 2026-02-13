HOMEPAGE = "http://upx.sourceforge.net"
SUMMARY = "Ultimate executable compressor."

SRCREV = "4e1ae22a1a07be5135c68b25ff05058ae8ae48e1"
SRC_URI = "gitsm://github.com/upx/upx;branch=devel;protocol=https \
    file://0001-MyCom.h-fix-build-with-gcc-11.patch;patchdir=src/lzma-sdk \
"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=353753597aa110e0ded3508408c6374a"

DEPENDS = "zlib libucl xz"

S = "${WORKDIR}/git"

PV = "3.96+${SRCPV}"

EXTRA_OEMAKE += " \
    UPX_UCLDIR=${STAGING_DIR_TARGET} \
    UPX_LZMADIR=${STAGING_DIR_TARGET} \
"

# FIXME: The build fails if security flags are enabled
SECURITY_CFLAGS = ""

do_compile() {
    oe_runmake -C src all
}

do_install:append() {
    install -d ${D}${bindir}
    install -m 755 ${B}/src/upx.out ${D}${bindir}/upx
}

BBCLASSEXTEND = "native"
