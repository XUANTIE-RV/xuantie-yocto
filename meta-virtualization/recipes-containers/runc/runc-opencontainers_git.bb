include runc.inc

SRCREV = "1e7bb5b773162b57333d57f612fd72e3f8612d94"
SRC_URI = " \
    git://github.com/opencontainers/runc;branch=release-1.1;protocol=https \
    file://0001-Makefile-respect-GOBUILDFLAGS-for-runc-and-remove-re.patch \
    "
RUNC_VERSION = "1.1.3"

CVE_PRODUCT = "runc"
