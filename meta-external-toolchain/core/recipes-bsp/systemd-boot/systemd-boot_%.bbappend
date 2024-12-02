# Ensure -m32/-m64 are passed for EFI, as the buildsystem currently relies on
# the default for the toolchain, which isn't necessarily correct for an
# external toolchain.
EFI_TUNE_ARCH = "-m32"
EFI_TUNE_ARCH:x86-64 = "-m64"
EFI_CC:tcmode-external = "${CCACHE}${HOST_PREFIX}gcc ${EFI_TUNE_ARCH}"
EXTRA_OECONF:append:tcmode-external = " 'EFI_CC=${EFI_CC}'"
