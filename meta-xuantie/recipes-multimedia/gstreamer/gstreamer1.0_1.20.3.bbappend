PACKAGECONFIG:append:pn-gstreamer1.0 = " tracer-hooks coretracers "
RDEPENDS:${PN}-ptest:append:libc-glibc = " glibc-gconv-iso8859-5"
PR = "r1"
