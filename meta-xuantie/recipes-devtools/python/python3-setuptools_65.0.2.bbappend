# Python 3.13 compatibility for oe-core langdale's setuptools 65.0.2.
# meta-xuantie pins python3 to 3.13.4 (conf/distro/include/xuantie-base.inc)
# while langdale still ships setuptools 65.0.2, which predates py3.12/3.13.
#
# 0) setuptools 65.0.2's pkg_resources registers finders keyed on
#    pkgutil.ImpImporter at import time; that attribute was removed in CPython
#    3.12, so `import setuptools` aborts on the 3.13 backport, breaking every
#    native recipe that uses setuptools (meson-native, glib-2.0-native, ...).
#    Guard the two legacy registrations. Applies to native + target.
FILESEXTRAPATHS:prepend := "${THISDIR}/python3-setuptools:"
SRC_URI += "file://0001-pkg_resources-guard-pkgutil.ImpImporter-for-py3.13.patch"
#
# 1) lib2to3/2to3 was removed from CPython (deprecated 3.11, gone in 3.13), so
#    the python3 recipe no longer produces python3-2to3. Drop setuptools'
#    vestigial RDEPENDS on it (setuptools >=58 no longer uses 2to3).
RDEPENDS:${PN}:remove = "${PYTHON_PN}-2to3"
#
# 2) distutils was removed from CPython in 3.12, so python3 no longer produces
#    python3-distutils. setuptools bundles its own copy and installs
#    distutils-precedence.pth (the _distutils_hack), so `import distutils`
#    keeps working at runtime whenever setuptools is installed. Declare that:
#    setuptools RPROVIDES python3-distutils, satisfying the many langdale
#    consumers (pip, glib-2.0, ...) that still RDEPEND on it. Mirrors how
#    upstream oe-core folded distutils into setuptools past 3.12.
RPROVIDES:${PN}:append:class-target = " ${PYTHON_PN}-distutils"
RPROVIDES:${PN}:append:class-native = " ${PYTHON_PN}-distutils-native"
RPROVIDES:${PN}:append:class-nativesdk = " nativesdk-${PYTHON_PN}-distutils"
