require conf/machine/include/csky/arch-csky.inc

TUNEVALID[csky32] = "Enable 32-bit CSKY optimizations"

TUNEVALID[bigendian] = "Big endian mode"

AVAILTUNES += "csky"

# Default
TUNE_FEATURES_tune-csky32 = "csky32"
TUNE_ARCH_tune-csky32 = "csky32"
TUNE_PKGARCH_tune-csky32 = "csky32"
PACKAGE_EXTRA_ARCHS_tune-csky32 = "csky32"

