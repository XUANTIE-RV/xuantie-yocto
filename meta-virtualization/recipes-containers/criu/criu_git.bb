SUMMARY = "CRIU"
DESCRIPTION = "Checkpoint/Restore In Userspace, or CRIU, is a software tool for \
Linux operating system. Using this tool, you can freeze a running application \
(or part of it) and checkpoint it to a hard drive as a collection of files. \
You can then use the files to restore and run the application from the point \
it was frozen at. The distinctive feature of the CRIU project is that it is \
mainly implemented in user space"
HOMEPAGE = "http://criu.org"
SECTION = "console/tools"
LICENSE = "GPL-2.0-only"

EXCLUDE_FROM_WORLD = "1"

LIC_FILES_CHKSUM = "file://COPYING;md5=412de458544c1cb6a2b512cd399286e2"

SRCREV = "d46f40f4ff0c724e0b9f0f8a2e8c043806897e94"
PV = "3.17.1+git${SRCPV}"

SRC_URI = "git://github.com/checkpoint-restore/criu.git;branch=master;protocol=https \
           file://0001-criu-Skip-documentation-install.patch \
           file://0002-criu-Change-libraries-install-directory.patch \
           file://0003-lib-Makefile-overwrite-install-lib-to-allow-multiarc.patch \
           file://0004-criu-fix-conflicting-headers.patch \
           file://0005-mount-add-definition-for-FSOPEN_CLOEXEC.patch \
           "

COMPATIBLE_HOST = "(x86_64|arm|aarch64).*-linux"

DEPENDS += "libnl libcap protobuf-c-native protobuf-c util-linux-native libbsd libnet"
RDEPENDS:${PN} = "bash"

S = "${WORKDIR}/git"

#
# CRIU just can be built on ARMv7 and ARMv6, so the Makefile check
# if the ARCH is ARMv7 or ARMv6.
# ARM BSPs need set CRIU_BUILD_ARCH variable for building CRIU.
#
EXTRA_OEMAKE:arm += "ARCH=arm UNAME-M=${CRIU_BUILD_ARCH} WERROR=0"
EXTRA_OEMAKE:x86-64 += "ARCH=x86 WERROR=0"
EXTRA_OEMAKE:aarch64 += "ARCH=aarch64 WERROR=0"

EXTRA_OEMAKE:append = " SBINDIR=${sbindir} LIBDIR=${libdir} INCLUDEDIR=${includedir} PIEGEN=no"
EXTRA_OEMAKE:append = " LOGROTATEDIR=${sysconfdir} SYSTEMDUNITDIR=${systemd_unitdir}"

CFLAGS += "-D__USE_GNU -D_GNU_SOURCE " 

CFLAGS += " -I${STAGING_INCDIR} -I${STAGING_INCDIR}/libnl3"
CFLAGS:arm += "-D__WORDSIZE"

# overide LDFLAGS to allow criu to build without: "x86_64-poky-linux-ld: unrecognized option '-Wl,-O1'"
export LDFLAGS=""
export C_INCLUDE_PATH="${STAGING_INCDIR}/libnl3"

export BUILD_SYS
export HOST_SYS
export HOSTCFLAGS = "${BUILD_CFLAGS}"

inherit setuptools3
inherit pkgconfig

B = "${S}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[selinux] = ",,libselinux"

CLEANBROKEN = "1"

do_compile:prepend() {
    rm -rf ${S}/images/google/protobuf/descriptor.proto
    ln -s  ${PKG_CONFIG_SYSROOT_DIR}/usr/include/google/protobuf/descriptor.proto ${S}/images/google/protobuf/descriptor.proto
}

do_compile () {
	oe_runmake FULL_PYTHON=${PYTHON} PYTHON=python3
}

do_install () {
    export INSTALL_LIB="${libdir}/${PYTHON_DIR}/site-packages"
    oe_runmake PREFIX=${exec_prefix} LIBDIR=${libdir} DESTDIR="${D}" PLUGINDIR="${localstatedir}/lib" FULL_PYTHON=${PYTHON} PYTHON=python3 install

    # python3's distutils has a feature of rewriting the interpeter on setup installed
    # scripts. 'crit' is one of those scripts. The "executable" or "e" option to the
    # setup call should fix it, but it is being ignored. So to avoid getting our native
    # intepreter replaced in the script, we'll do an explicit update ourselves.
    sed -i 's%^\#\!.*%\#\!/usr/bin/env python3%' ${D}/usr/bin/crit ${D}${libdir}/python3*/site-packages/crit-*-py3*.egg/EGG-INFO/scripts/crit

    rm -rf ${D}/__pycache__
}

FILES:${PN} += "${systemd_unitdir}/ \
            ${libdir}/python3*/site-packages/ \
            ${libdir}/pycriu/ \
            ${libdir}/crit-0.0.1-py3*.egg-info \
            "

FILES:${PN}-staticdev += " \
            ${libexecdir}/compel/std.lib.a \
            ${libexecdir}/compel/fds.lib.a \
            "
