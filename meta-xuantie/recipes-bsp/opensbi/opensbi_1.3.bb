require opensbi.inc

XUANTIE_GIT = "git://git@gitee.com/xuantie-yocto/opensbi.git"
XUANTIE_GIT_MIRRORS = "git://git@github.com/XUANTIE-RV/opensbi.git"
MIRRORS += "${XUANTIE_GIT} ${XUANTIE_GIT_MIRRORS}"
SRC_URI = " \
    ${XUANTIE_GIT_MIRRORS};branch=master;protocol=https \
    ${XUANTIE_GIT};branch=master;protocol=https \
"

SRC_URI = " \
            ${XUANTIE_GIT_MIRRORS};branch=linux-6.6;protocol=https \
            ${XUANTIE_GIT};branch=linux-6.6;protocol=https \
          "

# tag v1.3
SRCREV = "a9994daf2474e51df3c8ec83065ca556180eb203"
