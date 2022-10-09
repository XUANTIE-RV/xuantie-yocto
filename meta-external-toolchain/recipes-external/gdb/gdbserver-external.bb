SUMMARY = "gdbserver is a program that allows you to run GDB on a different machine than the one which is running the program being debugged"
HOMEPAGE = "http://www.gnu.org/software/gdb/"
SECTION = "devel"
PV := "${@external_run(d, 'gdb', '-v').splitlines()[0].split()[-1]}"

inherit external-toolchain

def get_gdb_license(d):
    output = external_run(d, 'gdb', '-v')
    if output != 'UNKNOWN':
        for line in output.splitlines():
            if line.startswith('License '):
                lic = line.split(':', 1)[0]
                return lic.replace('License ', '')
    else:
        return output

LICENSE := "${@get_gdb_license(d)}"
LICENSE[vardepvalue] = "${LICENSE}"

FILES_${PN} = "${bindir}/gdbserver"
FILES_${PN}-doc = "${mandir}/man1/gdbserver.1"

# Ensure that our rdeps are able to be set by shlibs processing
do_package[depends] += "\
    virtual/libc:do_packagedata \
    virtual/${MLPREFIX}${TARGET_PREFIX}compilerlibs:do_packagedata \
"
