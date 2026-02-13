SUMMARY = "Testfloat3"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=a3ad53679af913f5ae9101873c207540"

inherit pkgconfig autotools
inherit xuantie-set-package-arch-with-mcpu

SRC_URI = "git://github.com/ucb-bar/berkeley-testfloat-3.git;protocol=https;branch=master; \
           file://0001-testfloat-riscv-makefile.patch "
SRCREV = "a9c849f1b0eb0264b626d9686ffae167d996e3be"

do_compile[depends] += " softfloat:do_patch softfloat:do_compile "

S = "${WORKDIR}/git"
# Extend cpu configuration for different xuantie cpu
python () {
    if d.getVar('XUANTIE_TUNE_CCARGS_USE_MCPU') != 'NO':
        d.setVar('TUNE_CCARGS', d.getVar('TUNE_CCARGS') + " -mcpu=" + d.getVar('XUANTIE_MCPU'))
}

EXTRA_OEMAKE = " -C ${S}/build/Linux-RISCV64-GCC CC="${CC}" WORKDIR="${WORKDIR}" "

do_install() {
    install -d ${D}/usr/bin
    cp ${S}/build/Linux-RISCV64-GCC/testfloat ${D}/usr/bin/
}
