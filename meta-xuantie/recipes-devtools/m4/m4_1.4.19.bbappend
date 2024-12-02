
FILES:${PN}:append:libc-musl = "${libdir}/charset.alias"
INSANE_SKIP:${PN}:libc-musl = "installed-vs-shipped"
