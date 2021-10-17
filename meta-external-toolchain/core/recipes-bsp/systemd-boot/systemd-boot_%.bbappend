# Ensure -m32/-m64 are passed for EFI, as the buildsystem currently relies on
# the default for the toolchain, which isn't necessarily correct for an
# external toolchain.
EFI_TUNE_ARCH = "-m32"
EFI_TUNE_ARCH_x86-64 = "-m64"
EFI_CC_tcmode-external = "${@'${CC}'.split()[0]} ${EFI_TUNE_ARCH}"
EXTRA_OECONF_append_tcmode-external = " 'EFI_CC=${EFI_CC}'"
