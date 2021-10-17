require recipes-external/gcc/gcc-external.inc
inherit external-toolchain-cross

PN .= "-${TARGET_ARCH}"
DEPENDS += "virtual/${TARGET_PREFIX}binutils"
PROVIDES += "\
    virtual/${TARGET_PREFIX}gcc-intermediate \
    virtual/${TARGET_PREFIX}gcc \
    virtual/${TARGET_PREFIX}g++ \
"

EXTERNAL_CROSS_BINARIES = "${gcc_binaries}"
