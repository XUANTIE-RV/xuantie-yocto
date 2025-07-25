SUMMARY = "Softfloat3"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=c3d18aacd7e005685ca22e5e14e42a85"

inherit pkgconfig autotools

SRC_URI = "git://github.com/ucb-bar/berkeley-softfloat-3.git;protocol=https;branch=master; \
           file://0001-softfloat-riscv-makefile.patch "
SRCREV = "a0c6494cdc11865811dec815d5c0049fba9d82a8"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = " -C ${S}/build/Linux-RISCV64-GCC CC="${CC}""

do_install() {
    install -d ${D}/usr/lib
    cp ${S}/build/Linux-RISCV64-GCC/softfloat.a ${D}/usr/lib
}
