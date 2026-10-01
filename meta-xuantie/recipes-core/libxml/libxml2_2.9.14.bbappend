# Python 3.13 compat for libxml2 2.9.14 python bindings:
# - 0001: configure probes used distutils (removed, PEP 632), leaving the
#   bindings silently disabled (broke itstool-native do_configure and
#   libxml2 do_install_ptest_base). Replace with stdlib sysconfig probes.
# - 0002: bindings call PyEval_CallObject/PyEval_CallMethod, removed from
#   the 3.13 C API; libxml2mod.so linked with undefined symbols and
#   `import libxml2` failed. Replace with PyObject_CallObject/CallMethod.
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "file://0001-configure-replace-distutils-with-stdlib-sysconfig.patch \
            file://0002-python-replace-PyEval-call-APIs-removed-in-3.13.patch \
            "
