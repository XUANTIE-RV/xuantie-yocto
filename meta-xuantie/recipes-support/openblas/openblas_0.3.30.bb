DESCRIPTION = "OpenBLAS is an optimized BLAS library"
HOMEPAGE = "http://www.openblas.net/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5adf4792c949a00013ce25d476a2abc0"

inherit siteinfo

RDEPENDS:${PN} += "libgomp"

SRCREV = "v0.3.30"
SRC_URI = "git://github.com/OpenMathLib/OpenBLAS.git;protocol=https;branch=develop"
S = "${WORKDIR}/git"
CC  = "${CCACHE}${HOST_PREFIX}gcc ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
CXX = "${CCACHE}${HOST_PREFIX}g++ ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
CC:toolchain-clang  = "${CCACHE}${HOST_PREFIX}clang ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
CXX:toolchain-clang = "${CCACHE}${HOST_PREFIX}clang++ ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
CFLAGS = "-O2"
LDFLAGS = ""
FULL_OPTIMIZATION = ""

FC:toolchain-clang = "${CCACHE}${HOST_PREFIX}flang-new"
OPENBLAS_TARGET ?= "RISCV64_ZVL128B"
EXTRA_OEMAKE += " \
    TARGET=${OPENBLAS_TARGET} \
    CFLAGS="-DTARGET=${OPENBLAS_TARGET}" \
    HOSTCC="${BUILD_CC}" \
    CC="${CC}" \
    FC="${FC}" \
    PREFIX=${exec_prefix} \
    CROSS_SUFFIX=${HOST_PREFIX} \
    DESTDIR=${D} \
    NOFORTRAN=1 \
    CROSS=1 \
    "

do_install() {
    oe_runmake install
	rmdir ${D}${bindir}
}

FILES:${PN} = "${libdir}/*"
FILES:${PN}-dev = "${includedir} ${libdir}/lib${PN}.so ${libdir}/pkgconfig ${libdir}/cmake"

BBCLASSEXTEND = "nativesdk"

inherit xuantie-set-package-arch-with-mcpu
python () {
    if d.getVar('XUANTIE_MCPU') and (d.getVar('XUANTIE_MCPU') != d.getVar('XUANTIE_MCPU_GENERIC')):
        d.setVar('TUNE_CCARGS', d.getVar('TUNE_CCARGS') + " -mcpu=" + d.getVar('XUANTIE_MCPU'))
    else:
        d.setVar('TUNE_CCARGS', d.getVar('TUNE_CCARGS') + " " + d.getVar('XUANTIE_TUNE_CCARGS_GENERIC'))
}

inherit xuantie-prebuilt-src
