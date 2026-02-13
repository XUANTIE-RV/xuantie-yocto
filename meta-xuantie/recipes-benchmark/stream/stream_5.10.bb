SUMMARY = "Stream benchmark"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=bca8cbe07976fe64c8946378d08314b0"

inherit pkgconfig autotools
inherit xuantie-set-package-arch-with-mcpu-benchmark
PACKAGE_ARCH = "${XUANTIE_MCPU}_external"
STREAM_GIT_SRC_URI = "git://github.com/jeffhammond/STREAM.git;protocol=https;branch=master"
SRC_URI = "${STREAM_GIT_SRC_URI} \
           file://0001-add-static-compile-option.patch "

SRC_URI:toolchain-clang = "${STREAM_GIT_SRC_URI} \
           file://0001-Add-riscv64-linux-to-gcc-and-gfortran.patch "
SRCREV = "4dbce1d0fdb7410e8f21b48f3381bc0a1341967f"

S = "${WORKDIR}/git"

# Extend cpu configuration for different xuantie cpu
python () {
    if d.getVar('XUANTIE_MCPU') and (d.getVar('XUANTIE_MCPU') != d.getVar('XUANTIE_MCPU_GENERIC')):
        d.setVar('TUNE_CCARGS', d.getVar('TUNE_CCARGS') + " -mcpu=" + d.getVar('XUANTIE_MCPU'))
    else:
        d.setVar('TUNE_CCARGS', d.getVar('TUNE_CCARGS') + " " + d.getVar('XUANTIE_TUNE_CCARGS_GENERIC'))
}

TUNE_CCARGS:append:toolchain-clang = " -mllvm -xt-enable-vectorization"

PR="r2"

do_compile() {
    cd ${S}
    oe_runmake CC="${CC}" FC="${FC}"
}

do_compile:toolchain-clang() {
    cd ${S}
    oe_runmake CC="${CC}" FC="${CCACHE}${HOST_PREFIX}flang-new ${TOOLCHAIN_OPTIONS}"
}

do_install() {
    install -d ${D}${bindir}
    cp ${S}/stream_c.exe ${D}${bindir}
}

DEPENDS:append:toolchain-clang = " openmp"
