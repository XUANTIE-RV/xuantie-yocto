SUMMARY = "OpenPGM reliable multicast protocol library"
DESCRIPTION = "OpenPGM is an open source implementation of the Pragmatic \
General Multicast (PGM) specification in RFC 3208, a transport protocol \
for reliable multicast over UDP, used by ZeroMQ (-DWITH_OPENPGM)."
HOMEPAGE = "https://github.com/steve-o/openpgm"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

SRC_URI = "https://github.com/steve-o/openpgm/archive/refs/tags/release-5-3-128.tar.gz;downloadfilename=${BP}.tar.gz \
           file://0001-cpu.c-guard-x86-only-cpuid-helpers-for-non-x86-target.patch \
           "
SRC_URI[sha256sum] = "8d707ef8dda45f4a7bc91016d7f2fed6a418637185d76c7ab30b306499c6d393"

S = "${WORKDIR}/openpgm-release-5-3-128/openpgm/pgm"

DEPENDS = "perl-native"

inherit autotools pkgconfig

# AC_CHECK_FILES cannot run when cross compiling; /proc/cpuinfo exists on all
# Linux targets, /dev/rtc and /dev/hpet are not relied upon (generic clock path)
CACHED_CONFIGUREVARS = "ac_cv_file__proc_cpuinfo=yes \
                        ac_cv_file__dev_rtc=no \
                        ac_cv_file__dev_hpet=no"

# configure.ac resolves the version through perl version.pl and expects an
# m4 macro directory that is not shipped in the git archive; the pc template
# is stale upstream (openpgm-5.2.pc.in) while AC_CONFIG_FILES wants 5.3
do_configure:prepend() {
    install -d ${S}/m4
    cp ${S}/openpgm-5.2.pc.in ${S}/openpgm-5.3.pc.in
}

# The installed headers use the bare `restrict` keyword, which is valid in C
# but rejected by clang++ in C++ consumers (e.g. zeromq): "redefinition of
# parameter 'restrict'". Rewrite to the portable __restrict spelling.
do_install:append() {
    sed -i -e 's/\*\s*restrict\b/*__restrict/g' \
        -e 's/\*const\s*restrict\b/*const __restrict/g' \
        ${D}${includedir}/pgm-5.3/pgm/*.h
}

BBCLASSEXTEND = "native nativesdk"
