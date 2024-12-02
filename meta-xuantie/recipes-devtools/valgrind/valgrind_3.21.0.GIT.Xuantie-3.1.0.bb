SUMMARY = "Valgrind memory debugger and instrumentation framework"
HOMEPAGE = "http://valgrind.org/"
DESCRIPTION = "Valgrind is an instrumentation framework for building dynamic analysis tools. There are Valgrind tools that can automatically detect many memory management and threading bugs, and profile your programs in detail."
BUGTRACKER = "http://valgrind.org/support/bug_reports.html"
LICENSE = "CLOSED"


SRC_URI = "ftp://11.163.23.134/Release/Valgrind/V3.1.0/valgrind-3.21.0.GIT.Xuantie-3.1.0.deb;subdir=${BP}"
SRC_URI[sha256sum] = "0d285e76e9e0777a18fdb5dc78fe5f892c0b3881828d8ab67056ef498b61be98"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
    cp -r ${B}/* ${D}
}

VALGRINDARCH ?= "${TARGET_ARCH}"

INHIBIT_PACKAGE_STRIP_FILES = "${PKGD}${libexecdir}/valgrind/vgpreload_memcheck-${VALGRINDARCH}-linux.so"

RDEPENDS:${PN} += "perl"

# valgrind needs debug information for ld.so at runtime in order to
# redirect functions like strlen.
RRECOMMENDS:${PN} += "${TCLIBC}-dbg"
