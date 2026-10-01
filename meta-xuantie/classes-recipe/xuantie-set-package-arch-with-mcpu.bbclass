PACKAGE_ARCH = "${XUANTIE_MCPU_PACKAGE_ARCH}"
XUANTIE_PACKAGE_ARCH_SET = "YES"

XUANTIE_NATIVE_TUNE_CCARGS = " -march=${XUANTIE_NATIVE_MARCH} -mabi=${XUANTIE_MABI}"
# requires '-menable-experimental-extensions' for experimental extension 'zfbfmin zvfbfmin zvfbfwma'
XUANTIE_NATIVE_TUNE_CCARGS:append:toolchain-clang = " -menable-experimental-extensions"

XUANTIE_TUNE_CCARGS_GENERIC ?= " ${XUANTIE_TUNE_CCARGS}"

XUANTIE_MCPU_GENERIC:riscv32 ?= "c9xx-riscv32"
XUANTIE_MCPU_GENERIC:riscv64 ?= "c9xx-riscv64"

# =============================================================================
# Unified XuanTie optimization flags — categorized for per-recipe composition
# Only effective with XuanTie clang (open-source clang does not support these)
# Note: -xt-enable-vectorization is enabled by default in XuanTie toolchain,
#       no need to pass it explicitly.
# =============================================================================

# Loop optimization: loop distribution and bound splitting
XUANTIE_OPT_LOOP = ""
XUANTIE_OPT_LOOP:toolchain-clang:vendor-xuantie = "-mllvm -enable-loop-bound-split -mllvm -lbs-triangle-thresh=15"

# Inlining: aggressive inline threshold for performance-critical code
XUANTIE_OPT_INLINE = ""
XUANTIE_OPT_INLINE:toolchain-clang:vendor-xuantie = "-mllvm -inline-threshold=800"

# Structure reorg: interprocedural structure layout optimization
XUANTIE_OPT_STRUCT = ""
XUANTIE_OPT_STRUCT:toolchain-clang:vendor-xuantie = "-mxt-enable-structure-reorg"

# Default composite: empty by default — individual recipes opt-in as needed
# Individual recipes can override or extend via CFLAGS:append
XUANTIE_OPT_FLAGS = ""
