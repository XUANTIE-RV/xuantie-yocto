SUMMARY = "Library for BPF handling"
DESCRIPTION = "Library for BPF handling"
HOMEPAGE = "https://github.com/libbpf/libbpf"
SECTION = "libs"
LICENSE = "LGPL-2.1-or-later"

LIC_FILES_CHKSUM = "file://../LICENSE.LGPL-2.1;md5=b370887980db5dd40659b50909238dbd"

DEPENDS = "zlib elfutils"

SRC_URI = "git://github.com/libbpf/libbpf.git;protocol=https;branch=master"
SRCREV = "fdf402b384cc42ce29bb9e27011633be3cbafe1e"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_HOST = "(x86_64|i.86|arm|aarch64|riscv64|powerpc|powerpc64|mips64).*-linux"

S = "${WORKDIR}/git/src"

EXTRA_OEMAKE += "DESTDIR=${D} LIBDIR=${libdir} INCLUDEDIR=${includedir}"
EXTRA_OEMAKE:append:class-native = " UAPIDIR=${includedir}"

inherit pkgconfig

do_configure() {
        cd ${WORKDIR}/git/
        git checkout v1.4.2
}
do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install
}

do_install:append:class-native() {
	oe_runmake install_uapi_headers
}

BBCLASSEXTEND = "native nativesdk"