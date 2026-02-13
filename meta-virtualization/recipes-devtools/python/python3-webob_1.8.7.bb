DESCRIPTION = "WSGI request and response object"
HOMEPAGE = "http://webob.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/license.txt;md5=8ed3584bcc78c16da363747ccabc5af5"

PYPI_PACKAGE = "WebOb"

SRC_URI[sha256sum] = "b64ef5141be559cfade448f044fa45c2260351edcb6a8ef6b7e00c7dcef0c323"

inherit setuptools3 pypi

RDEPENDS:${PN} += " \
	python3-sphinx \
	"

