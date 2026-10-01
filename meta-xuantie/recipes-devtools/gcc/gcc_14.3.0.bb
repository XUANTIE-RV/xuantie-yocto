SUMMARY = "GNU cc and gcc C compilers"
HOMEPAGE = "http://gcc.org/"
DESCRIPTION = "GNU cc and gcc C compilers."
LICENSE = "CLOSED"
PV = "14.3.0-V3.5.0"

FILE_RV64_510 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.4.0/gcc_14.3.0_riscv64-5.10.4-rv64gc-lp64d.deb;sha256sum=31189ed3e31e5c21cbc73d84effdabdca683bbcf3ffb339d8ae1a065eb4e0fdb"
FILE_RV64_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.4.0/gcc_14.3.0_riscv64-6.6.36-rv64gc-lp64d.deb;sha256sum=bac8e759defd8eb87a8b550c8a0b5ff6126040444fc272ccb17f17133992e23e"
FILE_RV32_66 = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.4.0/gcc_14.3.0_riscv32-6.6.36-rv32gc-ilp32d.deb;sha256sum=97a4b5319d186bae6fd52f07c3c62174fbc9a68d10fa9f5c390b8229f7d7dd26"

SRC_URI:riscv64 = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', \
    '${FILE_RV64_510}', \
    '${FILE_RV64_66}', d)}"

SRC_URI:riscv32 = "${FILE_RV32_66}"

PR = "r1"

# on-target deb 内部 triplet 只由 target arch 决定(prebuilt glibc toolchain,
# configure --target=riscv{32,64}-unknown-linux-gnu),与交叉工具链的
# EXTERNAL_TARGET_SYS 无关:vendor-xuantie 的交叉器是单一 riscv64 multilib exe,
# rv32 下 EXTERNAL_TARGET_SYS 仍是 riscv64-...,会与 rv32 deb 的 riscv32-... 目录错配。
GCC_DEB_TARGET_SYS = "riscv${@'32' if d.getVar('TUNE_ARCH') == 'riscv32' else '64'}-unknown-linux-gnu"

# c++headers is a drop-in replacement for libstdc++-dev (ships the same
# C++ headers + libstdc++.so dev symlink). Keep the FULL Provides+Conflicts+Replaces
# triad: Provides satisfies deps that want libstdc++-dev, Conflicts prevents
# co-install, Replaces resolves file ownership. Dropping Provides makes apt
# EVICT c++headers (and thus gcc) when any -dev chain pulls libstdc++-dev
# (observed: `apt install python3` uninstalls gcc).
# Do NOT re-add libstdc++-dev to RDEPENDS on toolchain upgrades. See #83585945
# (fix 10a9c12c97, regressed by b38e35620a, re-fixed by 53dc139838).
#
# glibc-only: under the musl external toolchain, glibc/gcc-runtime/musl-obstack
# are SKIP_RECIPE'd, so the on-target gcc chain (libc6-dev, binutils-tools,
# elfutils) has no provider. Providing libstdc++-dev here un-prunes that chain
# and turns those missing providers into build-breaking ERRORs. Restricting the
# triad to libc-glibc keeps the eviction fix where apt/on-target gcc actually
# exist, and lets musl fall back to pruning the chain (the prior good behavior).
# See #84352645.
RPROVIDES:c++headers:libc-glibc = "libstdc++-dev"
RREPLACES:c++headers:libc-glibc = "libstdc++-dev"
RCONFLICTS:c++headers:libc-glibc = "libstdc++-dev"

INSANE_SKIP:${PN} += "file-rdeps dev-deps infodir libdir"
INSANE_SKIP:${PN} += "dev-so staticdev"
INSANE_SKIP:${PN} += "already-stripped"
INSANE_SKIP:${PN} += "installed-vs-shipped"
INSANE_SKIP:gcclibs += "staticdev"
INSANE_SKIP:c++headers += "dev-so"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

FILES:${PN} += "\
    usr/bin/* \
    usr/libexec/* \
    usr/lib/bfd-plugins/* \
    usr/${GCC_DEB_TARGET_SYS}/bin/* \
    usr/${GCC_DEB_TARGET_SYS}/lib/* \
    usr/share/* \ 
"
FILES:gcclibs += "\
    usr/lib/gcc/* \
"
FILES:c++headers += "\
    usr/include/* \
    usr/lib/libstdc++.so \
"

PACKAGES = "gcclibs ${PN} c++headers"
do_install () {
    cp -rfd ${WORKDIR}/usr ${D}
    rm -rf ${D}/usr/${GCC_DEB_TARGET_SYS}/lib/libstdc++.so*
    rm -rf ${D}/usr/${GCC_DEB_TARGET_SYS}/lib/libgcc_s.so.1
    mkdir -p ${D}${libdir}
    ln -sf libstdc++.so.6 ${D}${libdir}/libstdc++.so
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

# rv32 on-target `gcc -lgcc_s` needs the libgcc_s dev symlink on the native ld
# search path. libgcc-external-dev already ships ${base_libdir}/libgcc_s.so
# (/lib, on the default search path); depend on it instead of shipping a second
# copy under ${libdir}. Shipping our own ${libdir}/libgcc_s.so.1 would register
# gcc as a second provider of SONAME libgcc_s.so.1 (libgcc-external is the
# other) and break do_package with "Multiple shlib providers".
RDEPENDS:${PN}:append:riscv32 = " libgcc-external-dev"

RDEPENDS:${PN} += "libstdc++ c++headers gcclibs libgcc binutils libc6-dev"
