FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# musl libc does not provide the GNU extension basename() in <string.h>.
# kmod relies on _GNU_SOURCE basename (which takes const char*).
# Provide a POSIX-compatible fallback via <libgen.h> with a const wrapper
# injected into config.h (which is force-included by -include ./config.h).
do_configure:append:libc-musl() {
    cat >> ${B}/config.h <<'MUSL_EOF'

/* musl compat: provide GNU-compatible basename() via POSIX libgen.h */
#ifndef __GLIBC__
#include <libgen.h>
#endif
MUSL_EOF
}
