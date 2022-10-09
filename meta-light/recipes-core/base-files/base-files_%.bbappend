FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

do_install_append_d1() {
    echo "" >> ${D}${sysconfdir}/fstab
    echo "tmpfs                /tmp                 tmpfs      defaults              0  0" >> ${D}${sysconfdir}/fstab
    echo "tmpfs                /var/lib             tmpfs      defaults              0  0" >> ${D}${sysconfdir}/fstab

    echo "[ -x /bin/ll ] || alias ll='ls -al'" >> ${D}${sysconfdir}/profile
    echo "[ -x /usr/bin/vim ] && alias vi=vim || alias vim=vi" >> ${D}${sysconfdir}/profile
    echo "export SK_USE_G2D=1" >> ${D}${sysconfdir}/profile
}