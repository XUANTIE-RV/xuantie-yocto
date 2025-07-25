wrap_bin () {
    bin="$1"
    shift
    script="${D}${bindir}/${TARGET_PREFIX}$bin"
    printf '#!/bin/sh\n' >$script
    for arg in "$@"; do
        printf '%s\n' "$arg"
    done >>"$script"
    printf 'exec $EXTERNAL_TOOLCHAIN_EXPORT_GCC/bin/${EXTERNAL_TARGET_SYS}-%s "$@"\n' "$bin" >>"$script"
    chmod +x "$script"
}

EXTERNAL_TOOLCHAIN = "${EXTERNAL_TOOLCHAIN_EXPORT_GCC}"
