# musl does not provide GNU basename() in <string.h>.
# We cannot use global -include libgen.h because edquota.c declares a
# 'static char *dirname' variable that conflicts with libgen.h's dirname().
# Instead, provide a minimal compat header that only declares basename().
CFLAGS:append:libc-musl = " -include ${S}/musl-basename-compat.h"

do_configure:prepend:libc-musl() {
    cat > ${S}/musl-basename-compat.h <<'EOF'
/* Minimal compat: declare basename() without dirname() to avoid
   conflicting with edquota.c's 'static char *dirname' variable. */
#ifndef _MUSL_BASENAME_COMPAT_H
#define _MUSL_BASENAME_COMPAT_H
#ifndef __GLIBC__
char *basename(char *);
#endif
#endif
EOF
}
