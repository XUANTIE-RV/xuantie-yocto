SUMMARY = "System-Wide Profiler"
DESCRIPTION = "OProfile is a system-wide profiler for Linux systems, capable \
of profiling all running code at low overhead."
HOMEPAGE = "http://oprofile.sourceforge.net/news/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=16191&atid=116191"
SECTION = "devel"

inherit external-toolchain

FILES:${PN} = "${bindir}/op* ${datadir}/oprofile"
FILES:${PN}-doc = "${docdir}/oprofile ${mandir}/man1/op*"
FILES:${PN}-staticdev = "${libdir}/oprofile/*.a"
