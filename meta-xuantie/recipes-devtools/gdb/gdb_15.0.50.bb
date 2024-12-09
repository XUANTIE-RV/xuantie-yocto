LICENSE = "CLOSED"

inherit python3-dir autotools texinfo
SECTION = "devel"
DEPENDS = "expat mpfr zlib ncurses virtual/libiconv bison-native"

XUANTIE_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "f026a7be7a830fac9eb70a541399a51c90514dff"

XUANTIE_GIT = "git://git@gitee.com/xuantie-yocto/gdb-xuantie-private.git"
XUANTIE_GIT_MIRRORS = "git://git@github.com/XUANTIE-RV/gdb-xuantie-private.git"
MIRRORS += "${XUANTIE_GIT} ${XUANTIE_GIT_MIRRORS}"

SRC_URI = " \
    ${XUANTIE_GIT_MIRRORS};branch=1.0;protocol=https \
    ${XUANTIE_GIT};branch=1.0;protocol=https \
"

S:riscv32 = "${WORKDIR}/git/gdb_prebuilt/gdb-xuantie-private_15.0.50-r0_riscv32/usr"
S:riscv64 = "${WORKDIR}/git/gdb_prebuilt/gdb-xuantie-private_15.0.50-r0_riscv64/usr"

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
