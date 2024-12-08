require opensbi.inc

XUANTIE_GIT = "git://git@gitee.com/xuantie-yocto/opensbi.git"
XUANTIE_GIT_MIRRORS = "git://git@github.com/XUANTIE-RV/opensbi.git"
MIRRORS += "${XUANTIE_GIT} ${XUANTIE_GIT_MIRRORS}"

SRC_URI = " \
            ${XUANTIE_GIT_MIRRORS};branch=opensbi-v0.9-dev;protocol=https \
            ${XUANTIE_GIT};branch=opensbi-v0.9-dev;protocol=https \
            file://0001-Makefile-tmp-fix.patch \
          "

# Mon Sep 23 22:30:13 2024 +0800
# dts:compatible: Add thead,c900-clint & thead,c900-plic
SRCREV = "cc19d214ac870c07c2dba39051a3625d83d24032"
