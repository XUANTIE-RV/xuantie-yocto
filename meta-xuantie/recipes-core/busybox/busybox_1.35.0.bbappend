FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://basic.cfg \
    file://debug.cfg \
    file://file_system.cfg \
    file://misc.cfg \
    file://network.cfg \
    "

do_install:append() {
    if grep -q "CONFIG_MDEV=y" ${B}/.config; then
        sed -i '/echo "OK"/a \ \ exit 0' ${D}${sysconfdir}/init.d/mdev
    fi
    if grep -q "CONFIG_INIT=y" ${B}/.config && ${@bb.utils.contains('VIRTUAL-RUNTIME_init_manager','busybox','true','false',d)}; then
        sed -i '/\[ ! -f "\$i" \] && continue/a \ \ \ \ echo "Starting service $i"' ${D}${sysconfdir}/init.d/rcS
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        echo '/sbin/sysctl -e -w kernel.core_pattern=core.%e.%p kernel.core_uses_pid=1 >/dev/null' >> ${D}${sysconfdir}/init.d/rcS
    fi
}
