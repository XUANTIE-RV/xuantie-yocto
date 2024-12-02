SUMMARY = "Valgrind memory debugger and instrumentation framework"
HOMEPAGE = "http://valgrind.org/"
DESCRIPTION = "Valgrind is an instrumentation framework for building dynamic analysis tools. There are Valgrind tools that can automatically detect many memory management and threading bugs, and profile your programs in detail."
BUGTRACKER = "http://valgrind.org/support/bug_reports.html"
LICENSE = "CLOSED"
PV = "3.1.0"

SRC_URI = "ftp://swftp01.damo.xrvm.cn/Test/Test/valgrind/v3.1.0/valgrind-3.21.0.GIT.Xuantie-3.1.0-c908v.deb;subdir=${BP}"
SRC_URI[sha256sum] = "d68f9823a3e889b7947c1376a322e47ff2b38cf6e8f8f23f8900d999caaf585f"

INSANE_SKIP:${PN} += "file-rdeps dev-so staticdev already-stripped installed-vs-shipped"
inherit xuantie-set-package-arch-with-mcpu
do_configure[noexec] = "1"
do_compile[noexec] = "1"
FILES:${PN} += "\
      * \
"
PACKAGES = "${PN}"
do_install () {
    cp -r ${B}/* ${D}
}

VALGRINDARCH ?= "${TARGET_ARCH}"

INHIBIT_PACKAGE_STRIP_FILES = "${PKGD}${libexecdir}/valgrind/vgpreload_memcheck-${VALGRINDARCH}-linux.so"

RDEPENDS:${PN} += "perl"

# valgrind needs debug information for ld.so at runtime in order to
# redirect functions like strlen.
RRECOMMENDS:${PN} += "${TCLIBC}-dbg"
