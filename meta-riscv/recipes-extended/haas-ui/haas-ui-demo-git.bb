DESCRIPTION = "HaaS UI Demo git src"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "(^d1.*)"

SRC_URI = " \
            git://git@gitlab.alibaba-inc.com/thead-os-platform/haas-ui-demo.git;protocol=ssh \
          "

SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"
