SUMMARY = "OpenCL benchmark"
DESCRIPTION = "mixbench-opencl is a benchmark for OpenCL"
HOMEPAGE = "https://github.com/ekondis/mixbench"
BUGTRACKER = "https://github.com/ekondis/mixbench"

LICENSE = "GPLv2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=2c1c00f9d3ed9e24fa69b932b7e7aff2"

DEPENDS = "opencl-headers opencl-icd-loader"

SRC_URI = "git://github.com/ekondis/mixbench.git;protocol=https \
           file://0001-Disable-double-and-half2-kernels-as-WA-for-llvm-cras.patch;patchdir=.. \
"
SRCREV = "e1d6c00bd86d7d904b658213370ddb780a116d1f"

S = "${WORKDIR}/git/mixbench-opencl"

inherit pkgconfig cmake

do_install() {
    mkdir -p ${D}${bindir}
    cp ${WORKDIR}/build/mixbench-ocl-alt ${D}${bindir}
    cp ${WORKDIR}/build/mixbench-ocl-ro ${D}${bindir}
    mkdir -p ${D}${datadir}/mixbench/
    cp ${WORKDIR}/build/mix_kernels.cl ${D}${datadir}/mixbench/
    cp ${WORKDIR}/build/mix_kernels_ro.cl ${D}${datadir}/mixbench/
}
