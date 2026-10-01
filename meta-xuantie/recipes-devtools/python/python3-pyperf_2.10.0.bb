SUMMARY = "A toolkit to write, run and analyze benchmarks"
DESCRIPTION = " \
The Python pyperf module is a toolkit to write, run and analyze benchmarks. \
Features: \
    * Simple API to run reliable benchmarks \
    * Automatically calibrate a benchmark for a time budget. \
    * Spawn multiple worker processes. \
    * Compute the mean and standard deviation. \
    * Detect if a benchmark result seems unstable. \
    * JSON format to store benchmark results. \
    * Support multiple units: seconds, bytes and integer. \
"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=78bc2e6e87c8c61272937b879e6dc2f8"

SRC_URI[sha256sum] = "dd93ccfda79214725293e95f1fa6e00cb4a64adcf1326039486d4e1f91caaa62"

PYPI_PACKAGE = "pyperf"
inherit pypi python_setuptools_build_meta

# Overrides meta-python's python3-pyperf 2.4.1 (BBFILE_PRIORITY 6 > 5):
# pyperformance 1.14.0 imports pyperf._hooks, only present since pyperf 2.6+,
# and pins pyperf==2.10.0 in its requirements.
RDEPENDS:${PN} += "${PYTHON_PN}-misc ${PYTHON_PN}-statistics ${PYTHON_PN}-psutil"
