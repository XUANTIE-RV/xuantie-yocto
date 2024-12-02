require conf/machine/include/csky/arch-csky.inc

TUNEVALID[csky32] = "Enable 32-bit CSKY optimizations"

TUNEVALID[bigendian] = "Big endian mode"

AVAILTUNES += "csky"

# Default
TUNE_FEATURES:tune-csky32 = "csky32"
TUNE_ARCH:tune-csky32 = "csky32"
TUNE_PKGARCH:tune-csky32 = "csky32"
PACKAGE_EXTRA_ARCHS:tune-csky32 = "csky32"

