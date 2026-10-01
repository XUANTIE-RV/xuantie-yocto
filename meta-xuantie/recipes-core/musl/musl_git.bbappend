
do_install:append() {
	ln -rs ${D}${libdir}/libc.so ${D}${base_libdir}/ld-musl-${TUNE_ARCH}xthead.so.1
}

FILES:${PN} += "${base_libdir}/ld-musl-${TUNE_ARCH}xthead.so.1"
