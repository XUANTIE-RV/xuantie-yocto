SUMMARY = "System-Wide Profiler"
DESCRIPTION = "OProfile is a system-wide profiler for Linux systems, capable \
of profiling all running code at low overhead."
HOMEPAGE = "http://oprofile.sourceforge.net/news/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=16191&atid=116191"
SECTION = "devel"

inherit external-toolchain

FILES_${PN} = "${bindir}/op* ${datadir}/oprofile"
FILES_${PN}-doc = "${docdir}/oprofile ${mandir}/man1/op*"
FILES_${PN}-staticdev = "${libdir}/oprofile/*.a"
