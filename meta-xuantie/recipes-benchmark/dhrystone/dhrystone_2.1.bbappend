LDFLAGS += "-static"

# GCC 15 defaults to -std=gnu23, which treats K&R-style `()` declarations as
# `(void)`. dhrystone's `extern char *malloc ();` then conflicts with calls
# like `malloc(sizeof(...))` ("too many arguments to function 'malloc'").
# Force the pre-C23 semantics so the upstream K&R prototypes still compile.
CFLAGS:append = " -std=gnu17"

DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'xtstring-opt', ' libxtstring-muslc', '', d)}"
EXTRA_OEMAKE:append = "${@bb.utils.contains('DISTRO_FEATURES', 'xtstring-opt', ' LDLIBS+=-lXTString', '', d)}"
