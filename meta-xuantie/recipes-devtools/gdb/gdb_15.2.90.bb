LICENSE = "CLOSED"

inherit python3-dir autotools texinfo
SECTION = "devel"
DEPENDS = "expat mpfr zlib ncurses virtual/libiconv bison-native"

SRC_URI = "\
    ${@bb.utils.contains('MACHINECONFIG', 'public', \
    '${XUANTIE_GIT}/gdb-xuantie-private.git;nobranch=1;protocol=https', \
    'git://gerrit.damo.xrvm.cn:29518/xuantie-linux/gdb-xuantie-private.git;branch=dev-cpu;protocol=ssh', d)} \
"
XUANTIE_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "${XUANTIE_LINUX_TAG}"

S:riscv32 = "${WORKDIR}/git/gdb_prebuilt/gdb-xuantie-private_15.0.50-r0_riscv32/usr"
S:riscv64 = "${WORKDIR}/git/gdb_prebuilt/gdb-xuantie-private_15.0.50-r0_riscv64/usr"
INSANE_SKIP:${PN} = "already-stripped"
do_compile() {
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${datadir}

    cp -rf ${S}/bin/*                ${D}${bindir}
    cp -rf ${S}/share/*              ${D}${datadir}
}

RRECOMMENDS:gdb:append:linux = " glibc-thread-db "
RRECOMMENDS:gdb:append:linux-gnueabi = " glibc-thread-db "
RRECOMMENDS:gdbserver:append:linux = " glibc-thread-db "
RRECOMMENDS:gdbserver:append:linux-gnueabi = " glibc-thread-db "

RDEPENDS:${PN}:libc-glibc = " \
    glibc-gconv-utf-32 \
"
