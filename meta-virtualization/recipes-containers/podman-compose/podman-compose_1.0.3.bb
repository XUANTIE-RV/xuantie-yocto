DESCRIPTION = "An implementation of docker-compose with podman backend"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit setuptools3

SRC_URI = "git://github.com/containers/podman-compose.git;branch=stable;protocol=https"

SRCREV = "24ec539932580a6bc96d6eb2341141b6d7198b39"

S = "${WORKDIR}/git"

DEPENDS += "${PYTHON_PN}-pyyaml-native"

RDEPENDS:${PN} += "${PYTHON_PN}-pyyaml"
