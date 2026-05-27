inherit xuantie-clang18-compatible xuantie-gcc14-compatible

# musl does not provide GNU basename() in <string.h>; inject POSIX version
# via config.h which is force-included by -include config.h.
do_configure:append:libc-musl() {
    cat >> ${B}/config.h <<'MUSL_EOF'

/* musl compat: provide basename() via POSIX libgen.h */
#ifndef __GLIBC__
#include <libgen.h>
#endif
MUSL_EOF
}
