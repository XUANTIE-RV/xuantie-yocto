SUMMARY = "The low-level, core functionality of boto 3."
HOMEPAGE = "https://github.com/boto/botocore"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI[sha256sum] = "c853d6c2321e2f2328282c7d49d7b1a06201826ba0e7049c6975ab5f22927ea8"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-jmespath python3-dateutil python3-logging"
