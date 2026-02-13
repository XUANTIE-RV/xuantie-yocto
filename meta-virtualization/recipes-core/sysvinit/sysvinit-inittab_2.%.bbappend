require ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'sysvinit-inittab_xen.inc', '', d)}
require ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'sysvinit-inittab_virtualization.inc', '', d)}
