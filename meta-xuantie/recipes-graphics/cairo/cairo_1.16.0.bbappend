
do_compile:append:class-target () {
    ${B}/perf/cairo-perf-micro || ls ${B}/perf/.libs/lt-cairo-perf-micro
    ${B}/perf/cairo-perf-trace || ls ${B}/perf/.libs/lt-cairo-perf-trace
    ${B}/perf/cairo-analyse-trace || ls ${B}/perf/.libs/lt-cairo-analyse-trace
}

do_install:append:class-target () {
    install -d ${D}${bindir}/.libs

    install -m 0755 ${B}/perf/cairo-perf-micro ${D}${bindir}
    install -m 0755 ${B}/perf/.libs/lt-cairo-perf-micro ${D}${bindir}/.libs
    install -m 0755 ${B}/perf/cairo-perf-trace ${D}${bindir}
    install -m 0755 ${B}/perf/.libs/lt-cairo-perf-trace ${D}${bindir}/.libs
    install -m 0755 ${B}/perf/cairo-analyse-trace ${D}${bindir}
    install -m 0755 ${B}/perf/.libs/lt-cairo-analyse-trace ${D}${bindir}/.libs
}

RDEPENDS:${PN}-perf-utils:append:class-target = " bash"
FILES:${PN}-perf-utils:append:class-target = " \
    ${bindir}/cairo-perf-micro \
    ${bindir}/cairo-perf-trace \
    ${bindir}/cairo-analyse-trace \
    ${bindir}/.libs \
"
DEBIAN_NOAUTONAME:${PN}-perf-utils:class-target = "1"
