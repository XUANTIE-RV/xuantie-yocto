# GCC 15 defaults to -std=gnu23 which treats K&R `()` declarations as `(void)`.
# which-2.21's getopt.h declares `extern int getopt ();` and getopt.c defines
# `int getopt(int argc, char *const *argv, const char *optstring)` — now
# flagged as "conflicting types for 'getopt'". Force pre-C23 semantics.
CFLAGS:append = " -std=gnu17"
