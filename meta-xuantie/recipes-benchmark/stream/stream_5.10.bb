SUMMARY = "Stream benchmark"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=bca8cbe07976fe64c8946378d08314b0"

inherit pkgconfig autotools
inherit xuantie-set-package-arch-with-mcpu-benchmark
PACKAGE_ARCH = "${XUANTIE_MCPU_PACKAGE_ARCH}_external"
STREAM_GIT_SRC_URI = "git://github.com/jeffhammond/STREAM.git;protocol=https;branch=master"
SRC_URI = "${STREAM_GIT_SRC_URI} \
           file://0001-add-static-compile-option.patch "

SRC_URI:toolchain-clang = "${STREAM_GIT_SRC_URI} \
           file://0001-Add-riscv64-linux-to-gcc-and-gfortran.patch "
SRCREV = "4dbce1d0fdb7410e8f21b48f3381bc0a1341967f"

S = "${WORKDIR}/git"

PR="r2"

do_compile() {
    cd ${S}
    oe_runmake CC="${CC}" FC="${FC}"
}

do_compile:toolchain-clang:libc-glibc() {
    cd ${S}
    oe_runmake CC="${CC}" FC="${CCACHE}${HOST_PREFIX}flang-new ${TOOLCHAIN_OPTIONS}"
}

do_install() {
    install -d ${D}${bindir}
    cp ${S}/stream_c.exe ${D}${bindir}
}

DEPENDS:append:toolchain-clang:libc-glibc = " openmp"

DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'xtstring-opt', ' libxtstring-muslc', '', d)}"
export LIBS = "${LD_LIBXTSTRING}"
