SUMMARY = "A High Performance Message Passing Library"
DESCRIPTION = "The Open MPI Project is an open source implementation of the Message Passing Interface (MPI) specification that is developed and maintained by a consortium of academic, research, and industry partners. "
HOMEPAGE = "https://www.open-mpi.org/"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4c21215bd9a7b56c0de1dcde838cb795"

SRC_URI = " \
    git://github.com/open-mpi/ompi.git;protocol=https;branch=main \
    git://github.com/openpmix/openpmix.git;protocol=https;destsuffix=git/3rd-party/openpmix;name=openpmix;branch=master \
    git://github.com/open-mpi/prrte.git;protocol=https;destsuffix=git/3rd-party/prrte;name=prrte;branch=master \
    git://github.com/open-mpi/oac.git;protocol=https;destsuffix=git/config/oac;name=oac;branch=main \
    git://github.com/open-mpi/oac.git;protocol=https;destsuffix=git/3rd-party/openpmix/config/oac;name=oac;branch=main \
    git://github.com/open-mpi/oac.git;protocol=https;destsuffix=git/3rd-party/prrte/config/oac;name=oac;branch=main \
"

SRCREV_FORMAT = "ompi_openpmix_prrte_oac"
SRCREV = "1fcf4db359d27a4960fc518ddcf5710a6ae7d4c4"
SRCREV_openpmix = "e32e0179bc6bd1637f92690511ce6091719fa046"
SRCREV_prrte = "1d867e84981077bffda9ad9d44ff415a3f6d91c4"
SRCREV_oac = "dfff67569fb72dbf8d73a1dcf74d091dad93f71b"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGECONFIG ??= "libevent binaries per-user-config-files mpi-interface-warning oshmem-compat oshmem-profile wrapper-rpath nonglobal-dlopen \
                    prte-prefix-by-default uct-version-check"


# libraries to build in addition to avutil
PACKAGECONFIG[coverage] = "--enable-coverage,--disable-coverage"
PACKAGECONFIG[branch-probabilities] = "--with-branch-probabilities,--disable-branch-probabilities"
PACKAGECONFIG[mem-debug] = "--enable-mem-debug,--disable-mem-debug "
PACKAGECONFIG[mem-profile] = "--enable-mem-profile, --disable-mem-profile"
PACKAGECONFIG[picky] = "--enable-picky, --disable-picky"
PACKAGECONFIG[heterogeneous] = "--enable-heterogeneous, --disable-heterogeneous"
PACKAGECONFIG[script-wrapper-compilers] = "--enable-script-wrapper-compilers, --disable-script-wrapper-compilers"
PACKAGECONFIG[ipv6] = "--enable-ipv6, --disable-ipv6"
PACKAGECONFIG[sparse-groups] = "--enable-sparse-groups, --disable-sparse-groups"
PACKAGECONFIG[peruse] = "--enable-peruse, --disable-peruse"
PACKAGECONFIG[mpi1-compatibility] = "--enable-mpi1-compatibility, --disable-mpi1-compatibility"
PACKAGECONFIG[grequest-extensions] = "--enable-grequest-extensions, --disable-grequest-extensions"
PACKAGECONFIG[spc] = "--enable-spc, --disable-spc"
PACKAGECONFIG[dependency-tracking] = "--enable-dependency-tracking, --disable-dependency-tracking"
PACKAGECONFIG[werror] = "--enable-werror, --disable-werror"
PACKAGECONFIG[mpi-java] = "--enable-mpi-java, --disable-mpi-java"
PACKAGECONFIG[sphinx] = "--enable-sphinx, --disable-sphinx"
PACKAGECONFIG[devel-check] = "--enable-devel-check, --disable-devel-check"
PACKAGECONFIG[pmix-timing] = "--enable-pmix-timing, --disable-pmix-timing"
PACKAGECONFIG[pmix-binaries] = "--enable-pmix-binaries, --disable-pmix-binaries"
PACKAGECONFIG[btl-portals4-flow-control] = "--enable-btl-portals4-flow-control, --disable-btl-portals4-flow-control"
PACKAGECONFIG[opal-btl-usnic-unit-tests] = "--enable-opal-btl-usnic-unit-tests, --disable-opal-btl-usnic-unit-tests"
PACKAGECONFIG[libevent] = "--with-libevent, --without-libevent,libevent"

PACKAGECONFIG[binaries] = "--enable-binaries,--disable-binaries"
PACKAGECONFIG[per-user-config-files] = "--enable-per-user-config-files,--disable-per-user-config-files"
PACKAGECONFIG[disable-getpwuid] = "--disable-getpwuid, "
PACKAGECONFIG[disable-mpi] = "--disable-mpi, "
PACKAGECONFIG[mpi-interface-warning] = "--enable-mpi-interface-warning, --disable-mpi-interface-warning"
PACKAGECONFIG[disable-io-ompio] = "--disable-io-ompio, "
PACKAGECONFIG[oshmem-compat] = "--enable-oshmem-compat, --disable-oshmem-compat"
PACKAGECONFIG[oshmem-profile] = "--enable-oshmem-profile, --disable-oshmem-profile"

