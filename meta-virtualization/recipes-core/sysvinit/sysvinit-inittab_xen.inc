

do_install:append() {
    # if SERIAL_CONSOLES contains hvc0, it is already added in inittab so do
    # not add it twice
    if echo "${SERIAL_CONSOLES}" | grep -vq "hvc0"; then
        echo "" >> ${D}${sysconfdir}/inittab
        echo "X0:12345:respawn:${base_sbindir}/getty-wrapper 115200 hvc0" >> ${D}${sysconfdir}/inittab
    fi
}
