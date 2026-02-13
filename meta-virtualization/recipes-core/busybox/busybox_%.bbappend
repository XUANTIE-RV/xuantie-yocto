require ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${BPN}_virtualization.inc', '', d)}
require ${@bb.utils.contains('DISTRO_FEATURES', 'vmsep', '${BPN}_vmsep.inc', '', d)}

