SUMMARY = "GNU cc and gcc C compilers"
HOMEPAGE = "http://gcc.org/"
DESCRIPTION = "GNU cc and gcc C compilers."
LICENSE = "CLOSED"

XUANTIE_GCC_SRC_URI_PATH ?= "ftp://11.122.129.163/Test/Test/Toolschain/gnu-riscv/V3.1.0"
SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${XUANTIE_GCC_SRC_URI_PATH}/gcc_14.1.1_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=9882dfecd9817d1bb1f903480ede6d29033d6f4ba60a6bb865fad6ab1bce86e8', \
    '${XUANTIE_GCC_SRC_URI_PATH}/gcc_14.1.1_riscv64-6.6.0-rv64gc-lp64d.deb;sha256sum=51a24c5a7981e25897112b08b27777fd31693b747655d3cb2c80485c6c732477', d)}"
SRC_URI:riscv32 = "${XUANTIE_GCC_SRC_URI_PATH}/gcc_14.1.1_riscv32-6.6.0-rv32gc-ilp32d.deb;sha256sum=808823570c32d4078d18164d5427a2201482b8342c5059bfc4b5e9772a5d7ddc"

PR = "r3"

INSANE_SKIP:${PN} += "file-rdeps dev-deps infodir libdir"
INSANE_SKIP:${PN} += "dev-so staticdev"
INSANE_SKIP:${PN} += "already-stripped"
INSANE_SKIP:${PN} += "installed-vs-shipped"
INSANE_SKIP:gcclibs += "staticdev"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

FILES:${PN} += "\
    usr/bin/* \
    usr/libexec/* \
    usr/lib/bfd-plugins/* \
    usr/riscv64-unknown-linux-gnu/bin/* \
    usr/riscv64-unknown-linux-gnu/lib/* \
    usr/share/* \ 
"
FILES:gcclibs += "\
    usr/lib/gcc/* \
"
FILES:c++headers += "\
    usr/include/* \
"

PACKAGES = "gcclibs ${PN} c++headers"
do_install () {
    cp -rfd ${WORKDIR}/usr ${D}
    rm -rf ${D}/usr/riscv64-unknown-linux-gnu/lib/libstdc++.so*
    rm -rf ${D}/usr/riscv64-unknown-linux-gnu/lib/libgcc_s.so.1
    rm -rf ${D}/usr/bin/addr2line
    rm -rf ${D}/usr/bin/ar
    rm -rf ${D}/usr/bin/as
    rm -rf ${D}/usr/bin/elfedit
    rm -rf ${D}/usr/bin/gprof
    rm -rf ${D}/usr/bin/ld
    rm -rf ${D}/usr/bin/ld.bfd
    rm -rf ${D}/usr/bin/nm
    rm -rf ${D}/usr/bin/objcopy
    rm -rf ${D}/usr/bin/objdump
    rm -rf ${D}/usr/bin/ranlib
    rm -rf ${D}/usr/bin/readelf
    rm -rf ${D}/usr/bin/size
    rm -rf ${D}/usr/bin/strings
    rm -rf ${D}/usr/bin/strip
}

RDEPENDS:${PN} += "libstdc++ libstdc++-dev c++headers gcclibs binutils libc6-dev"
