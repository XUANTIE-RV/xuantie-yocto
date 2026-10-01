SUMMARY = "Python Performance Benchmark Suite"
DESCRIPTION = " \
pyperformance is the official Python benchmark suite. \
It runs a curated set of benchmarks from the pyperf project, \
produces JSON results consumable by speed.python.org upstream dashboards. \
"
HOMEPAGE = "https://github.com/python/pyperformance"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ec23723682ec494ce9b9deec2467f25"

SRC_URI[sha256sum] = "91f74393997b604375ad5b79bf569a24076d70181c076a53abad5383a238a8aa"
SRC_URI[md5sum] = "729526cb951b868100ceb2778fa47c2d"

PYPI_PACKAGE = "pyperformance"
inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-pyperf python3-packaging python3-venv python3-pip"
# tomli only needed on python < 3.11 (3.11+ uses stdlib tomllib); target python here is 3.10
RDEPENDS:${PN} += "python3-tomli"
