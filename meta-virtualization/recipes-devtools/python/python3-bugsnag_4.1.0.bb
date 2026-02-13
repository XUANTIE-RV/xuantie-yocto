HOMEPAGE = "https://bugsnag.com/"
SUMMARY = "Automatic error monitoring for django, flask, etc."
DESCRIPTION = "\
  The official Python notifier for `Bugsnag <https://bugsnag.com/>`_. \
  Provides support for automatically capturing and sending exceptions \
  in your Django and other Python apps to Bugsnag, to help you find \
  and solve your bugs as fast as possible. \
  "
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "dcbd59cd9edea26cc92efb6518aed83a2f356f81bfd5acc730bfe202fb27c1c1"

inherit pypi setuptools3

DEPENDS += "python3-webob python3-flask python3-blinker"

