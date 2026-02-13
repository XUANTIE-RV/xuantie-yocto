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

    # Modify /dev/pts mount gid=5 and change the permissions to be 0666
    sed -i 's|::sysinit:/bin/mount -t devpts devpts /dev/pts|::sysinit:/bin/mount -t devpts -o gid=5,mode=0620,ptmxmode=0666 devpts /dev/pts|' ${D}${sysconfdir}/inittab
}

FILES:${PN} += "${bindir}/check-mount-dev.sh"
