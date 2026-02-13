SUMMARY = "TCMALLOC"
DESCRIPTION = "TCMalloc is Google's customized implementation \
of C's malloc() and C++'s operator new used for memory \
allocation within our C and C++ code. TCMalloc is a fast, \
multi-threaded malloc implementation."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=762732742c73dc6c7fbe8632f06c059a"

inherit autotools


SRC_URI = "git://github.com/gperftools/gperftools.git;branch=master;protocol=https"

SRCREV = "80cdbea9a9be8d0541a162cb6bc9d2119c34d913"
S = "${WORKDIR}/git"

# enable static libs
DISABLE_STATIC = ""
