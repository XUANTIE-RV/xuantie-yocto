require opensbi.inc

SRC_URI = " \
    ${@bb.utils.contains('MACHINECONFIG', 'public', \
    '${XUANTIE_GIT}/opensbi.git;branch=opensbi-v1.7-dev;protocol=https', \
    'git://github.com/XUANTIE-RV/opensbi.git;branch=opensbi-v1.7-dev;protocol=https', d)} \
"

SRCREV = "ce6ec8f498b8f8f4e456d5ce8877360d7b0202a6"
