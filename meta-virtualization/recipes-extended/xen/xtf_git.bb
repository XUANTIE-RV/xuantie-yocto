SUMMARY = "Xen Test Framework"
HOMEPAGE = "https://xenbits.xenproject.org/docs/xtf/"
LICENSE = "BSD-2-Clause"

# For additional reference on XTF, please see:
# https://static.sched.com/hosted_files/xendeveloperanddesignsummit2017/79/xtf.pdf

SRC_URI = "git://xenbits.xen.org/xtf;branch=master"
SRCREV = "3e800027016ea4eb19887bf626b46f45fc43fa5d"

COMPATIBLE_HOST = '(x86_64.*).*-linux'

LIC_FILES_CHKSUM = "file://COPYING;md5=a5680865974e05cf0510615ee1d745d8"

PV = "0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit python3native

PACKAGES = "${PN}"

FILES:${PN} = " \
    ${libexecdir}/* \
    "

RDEPENDS:${PN} = " \
    xen-tools-xl \
    python3 \
    "

do_compile() {
    oe_runmake CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}" \
               CPP="${CPP}" \
               LD="${LD}" \
               OBJCOPY="${OBJCOPY}" \
               PYTHON="${PYTHON}"
    # switch the shebang to python3
    sed 's,^\(#!/usr/bin/env python\)$,\13,' -i "${B}/xtf-runner"
}

do_install() {
    # packaging: rpmbuild can package the XTF test unikernels when they are
    # installed as non-executable files (they are run within VMs anyway).
    oe_runmake install DESTDIR="${D}" \
                       xtfdir="${libexecdir}/${BPN}" \
                       PYTHON="${PYTHON}" \
                       INSTALL_PROGRAM="install -m 644 -p"
    install -m 755 -p "${B}/xtf-runner" "${D}${libexecdir}/${BPN}/xtf-runner"
}

INSANE_SKIP = "arch"
# xen-tools-xl is a runtime but not build time dependency
INSANE_SKIP:${PN} = "build-deps"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
