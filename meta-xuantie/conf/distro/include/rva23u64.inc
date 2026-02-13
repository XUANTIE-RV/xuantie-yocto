require conf/distro/include/rv64.inc
TOOLCHAIN = "clang"

XUANTIE_MARCH = "rva23u64"
XUANTIE_TUNE_CCARGS:append:toolchain-clang = " -menable-experimental-extensions"
EXTERNAL_TOOLCHAIN_LIB_DIR_NAME = "lib64"

PREFERRED_VERSION_spec2017 = "1.0.6"
PREFERRED_VERSION_spec2006 = "1.2"

XUANTIE_PACKAGE_FEED_ARCH ?= "rva23"
