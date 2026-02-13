SUMMARY = "Device Tree Lopper"
DESCRIPTION = "Tool for manipulation of system device tree files"
LICENSE = "BSD-3-Clause"
SECTION = "bootloader"

SRC_URI = "git://github.com/devicetree-org/lopper.git;branch=master;protocol=https"
SRCREV = "9159040dab25e2f1e7b447fcbfcd5397b1d618e8"
S = "${WORKDIR}/git"

BASEVERSION = "1.0.2"
PV = "v${BASEVERSION}+git${SRCPV}"

PYPA_WHEEL = "${PIP_INSTALL_DIST_PATH}/${BPN}-${BASEVERSION}-*.whl"

LIC_FILES_CHKSUM = "file://LICENSE.md;md5=8e5f5f691f01c9fdfa7a7f2d535be619"

RDEPENDS:${PN} = " \
    python3-core \
    python3-dtc \
    python3-humanfriendly \
    "

inherit setuptools3

INHIBIT_PACKAGE_STRIP = "1"

do_install:append() {
        # we have to remove the vendor'd libfdt, since an attempt to strip it
        # will be made, and it will fail in a cross environment.
        rm -rf ${D}/${PYTHON_SITEPACKAGES_DIR}/${BPN}/vendor
}

BBCLASSEXTEND = "native nativesdk"

