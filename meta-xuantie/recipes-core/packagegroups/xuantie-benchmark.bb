DESCRIPTION = "This packagegroup includes benchmark"
LICENSE = "MIT"
PACKAGE_ARCH = "${TUNE_PKGARCH}"
inherit packagegroup

# aibench wait for add.

RDEPENDS:${PN} += " \
    atomicbench \
    circustent \
    coremark \
    core-to-core-latency \
    csi-nn2-release \
    dhrystone \
    eembc \
    iperf2 \
    iperf3 \
    linpack \
    lmbench \
    nbench \
    spec2006 \
    stream \
    sysbench \
    unixbench \
    whetstone \
"
