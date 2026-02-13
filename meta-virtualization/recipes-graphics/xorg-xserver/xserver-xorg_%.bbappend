require ${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${BPN}_xen.inc', '', d)}

