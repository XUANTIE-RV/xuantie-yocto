SUMMARY = "GNU binary utilities"
HOMEPAGE = "http://www.gnu.org/software/binutils/"
BUGTRACKER = "http://sourceware.org/bugzilla/"
SECTION = "devel"
PV := "${@external_run(d, 'ld', '-v').splitlines()[0].split()[-1].rstrip()}"
LICENSE = "${@'GPL-3.0-only' if '${PV}'.split('.') > '2.17.50.0.12'.split('.') else 'GPL-2.0-only'}"

binutils_binaries = "ar as ld ld.bfd ld.gold nm objcopy objdump ranlib strip \
                     addr2line c++filt elfedit gprof readelf size \
                     strings"
