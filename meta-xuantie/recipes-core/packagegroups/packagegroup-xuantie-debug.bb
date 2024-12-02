DESCRIPTION = "This packagegroup includes debug tools."
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} += " \
    gdb \
    gdbserver \
    gcc-sanitizers-external \
    strace \
    glibc-mtrace \
    perf \
    trace-cmd \
    memtool \
    kmod \
    binutils \
    systemd-analyze \
"

