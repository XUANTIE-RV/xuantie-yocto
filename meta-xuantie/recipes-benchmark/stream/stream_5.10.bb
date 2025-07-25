SUMMARY = "Stream benchmark"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=bca8cbe07976fe64c8946378d08314b0"

inherit pkgconfig autotools
inherit xuantie-set-package-arch-with-mcpu-benchmark
PACKAGE_ARCH = "${XUANTIE_MCPU}_external"
SRC_URI = "git://github.com/jeffhammond/STREAM.git;protocol=https;branch=master; \
           file://0001-add-static-compile-option.patch "
SRCREV = "4dbce1d0fdb7410e8f21b48f3381bc0a1341967f"
S = "${WORKDIR}/git"

PR="r2"
do_compile() {
    cd ${S}
    make CC="${CC}" FC="${FC}" -j 64
}
do_install() {
    install -d ${D}${bindir}
    cp ${S}/stream_c.exe ${D}${bindir}
}

DEPENDS:append:toolchain-clang = " openmp"
