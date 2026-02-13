DESCRIPTION = "Python documentation generator"
HOMEPAGE = "http://sphinx-doc.org/"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=82cc7d23060a75a07b820eaaf75abecf"

PYPI_PACKAGE = "Sphinx"

PV = "4.2.0"

RCONFLICTS:${PN} = "python3-sphinx"

SRC_URI[sha256sum] = "94078db9184491e15bce0a56d9186e0aec95f16ac20b12d00e06d4e36f1058a6"

inherit setuptools3 pypi
