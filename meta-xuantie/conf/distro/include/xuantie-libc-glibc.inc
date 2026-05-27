# glibc external toolchain configuration
# OE-Core defaults handle TCLIBC=glibc, PREFERRED_PROVIDERs, and LIBC_DEPENDENCIES.
# tcmode-external.inc handles external toolchain provider switching via :tcmode-external overrides.
# EXTERNAL_TARGET_SYS is set in rv32.inc/rv64.inc to "riscv64-linux", matching handle_multilib() symlinks.
