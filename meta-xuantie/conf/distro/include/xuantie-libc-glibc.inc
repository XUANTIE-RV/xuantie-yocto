# glibc external toolchain configuration
# OE-Core defaults handle TCLIBC=glibc, PREFERRED_PROVIDERs, and LIBC_DEPENDENCIES.
# tcmode-external.inc handles external toolchain provider switching via :tcmode-external overrides.
# EXTERNAL_TARGET_SYS is set per vendor: xuantie-vendor-*.inc sets it to the actual toolchain triplet.
