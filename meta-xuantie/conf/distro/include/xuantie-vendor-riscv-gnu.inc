# Open-source riscv-gnu-toolchain support
# https://github.com/riscv-collab/riscv-gnu-toolchain
#
# 本文件用 multi-override (:libc-*:vendor-riscv-gnu:archN) 设 EXTERNAL_TOOLCHAIN_*
# 跨 (libc, arch) 矩阵的路径. 期望值见 docs/toolchain-matrix.md,
# 审计 meta-xuantie/scripts/validate-toolchain-matrix.sh.
#
# Usage: TOOLCHAIN=riscv-gcc DISTRO=rv64-k6.6-debug bitbake xuantie-image-lite
#   (oe-buildenv-internal decomposes riscv-gcc → TOOLCHAIN=gcc + TOOLCHAIN_VENDOR=riscv-gnu)
#
# Per-variant deployment directories:
#   riscv-toolchain-gcc-glibc64/     rv64 glibc GCC
#   riscv-toolchain-gcc-glibc32/     rv32 glibc GCC
#   riscv-toolchain-gcc-muslc64/     rv64 musl GCC
#   riscv-toolchain-gcc-muslc32/     rv32 musl GCC
#   riscv-toolchain-gcc-uclibc-ng64/ rv64 uclibc-ng GCC
#   riscv-toolchain-gcc-uclibc-ng32/ rv32 uclibc-ng GCC
#   riscv-toolchain-clang-glibc64/   rv64 glibc Clang
#   riscv-toolchain-clang-glibc32/   rv32 glibc Clang
#   riscv-toolchain-clang-muslc64/   rv64 musl Clang
#   riscv-toolchain-clang-muslc32/   rv32 musl Clang
#   riscv-toolchain-clang-uclibc-ng64/ rv64 uclibc-ng Clang
#   riscv-toolchain-clang-uclibc-ng32/ rv32 uclibc-ng Clang

# 1. Add override for vendor-specific variable resolution
OVERRIDES:append = ":vendor-riscv-gnu"

# Helper: pick the GCC bundle dir for a given variant suffix, falling back
# to the Clang bundle when the dedicated GCC bundle is absent. The riscv-collab
# Clang tarball ships a co-tenant `riscv*-*-gcc` binary plus libgcc/libstdc++,
# so it can fully serve gcc-runtime-external in clang-only deployments.
def _xuantie_pick_riscv_gnu_gcc_dir(d, suffix):
    import os
    # YOCTOROOT (set in bblayers.conf) points at the real source tree and is
    # robust to a symlinked BUILDDIR (e.g. xuantie-build → xuantie-build-data).
    # Fall back to TOPDIR/../.. for legacy bblayers that don't export YOCTOROOT.
    yocto_root = d.getVar('YOCTOROOT')
    if not yocto_root:
        topdir = d.getVar('TOPDIR') or ''
        yocto_root = os.path.normpath(os.path.join(topdir, '..', '..'))
    gcc_dir = os.path.join(yocto_root, 'riscv-toolchain-gcc-' + suffix)
    if os.path.isdir(gcc_dir):
        return gcc_dir
    clang_dir = os.path.join(yocto_root, 'riscv-toolchain-clang-' + suffix)
    if os.path.isdir(clang_dir):
        return clang_dir
    # Neither present — return canonical GCC path so the existing
    # vendor-mismatch fatal in xuantie-base.inc surfaces a clean error.
    return gcc_dir

# 2. Toolchain path - glibc variant (default)
EXTERNAL_TOOLCHAIN_GCC:vendor-riscv-gnu:riscv64 = "${@_xuantie_pick_riscv_gnu_gcc_dir(d, 'glibc64')}"
EXTERNAL_TOOLCHAIN_GCC:vendor-riscv-gnu:riscv32 = "${@_xuantie_pick_riscv_gnu_gcc_dir(d, 'glibc32')}"

# 3. Toolchain path - muslc variant
EXTERNAL_TOOLCHAIN_GCC:libc-musl:vendor-riscv-gnu:riscv64 = "${@_xuantie_pick_riscv_gnu_gcc_dir(d, 'muslc64')}"
EXTERNAL_TOOLCHAIN_GCC:libc-musl:vendor-riscv-gnu:riscv32 = "${@_xuantie_pick_riscv_gnu_gcc_dir(d, 'muslc32')}"

