do_install:append() {
    install -d ${D}${bindir}
    cat > ${D}${bindir}/check-mount-dev.sh << EOF
#!/bin/sh

[ -e /dev/null ] && exit 0

/bin/mount -t devtmpfs devtmpfs /dev
exit 0
EOF
    chmod +x ${D}${bindir}/check-mount-dev.sh
    sed -i 's|::sysinit:/bin/mount -t devtmpfs devtmpfs /dev|::sysinit:${bindir}/check-mount-dev.sh|' ${D}${sysconfdir}/inittab
}

FILES:${PN} += "${bindir}/check-mount-dev.sh"
