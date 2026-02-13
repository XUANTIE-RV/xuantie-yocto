do_install:append() {
    cat >> ${D}${sysconfdir}/ntp.conf << EOF
server 0.pool.ntp.org iburst
server 1.pool.ntp.org iburst
server 2.pool.ntp.org iburst
server 3.pool.ntp.org iburst
EOF
}
