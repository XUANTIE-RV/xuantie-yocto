require recipes-external/gdb/gdb-external.inc
inherit external-toolchain-cross

PN .= "-${TARGET_ARCH}"

EXTERNAL_CROSS_BINARIES = "${gdb_binaries}"
