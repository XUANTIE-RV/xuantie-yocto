#
# uclibc specific configuration for open-source toolchain (uclibc-ng-external)
#

LIBCOVERRIDE = ":libc-uclibc-ng"

# uclibc-ng configuration
PREFERRED_PROVIDER_virtual/libc ?= "uclibc-ng-external"
PREFERRED_PROVIDER_virtual/libiconv ?= "uclibc-ng-external"
PREFERRED_PROVIDER_virtual/libintl ?= "uclibc-ng-external"
PREFERRED_PROVIDER_virtual/crypt ?= "uclibc-ng-external"
PREFERRED_PROVIDER_uclibc-ng ?= "uclibc-ng-external"

# uclibc-ng does not support glibc locale system
IMAGE_LINGUAS = ""

DISTRO_FEATURES_BACKFILL_CONSIDERED += "ldconfig"

# dpkg depends on ldconfig which is glibc-specific, disable for uclibc-ng
EXTRA_RDPENDS:libc-uclibc-ng = ""

CXXFLAGS += "-fvisibility-inlines-hidden"

LIBC_DEPENDENCIES = "\
    uclibc-ng-external \
    uclibc-ng-external-dev \
    "

# Include shared uclibc-ng compiler flags configuration
require conf/distro/include/tclibc-uclibc-common.inc
