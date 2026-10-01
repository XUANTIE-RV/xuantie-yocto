require opensbi.inc

SRC_URI = " \
    ${@bb.utils.contains('MACHINECONFIG', 'public', \
    '${XUANTIE_GIT}/opensbi.git;branch=opensbi-v1.7-dev;protocol=https', \
    'git://github.com/XUANTIE-RV/opensbi.git;branch=opensbi-v1.7-dev;protocol=https', d)} \
"

XUANTIE_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "${XUANTIE_LINUX_TAG}"
