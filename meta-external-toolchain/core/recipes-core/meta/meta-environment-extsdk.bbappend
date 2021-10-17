
# Don't dump the -B${gcc_bindir} appended to TUNE_CCARGS and subsequently
# to TARGET_CC_ARCH, since it consists of local machine toolchain paths.
TUNE_CCARGS_remove = "-B${gcc_bindir}"
