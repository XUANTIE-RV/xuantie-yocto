# stash_locale_cleanup removes the empty /usr/lib after stashing the locale
# files to avoid remnant clutter, but we need it in the sysroot
stash_locale_sysroot_cleanup:append() {
	# If the usr/lib directory doesn't exist, the toolchain fails to even
	# try to find crti.o in a completely different directory (usr/lib64)
	install -d ${SYSROOT_DESTDIR}${nonarch_libdir}
}
