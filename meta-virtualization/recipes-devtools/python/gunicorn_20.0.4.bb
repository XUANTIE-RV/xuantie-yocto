SUMMARY = "WSGI HTTP Server for UNIX"
DESCRIPTION = "\
  Gunicorn ‘Green Unicorn’ is a Python WSGI HTTP Server for UNIX. It’s \
  a pre-fork worker model ported from Ruby’s Unicorn project. The \
  Gunicorn server is broadly compatible with various web frameworks, \
  simply implemented, light on server resource usage, and fairly speedy. \
  " 
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f75f3fb94cdeab1d607e2adaa6077752"

SRC_URI = "https://pypi.python.org/packages/source/g/gunicorn/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "543669fcbb5739ee2af77184c5e571a1"
SRC_URI[sha256sum] = "1904bb2b8a43658807108d59c3f3d56c2b6121a701161de0ddf9ad140073c626"

inherit setuptools3
