HOMEPAGE = "https://pypi.python.org/pypi/Flask-Cors/"
SUMMARY = "A Flask extension adding a decorator for CORS support"
DESCRIPTION = "\
  A Flask extension for handling Cross Origin Resource Sharing (CORS), making cross-origin AJAX possible \
  "
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=118fecaa576ab51c1520f95e98db61ce"

DEPENDS += "python3-six python3-flask"

PYPI_PACKAGE = "Flask-Cors"

SRC_URI[sha256sum] = "b60839393f3b84a0f3746f6cdca56c1ad7426aa738b70d6c61375857823181de"

inherit pypi setuptools3
