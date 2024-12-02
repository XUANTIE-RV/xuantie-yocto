do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/security/limits.d
        echo '* hard core unlimited' >> ${D}${sysconfdir}/security/limits.d/01-ulimit.conf
        echo '* soft core unlimited' >> ${D}${sysconfdir}/security/limits.d/01-ulimit.conf
    fi
}
