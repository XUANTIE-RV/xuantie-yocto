# stash_locale_cleanup removes the empty /usr/lib after stashing the locale
# files to avoid remnant clutter, but we need it in the sysroot

SYSROOT_PREPROCESS_FUNCS += "external_toolchain_sysroot_adjust"
external_toolchain_sysroot_adjust() {
	# If the usr/lib directory doesn't exist, the toolchain fails to even
	# try to find crti.o in a completely different directory (usr/lib64)
	install -d ${SYSROOT_DESTDIR}${nonarch_libdir}

	install -d ${SYSROOT_DESTDIR}/lib64xthead/
	ln -s ../lib ${SYSROOT_DESTDIR}/lib64xthead/lp64d

	install -d ${SYSROOT_DESTDIR}/usr/lib64xthead/
	ln -s ../lib ${SYSROOT_DESTDIR}/usr/lib64xthead/lp64d
}
