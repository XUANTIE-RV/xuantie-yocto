DESCRIPTION = "This packagegroup includes benchmark"
LICENSE = "MIT"
PACKAGE_ARCH = "${TUNE_PKGARCH}"
inherit packagegroup

# aibench wait for add.

RDEPENDS:${PN} += " \
     \
     \
    coremark \
     \
     \
    dhrystone \
     \
    iperf2 \
    iperf3 \
    linpack \
    lmbench \
     \
     \
     \
    stream \
    sysbench \
    unixbench \
    whetstone \
"
