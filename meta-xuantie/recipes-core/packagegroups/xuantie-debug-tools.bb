DESCRIPTION = "This packagegroup includes debug tools"
LICENSE = "MIT"
PACKAGE_ARCH = "${TUNE_PKGARCH}"
inherit packagegroup

RDEPENDS:${PN} += " \
    gdb \
    trace-cmd \
"

RDEPENDS:${PN}:append:libc-glibc = " glibc-mtrace"

RDEPENDS:${PN}:append:riscv64 = " perf gcc-sanitizers kdump"
