DESCRIPTION = "This packagegroup includes debug tools."
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} += " \
    gdb \
    gdbserver \
    gcc-sanitizers-external \
    strace \
    perf \
    trace-cmd \
    memtool \
    kmod \
    binutils \
    systemd-analyze \
"

RDEPENDS:${PN}:append:libc-glibc = " glibc-mtrace"