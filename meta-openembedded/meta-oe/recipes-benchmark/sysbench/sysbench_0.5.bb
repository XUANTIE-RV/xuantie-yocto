SUMMARY = "System performance benchmark"
HOMEPAGE = "http://github.com/akopytov/sysbench"
SECTION = "console/tests"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit autotools
inherit xuantie-set-package-arch-with-mcpu
SRC_URI = "git://github.com/akopytov/sysbench;branch=0.5;protocol=https"
SRCREV = "b23a7db377916e424cb555108dc5f784f615993b"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

# Extend cpu configuration for different xuantie cpu
python () {
    if d.getVar('XUANTIE_MCPU'):
        d.setVar('TUNE_CCARGS', d.getVar('TUNE_CCARGS') + " -mcpu=" + d.getVar('XUANTIE_MCPU'))
}

EXTRA_OECONF += "--enable-largefile"
PACKAGECONFIG ??= ""
PACKAGECONFIG[aio] = "--enable-aio,--disable-aio,libaio,"
PACKAGECONFIG[mysql] = "--with-mysql \
                        --with-mysql-includes=${STAGING_INCDIR}/mysql \
                        --with-mysql-libs=${STAGING_LIBDIR}, \
                        --without-mysql,mysql5"

do_configure:prepend() {
    touch ${S}/NEWS ${S}/AUTHORS
}
