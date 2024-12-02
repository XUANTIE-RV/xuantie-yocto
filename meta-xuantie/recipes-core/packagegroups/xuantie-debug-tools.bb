DESCRIPTION = "This packagegroup includes debug tools"
LICENSE = "MIT"
PACKAGE_ARCH = "${TUNE_PKGARCH}"
inherit packagegroup

RDEPENDS:${PN} += " \
    gdb \
    glibc-mtrace \
    trace-cmd \
"

RDEPENDS:${PN}:append:riscv64 = " perf gcc-sanitizers kdump"
