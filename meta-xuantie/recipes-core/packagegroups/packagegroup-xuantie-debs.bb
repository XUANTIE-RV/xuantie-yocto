DESCRIPTION = "This packagegroup includes extend debs."
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} += " python3 python3-numpy python3-pip python3-psutil python3-ruamel-yaml python3-pytest python3-lxml "

RDEPENDS:${PN} += " packagegroup-xuantie-test packagegroup-xuantie-debug "
