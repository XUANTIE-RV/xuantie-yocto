SUMMARY = "GNU cc and gcc C compilers"
HOMEPAGE = "http://gcc.org/"
DESCRIPTION = "GNU cc and gcc C compilers."
LICENSE = "CLOSED"

XUANTIE_GCC_SRC_URI_PATH ?= "ftp://11.122.129.163/Test/Test/Toolschain/gnu-riscv/V3.1.0-rc2"
SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${XUANTIE_GCC_SRC_URI_PATH}/gcc_14.1.1_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=ca26235363218e708bcb536b91e3d0e1a3eb4ce752aca3b93c84670d61a98726', \
    '${XUANTIE_GCC_SRC_URI_PATH}/gcc_14.1.1_riscv64-6.6.0-rv64gc-lp64d.deb;sha256sum=c482dd17b7f34e0638124878fa7352b149b32c0771876204a6c0ffa4fc17e824', d)}"
SRC_URI:riscv32 = "${XUANTIE_GCC_SRC_URI_PATH}/gcc_14.1.1_riscv32-6.6.0-rv32gc-ilp32d.deb;sha256sum=ec3dc0b87cc6e45f4056447a6d101e90178b2fd411d2b06443cf57db29535971"

PR = "r2"

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