# 4. Toolchain path - uclibc-ng variant (flat directories, no /rv64 /rv32 subdirs)
EXTERNAL_TOOLCHAIN_GCC:libc-uclibc-ng:vendor-riscv-gnu:riscv64 = "${@_xuantie_pick_riscv_gnu_gcc_dir(d, 'uclibc-ng64')}"
EXTERNAL_TOOLCHAIN_GCC:libc-uclibc-ng:vendor-riscv-gnu:riscv32 = "${@_xuantie_pick_riscv_gnu_gcc_dir(d, 'uclibc-ng32')}"

# 5. Clang toolchain path - glibc variant (default)
EXTERNAL_TOOLCHAIN_CLANG:vendor-riscv-gnu:riscv64 = "${YOCTOROOT}/riscv-toolchain-clang-glibc64"
EXTERNAL_TOOLCHAIN_CLANG:vendor-riscv-gnu:riscv32 = "${YOCTOROOT}/riscv-toolchain-clang-glibc32"

# 6. Clang toolchain path - muslc variant
EXTERNAL_TOOLCHAIN_CLANG:libc-musl:vendor-riscv-gnu:riscv64 = "${YOCTOROOT}/riscv-toolchain-clang-muslc64"
EXTERNAL_TOOLCHAIN_CLANG:libc-musl:vendor-riscv-gnu:riscv32 = "${YOCTOROOT}/riscv-toolchain-clang-muslc32"

# 7. Clang toolchain path - uclibc-ng variant
EXTERNAL_TOOLCHAIN_CLANG:libc-uclibc-ng:vendor-riscv-gnu:riscv64 = "${YOCTOROOT}/riscv-toolchain-clang-uclibc-ng64"
EXTERNAL_TOOLCHAIN_CLANG:libc-uclibc-ng:vendor-riscv-gnu:riscv32 = "${YOCTOROOT}/riscv-toolchain-clang-uclibc-ng32"

# 8. Flat sysroot - no multilib directories
EXTERNAL_TOOLCHAIN_LIB_DIR_NAME:vendor-riscv-gnu = "lib"
EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT = "lib"

# 9. Target triplet - glibc variant
EXTERNAL_TARGET_SYS:vendor-riscv-gnu:riscv64 = "riscv64-unknown-linux-gnu"
EXTERNAL_TARGET_SYS:vendor-riscv-gnu:riscv32 = "riscv32-unknown-linux-gnu"

# 10. Target triplet - muslc variant
EXTERNAL_TARGET_SYS:libc-musl:vendor-riscv-gnu:riscv64 = "riscv64-unknown-linux-musl"
EXTERNAL_TARGET_SYS:libc-musl:vendor-riscv-gnu:riscv32 = "riscv32-unknown-linux-musl"

# 11. Target triplet - uclibc-ng variant
EXTERNAL_TARGET_SYS:libc-uclibc-ng:vendor-riscv-gnu:riscv64 = "riscv64-unknown-linux-uclibc"
EXTERNAL_TARGET_SYS:libc-uclibc-ng:vendor-riscv-gnu:riscv32 = "riscv32-unknown-linux-uclibc"

# 12. GCC_VERSION: auto-detected by tcmode-external.inc via gcc -dumpversion

# 13. C dialect compatibility shim.
# The bundled open-source GCC 15 defaults to -std=gnu23, which treats K&R-style
# empty parameter lists (`extern char *getenv ();`) as `(void)`. Many upstream
# OE packages still rely on the pre-C23 "unknown args" semantics and break with
# errors like "too many arguments to function 'getenv'" / "conflicting types".
# Force pre-C23 semantics on every target compile under this vendor so the
# upstream Yocto recipe set still builds. Scoped to vendor-riscv-gnu so the
# XuanTie toolchain path (which historically ships an older GCC) is unaffected.
#
# Scope: 影响的是 C23 新特性 (nullptr / [[attr]] / constexpr / typeof_unqual / _BitInt 等),
#        C17/C11/C99 代码完全不受影响 (gnu17 = C17 + GNU 扩展).
#
# 包级别 escape hatch (recipe 真需要 C23 时):
#     CFLAGS:remove:pn-<package> = "-std=gnu17"
#     CFLAGS:append:pn-<package> = " -std=gnu23"
# 不需要修改本文件; BitBake :remove/:append 已是标准接口.
CFLAGS:append:vendor-riscv-gnu = " -std=gnu17"
