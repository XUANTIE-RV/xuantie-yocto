DESCRIPTION = "HaaS UI Demo git src"
LICENSE = "CLOSED"

SRCBRANCH ?= "master"
# SRCBRANCH ?= "pull_request"

COMPATIBLE_MACHINE = "(^d1.*)"

SRC_URI = " \
            git://git@gitlab.alibaba-inc.com/thead-os-platform/miniapp_falcon.git;branch=${SRCBRANCH};protocol=ssh \
          "

SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"
