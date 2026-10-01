SUMMARY = "LLVM based C/C++ compiler Runtime (from external XuanTie clang toolchain)"
DESCRIPTION = "Pre-built compiler-rt libraries from the XuanTie clang toolchain"
SECTION = "devel"

PV = "${CLANG_VERSION}"
LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "${COMMON_LIC_CHKSUM}"

inherit external-toolchain

PROVIDES += "compiler-rt"

INHIBIT_DEFAULT_DEPS = "1"

DEPENDS += "virtual/${TARGET_PREFIX}binutils"

EXTERNAL_AUTO_PROVIDE = "0"
EXTERNAL_AUTO_PROVIDE:class-target = "0"

FILES:${PN} = " \
    ${libdir}/libclang_rt.*.so.* \
    ${libdir}/libclang_rt.*.so \
"
FILES:${PN}-dev = " \
    ${libdir}/libclang_rt.*.syms \
    ${libdir}/clang_rt.crt*.o \
    ${libdir}/libclang_rt.asan-preinit*.a \
"
FILES:${PN}-staticdev = " \
    ${libdir}/libclang_rt.*.a \
"

INSANE_SKIP:${PN} += "ldflags textrel already-stripped file-rdeps"
INSANE_SKIP:${PN}-dev += "ldflags textrel file-rdeps"
INSANE_SKIP:${PN}-staticdev += "ldflags textrel file-rdeps"

RPROVIDES:${PN} += "compiler-rt"
RPROVIDES:${PN}-dev += "compiler-rt-dev"
RPROVIDES:${PN}-staticdev += "compiler-rt-staticdev"

RDEPENDS:${PN}-dev += "${PN}-staticdev"

python () {
    if d.getVar('TOOLCHAIN') != 'clang':
        raise bb.parse.SkipRecipe("compiler-rt-external only needed with TOOLCHAIN=clang")
}

# Override external-toolchain's do_install with custom logic that copies
# compiler-rt files from the clang toolchain sysroot.
do_install () {
    CLANG_SYSROOT="${EXTERNAL_TOOLCHAIN_CLANG}/sysroot/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}"
    DEST_LIBDIR="${D}${libdir}"

    # Try multiple multilib subdirectories; use the first that exists
    for ml in lp64d lp64; do
        SRC="${CLANG_SYSROOT}/${ml}"
        if [ -d "$SRC" ] && ls "$SRC"/libclang_rt.* >/dev/null 2>&1; then
            install -d "$DEST_LIBDIR"
            # Shared libs (.so)
            for f in "$SRC"/libclang_rt.*.so; do
                [ -e "$f" ] && install -m 0755 "$f" "$DEST_LIBDIR/"
            done
            # Static archives (.a)
            for f in "$SRC"/libclang_rt.*.a; do
                [ -e "$f" ] && install -m 0644 "$f" "$DEST_LIBDIR/"
            done
            # Symbol files (.syms)
            for f in "$SRC"/libclang_rt.*.syms; do
                [ -e "$f" ] && install -m 0644 "$f" "$DEST_LIBDIR/"
            done
            # CRT objects
            for f in "$SRC"/clang_rt.crt*.o; do
                [ -e "$f" ] && install -m 0644 "$f" "$DEST_LIBDIR/"
            done
            break
        fi
    done
}
