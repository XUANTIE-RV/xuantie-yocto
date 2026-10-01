
SUMMARY = "clang"
SECTION = "devel"
PV = "${CLANG_VERSION}"
LICENSE = "CLOSED"

clang_binaries = "clang clang++ clang-tidy ld.lld lld llvm-addr2line \
    llvm-ar llvm-as llvm-cov llvm-dwp llvm-nm llvm-objcopy llvm-objdump \
    llvm-profdata llvm-ranlib llvm-readelf llvm-size llvm-strings llvm-strip flang-new"


inherit external-toolchain-cross

PN .= "-${TARGET_ARCH}"
DEPENDS += "virtual/${TARGET_PREFIX}binutils"
PROVIDES += "\
    clang-cross-${TARGET_ARCH} \
"

EXTERNAL_CROSS_BINARIES = "${clang_binaries}"


wrap_bin () {
    bin="$1"
    shift
    script="${D}${bindir}/${TARGET_PREFIX}$bin"
    printf '#!/bin/sh\n' >$script
    for arg in "$@"; do
        printf '%s\n' "$arg"
    done >>"$script"
    printf 'exec $EXTERNAL_TOOLCHAIN_EXPORT_CLANG/bin/%s "$@"\n' "$bin" >>"$script"
    chmod +x "$script"
    ln -sr "$script" "${D}${bindir}/$bin"
}

do_install () {
    install -d ${D}${bindir}
    for bin in ${EXTERNAL_CROSS_BINARIES}; do
        if [ ! -e "${EXTERNAL_TOOLCHAIN_CLANG}/bin/$bin" ]; then
            bbdebug 1 "${EXTERNAL_TOOLCHAIN_CLANG}/${EXTERNAL_TARGET_SYS}-$bin does not exist"
            continue
        fi

        bbdebug 1 wrap_bin "$bin"
        wrap_bin "$bin"
    done

    # Fallback: if llvm-readelf was not created (binary missing from toolchain)
    # but llvm-readobj exists, create a wrapper using llvm-readobj with GNU output
    # style.  llvm-readelf is normally a symlink to llvm-readobj; the only
    # difference is the default output format.
    if [ ! -e "${D}${bindir}/${TARGET_PREFIX}llvm-readelf" ] && \
       [ -e "${EXTERNAL_TOOLCHAIN_CLANG}/bin/llvm-readobj" ]; then
        script="${D}${bindir}/${TARGET_PREFIX}llvm-readelf"
        printf '#!/bin/sh\nexec $EXTERNAL_TOOLCHAIN_EXPORT_CLANG/bin/llvm-readobj --elf-output-style=GNU "$@"\n' >"$script"
        chmod +x "$script"
        ln -sr "$script" "${D}${bindir}/llvm-readelf"
    fi
}
