inherit xuantie-clang-compatible xuantie-gcc-compatible

# musl does not provide GNU basename() in <string.h>; inject POSIX version
# via config.h which is force-included by -include config.h.
do_configure:append:libc-musl() {
    cat >> ${B}/config.h <<'MUSL_EOF'

/* musl compat: provide basename() via POSIX libgen.h */
#ifndef __GLIBC__
#include <libgen.h>
#endif
MUSL_EOF

    # musl's linux/stat.h lacks stx_mnt_id field (added in Linux 5.15)
    # configure incorrectly detects it as present; override to disable the code path
    sed -i 's/^#define HAVE_STRUCT_STATX_STX_MNT_ID 1$/#undef HAVE_STRUCT_STATX_STX_MNT_ID/' ${B}/config.h
}
