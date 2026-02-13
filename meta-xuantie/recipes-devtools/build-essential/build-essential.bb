SUMMARY = "A collection of essential build tools"
DESCRIPTION = "This package includes a set of basic tools required for building software, including the GNU Compiler Collection (gcc), make, and other utilities. It is designed to provide all necessary components for compiling most applications on this platform."
LICENSE = "CLOSED"

inherit packagegroup
RDEPENDS:${PN} += " \
    clang \
    dpkg \
    gcc \
    gdb \
    glibc \
    libc6-dev \
    libstdc++-dev \
    make \
"
INSANE_SKIP:${PN} += "dev-deps"
