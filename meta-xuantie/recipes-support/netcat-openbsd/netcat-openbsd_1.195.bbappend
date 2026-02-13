pkg_postinst:${PN}() {
    # register alternative between netcat-openbsd and netcat
    update-alternatives --install ${bindir}/netcat netcat ${bindir}/nc 100
}

pkg_prerm:${PN}() {
    # replace netcat with nc: rootfs/usr/bin/netcat -> /usr/bin/nc
    update-alternatives --remove netcat ${bindir}/nc
}
