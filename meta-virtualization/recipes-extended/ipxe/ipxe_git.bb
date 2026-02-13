DESCRIPTION = "Open source network boot firmware"
HOMEPAGE = "http://ipxe.org"
LICENSE = "GPL-2.0-only"
DEPENDS = "binutils-native perl-native syslinux mtools-native cdrtools-native xz"
LIC_FILES_CHKSUM = "file://../COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263"

# syslinux has this restriction
COMPATIBLE_HOST:class-target = '(x86_64|i.86).*-(linux|freebsd.*)'

SRCREV = "18dc73d27edb55ebe9cb13c58d59af3da3bd374b"
PV = "1.20.1+git${SRCPV}"
PR = "r0"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = " \
    git://github.com/ipxe/ipxe.git;protocol=https;branch=master \
    file://ipxe-fix-hostcc-nopie-cflags.patch \
    file://ipxe-intel-Avoid-spurious-compiler-warning-on-GCC-10.patch \
    file://ipxe-golan-Add-explicit-type-casts-for-nodnic_queue_pair_.patch \
    file://build-be-explicit-about-fcommon-compiler-directive.patch \
    "

FILES:${PN} = "/usr/share/firmware/*.rom"

EXTRA_OEMAKE = ' \
    ISOLINUX_BIN="${STAGING_DIR_TARGET}/usr/share/syslinux/isolinux.bin" \
    CROSS_COMPILE="${TARGET_PREFIX}" \
    EXTRA_HOST_CFLAGS="${BUILD_CFLAGS}" \
    EXTRA_HOST_LDFLAGS="${BUILD_LDFLAGS}" \
    NO_WERROR="1" \
'

S = "${WORKDIR}/git/src"

do_compile() {
    # Makefile.housekeeping:111: GNU gold is unsuitable for building iPXE
    # Makefile.housekeeping:112: Use GNU ld instead
    sed -i 's#\(^LD.*$(CROSS_COMPILE)ld\)$#\1.bfd#g' -i ${S}/Makefile
    oe_runmake
}

do_install() {
    install -d ${D}/usr/share/firmware
    install ${S}/bin/*.rom ${D}/usr/share/firmware/
}

TOOLCHAIN = "gcc"
