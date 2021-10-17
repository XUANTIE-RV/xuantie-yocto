inherit external-toolchain cross

EXTERNAL_CROSS_BINARIES ?= ""

wrap_bin () {
    bin="$1"
    shift
    script="${D}${bindir}/${TARGET_PREFIX}$bin"
    printf '#!/bin/sh\n' >$script
    for arg in "$@"; do
        printf '%s\n' "$arg"
    done >>"$script"
    printf 'exec ${EXTERNAL_TOOLCHAIN_BIN}/${EXTERNAL_TARGET_SYS}-%s "$@"\n' "$bin" >>"$script"
    chmod +x "$script"
}

do_install () {
    install -d ${D}${bindir}
    for bin in ${EXTERNAL_CROSS_BINARIES}; do
        if [ ! -e "${EXTERNAL_TOOLCHAIN_BIN}/${EXTERNAL_TARGET_SYS}-$bin" ]; then
            continue
        fi

        wrap_bin "$bin"
    done
}
