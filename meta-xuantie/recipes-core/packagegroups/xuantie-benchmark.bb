DESCRIPTION = "This packagegroup includes benchmark"
LICENSE = "MIT"
PACKAGE_ARCH = "${TUNE_PKGARCH}"
inherit packagegroup

RDEPENDS:${PN} += " \
    coremark \
    dhrystone \
    unixbench \
    lmbench \
    sysbench \
    iperf3 \
    iperf2 \
    aibench \
    csi-nn2-release \
"
