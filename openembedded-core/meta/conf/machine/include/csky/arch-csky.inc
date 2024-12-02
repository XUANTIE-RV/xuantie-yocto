# csky Architecture definition

DEFAULTTUNE ?= "csky32"

TUNE_ARCH = "${TUNE_ARCH:tune-${DEFAULTTUNE}}"
TUNE_PKGARCH = "${TUNE_PKGARCH:tune-${DEFAULTTUNE}}"
#TUNE_CCARGS:append = "${@bb.utils.contains('TUNE_FEATURES', 'csky32nf', ' -mabi=ilp32', ' ', d)}"

# QEMU usermode fails with invalid instruction error (For riscv32)
MACHINE_FEATURES_BACKFILL_CONSIDERED:append = "${@bb.utils.contains('TUNE_FEATURES', 'csky32', ' qemu-usermode', '', d)}"

# Fix: ld: unrecognized option '--hash-style=sysv'
LINKER_HASH_STYLE:libc-newlib = ""
# Fix: ld: unrecognized option '--hash-style=gnu'
LINKER_HASH_STYLE:libc-baremetal = ""
