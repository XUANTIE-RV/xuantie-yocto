SUMMARY = "System performance benchmark"
HOMEPAGE = "http://github.com/akopytov/sysbench"
SECTION = "console/tests"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
inherit autotools
inherit xuantie-set-package-arch-with-mcpu-benchmark
SRC_URI = "git://github.com/akopytov/sysbench;branch=0.5;protocol=https"
SRCREV = "b23a7db377916e424cb555108dc5f784f615993b"

PR="r2"
S = "${WORKDIR}/git"

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
do_compile:prepend() {
    sed -i.bak '/^sysbench_LINK[[:space:]]*=/ {N; s|\$(LIBTOOL).*\\|\$(CCLD) \$(AM_CFLAGS) \$(CFLAGS) \\|; :loop; n; s|\$(LIBTOOLFLAGS)||g; s|\s*--mode=link||g; b loop;}' ${WORKDIR}/build/sysbench/Makefile
}