PACKAGECONFIG[builtin-atomics-for-ppc] = "--enable-builtin-atomics-for-ppc, --disable-builtin-atomics-for-ppc"
PACKAGECONFIG[wrapper-rpath] = "--enable-wrapper-rpath, --disable-wrapper-rpath"
PACKAGECONFIG[wrapper-runpath] = "--enable-wrapper-runpath, --disable-wrapper-runpath"
PACKAGECONFIG[memory-sanitizers] = "--memory-sanitizers, "
PACKAGECONFIG[python-bindings] = "--enable-python-bindings, --disable-python-bindings"
PACKAGECONFIG[nonglobal-dlopen] = "--enable-nonglobal-dlopen, --disable-nonglobal-dlopen"

PACKAGECONFIG[dummy-handshake] = "--enable-dummy-handshake,--disable-dummy-handshake"
PACKAGECONFIG[disable-hwloc-lib-checks] = "--disable-hwloc-lib-checks, "
PACKAGECONFIG[disable-libev-lib-checks] = "--disable-libev-lib-checks, "
PACKAGECONFIG[disable-libevent-lib-checks] = "--disable-libevent-lib-checks, "
PACKAGECONFIG[disable-pmix-dlopen] = "--disable-pmix-dlopen, "
PACKAGECONFIG[show-load-errors-by-default] = "--enable-show-load-errors-by-default, --disable-show-load-errors-by-default"
PACKAGECONFIG[disable-pmix-lib-checks] = "--disable-pmix-lib-checks, "
PACKAGECONFIG[disable-prte-dlopen] = "--disable-prte-dlopen, "

PACKAGECONFIG[disable-mpich-support] = "--disable-mpich-support, "
PACKAGECONFIG[disable-ompi-support] = "--disable-ompi-support, "
PACKAGECONFIG[prte-prefix-by-default] = "--enable-prte-prefix-by-default, --disable-prte-prefix-by-default"
PACKAGECONFIG[uct-version-check] = "--enable-uct-version-check, --disable-uct-version-check"
PACKAGECONFIG[memchecker] = "--enable-memchecker, --disable-memchecker"
PACKAGECONFIG[disable-mmap-shmem] = "--disable-mmap-shmem, "
PACKAGECONFIG[disable-posix-shmem] = "--disable-posix-shmem, "
PACKAGECONFIG[disable-sysv-shmem] = "--disable-sysv-shmem, "

PACKAGECONFIG[disable-io-romio] = "--disable-io-romio, "
PACKAGECONFIG[mtl-portals4-flow-control] = "--enable-mtl-portals4-flow-control, --disable-mtl-portals4-flow-control"
PACKAGECONFIG[prte-prefix-by-default] = "--enable-prte-prefix-by-default, --disable-prte-prefix-by-default"
PACKAGECONFIG[disable-psm2-version-check] = "--disable-psm2-version-check, "
PACKAGECONFIG[disable-mmap-sshmem] = "--disable-mmap-sshmem, "
PACKAGECONFIG[disable-sysv-sshmem] = "--disable-sysv-sshmem, "
PACKAGECONFIG[visibility] = "--enable-visibility, --disable-visibility"
PACKAGECONFIG[disable-libtool-lock] = "--disable-libtool-lock, "


DEPENDS = "zlib ucx libtool"

EXTRA_OECONF = " \
    --enable-oshmem \
    --disable-mpi-fortran \
    --enable-shared \
    --disable-dlopen \
"


do_configure() {
    cd ${S}
    git submodule update --init --recursive
    ./autogen.pl || die "Failed to run ./autogen.pl"
    oe_runconf ${EXTRA_OECONF}
}

do_fix_makefile() {
    MAKEFILE_PATH="${S}/3rd-party/hwloc-2.7.1/utils/hwloc/Makefile.in"

    # fix xuantie-build/light-fm/tmp-glibc/work/riscv64-oe-linux/openmpi/5.1.0-r0/git/3rd-party/hwloc-2.7.1/utils/hwloc/Makefile.in
    if grep -q 'hwloc-compress-dir' "$MAKEFILE_PATH" && ! grep -q 'riscv64-oe-linux-hwloc-compress-dir' "${MAKEFILE_PATH}"; then
        sed -i '1825,1826s/hwloc-compress-dir/riscv64-oe-linux-hwloc-compress-dir/g' "${MAKEFILE_PATH}"
    fi

    if grep -q 'hwloc-gather-topology' "$MAKEFILE_PATH" && ! grep -q 'riscv64-oe-linux-hwloc-gather-topology' "${MAKEFILE_PATH}"; then
        sed -i '1827,1828s/hwloc-gather-topology/riscv64-oe-linux-hwloc-gather-topology/g' "${MAKEFILE_PATH}"
    fi
}

do_compile() {
    do_fix_makefile
    cd ${S}
    oe_runmake -C ${S}
}


do_install() {
    cd ${S}
    oe_runmake install DESTDIR=${D}
}

FILES:${PN} += "${datadir}"