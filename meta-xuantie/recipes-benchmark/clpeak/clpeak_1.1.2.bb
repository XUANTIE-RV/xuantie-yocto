DESCRIPTION = "clpeak - A tool which profiles OpenCL devices to find their peak capacities"
HOMEPAGE = "https://github.com/krrishnarraj/clpeak"
LICENSE = "CLOSED"

SRC_URI = "gitsm://github.com/krrishnarraj/clpeak.git;branch=master;protocol=https"
SRCREV = "82de99a86f95b76d1edd25805f1ed8592eeb6bf0"

S = "${WORKDIR}/git"

inherit cmake

DEPENDS = "opencl-headers opencl-icd-loader"

do_configure[network] = "1"

OECMAKE_GENERATOR = "Unix Makefiles"

do_configure:prepend () {
    sed -i 's/set( CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY )/#set( CMAKE_FIND_ROOT_PATH_MODE_INCLUDE BOTH )/g' ${WORKDIR}/toolchain.cmake
}
