# Configuration to use external Sourcery G++ toolchain
EXTERNAL_TOOLCHAIN ??= "UNKNOWN"
EXTERNAL_TARGET_SYS ??= "${TARGET_ARCH}-${TARGET_OS}"
EXTERNAL_TOOLCHAIN_BIN ??= "${EXTERNAL_TOOLCHAIN}/bin"

# External toolchain features.
#
#   locale-utf8-is-default: assume en_US is utf8, not en_US.UTF-8, as is the
#                           case for OE.
EXTERNAL_TOOLCHAIN_FEATURES ?= ""

python () {
    oe.utils.features_backfill("EXTERNAL_TOOLCHAIN_FEATURES", d)
}

# Prefer our recipes which extract files from the external toolchain
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc ?= "gcc-external-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc-intermediate ?= "gcc-external-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}g++ ?= "gcc-external-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}binutils ?= "binutils-external-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}compilerlibs ?= "gcc-runtime-external"
PREFERRED_PROVIDER_gcc-runtime = "gcc-runtime-external"
PREFERRED_PROVIDER_gcc-sanitizers ?= "gcc-sanitizers-external"
PREFERRED_PROVIDER_libgcc-initial = "libgcc-external"
PREFERRED_PROVIDER_libnsl2 = "libnsl2"
PREFERRED_PROVIDER_libtirpc = "libtirpc"
# PREFERRED_PROVIDER_libgfortran = "libgfortran-external"
PREFERRED_PROVIDER_glibc ?= "glibc-external"
PREFERRED_PROVIDER_virtual/libc ?= "glibc-external"
PREFERRED_PROVIDER_virtual/libintl ?= "glibc-external"
PREFERRED_PROVIDER_virtual/libiconv ?= "glibc-external"
PREFERRED_PROVIDER_virtual/crypt ?= "libxcrypt"
PREFERRED_PROVIDER_gdb-cross-${TARGET_ARCH} ?= "gdb-external-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_oprofile ??= "oprofile"
# Work around bug where the implicitly created mlprefix preference isn't being
# obeyed for the runtime package installation.
PREFERRED_PROVIDER_${MLPREFIX}gdbserver ?= "${MLPREFIX}gdb"

# This is defined in default-providers.inc, which is parsed before the tcmode,
# so we can't use ?=
PREFERRED_PROVIDER_linux-libc-headers = "glibc-external"
PNBLACKLIST[linux-libc-headers] = "not building with an external toolchain"
PNBLACKLIST[linux-libc-headers-yocto] = "not building with an external toolchain"

PREFERRED_PROVIDER_gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} ??= "gcc-external-cross-canadian-${TRANSLATED_TARGET_ARCH}"
PREFERRED_PROVIDER_binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} ??= "binutils-external-cross-canadian-${TRANSLATED_TARGET_ARCH}"
PREFERRED_PROVIDER_gdb-cross-canadian-${TRANSLATED_TARGET_ARCH} ??= "gdb-external-cross-canadian-${TRANSLATED_TARGET_ARCH}"

# This is defined in default-providers.inc, which is parsed before the tcmode,
# so we can't use ?=
PREFERRED_PROVIDER_libgcc = "libgcc-external"

# Pass --no-sysroot-suffix when using an external toolchain with the SDK
TOOLCHAIN_TARGET_TASK_append = " sdk-env-external-toolchain"

# Sanity check the toolchain configuration and toolchain
INHERIT += "sanity-external-toolchain"

# Pull in our utility functions for use elsewhere
INHERIT += "external-common"

# Ensure that we don't pull in any internal toolchain recipes
INHERIT += "blacklist blacklist_dynamic"
PNBLACKLIST[uclibc] = "not building with an external toolchain"
PNBLACKLIST[glibc] = "not building with an external toolchain"
PNBLACKLIST[glibc-intermediate] = "not building with an external toolchain"
PNBLACKLIST[gcc-runtime] = "not building with an external toolchain"
PNBLACKLIST[gcc-sanitizers] = "not building with an external toolchain"
PNBLACKLIST[gcc-source] = "not building with an external toolchain"
PNBLACKLIST[libgcc-initial] = "not building with an external toolchain"
PNBLACKLIST[libgcc] = "not building with an external toolchain"
PNBLACKLIST[libgfortran] = "not building with an external toolchain"

PNBLACKLIST_DYNAMIC += "\
    ${MLPREFIX}gcc-cross-${TARGET_ARCH} \
    \
    binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} \
    gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} \
    gdb-cross-canadian-${TRANSLATED_TARGET_ARCH} \
    \
    gcc-source-${@'${GCCVERSION}'.replace('%', '')} \
"

# We need our -cross recipes to rebuild when the external toolchain changes,
# to recreate the links / wrapper scripts
BB_HASHBASE_WHITELIST_remove = "EXTERNAL_TOOLCHAIN"

# For a toolchain built with multilibs, we don't want any suffix implicitly
# added to the oe sysroot path, as those dirs will not exist.
# TOOLCHAIN_OPTIONS would seem more appropriate, but that gets added to LD as
# well, and --no-sysroot-suffix only works for gcc, not binutils.
HOST_CC_ARCH += "--no-sysroot-suffix"

# Certain recipes call ld directly, so ensure the correct emulation is used,
# particularly for multilib toolchains, since we can't rely on the default
LDEMULATION = ""
LDEMULATION_ENDIAN = "${@'bt' if 'bigendian' in '${TUNE_FEATURES}'.split() else 'lt'}"
LDEMULATION_BITS = "${@'64' if 'n64' in '${TUNE_FEATURES}'.split() else '32'}"
LDEMULATION_mips64 = "elf${LDEMULATION_BITS}${LDEMULATION_ENDIAN}smip${@bb.utils.contains('TUNE_FEATURES', 'n32', 'n32', '', d)}"
LDEMULATION_x86-64 = "elf_${TARGET_ARCH}"
LDEMULATION_x86-64_linux-gnux32 = "elf32_${TARGET_ARCH}"
TUNE_LDARGS += "${@'-m ${LDEMULATION}' if '${LDEMULATION}' else ''}"

# Additional search path for compiler component programs, to let us rebuild
# binutils if necessary.
gcc_bindir = "${STAGING_BINDIR_TOOLCHAIN}/gcc"
TUNE_CCARGS .= "${@' -B${gcc_bindir}' if d.getVar('PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}binutils') != 'binutils-external-cross-${TARGET_ARCH}' else ''}"

# Align the external toolchain ldflags with the internal
TARGET_LDFLAGS_BUILD_ID = "-Wl,--build-id=sha1"
TARGET_LDFLAGS += "${TARGET_LDFLAGS_BUILD_ID}"

TCMODEOVERRIDES = "tcmode-external"
TCMODEOVERRIDES_append = ":tcmode-${TCMODE}"
OVERRIDES =. "${TCMODEOVERRIDES}:"

python toolchain_metadata_setup () {
    # The external toolchain may not have been built with the yocto preferred
    # gnu hash setting, so ensure that the corresponding sanity check is a
    # warning, not an error.
    error_qa = (d.getVar('ERROR_QA', True) or '').split()
    if 'ldflags' in error_qa:
        error_qa.remove('ldflags')
        d.setVar('ERROR_QA', ' '.join(error_qa))
        d.appendVar('WARN_QA', ' ldflags')
}
toolchain_metadata_setup[eventmask] = "bb.event.ConfigParsed"
addhandler toolchain_metadata_setup

GCCVERSION ?= "${@'.'.join('${GCC_VERSION}'.split('.')[:2])}%"
GCC_VERSION = "${@external_run(d, 'gcc', '-dumpversion').rstrip()}"
GCC_VERSION_allarch = ""
GCC_VERSION[vardepvalue] = "${GCC_VERSION}"

# macro-prefix-map was introduced in gcc 8
REMOVE_MACRO_PREFIX_MAP = "-fmacro-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}"
DEBUG_PREFIX_MAP_remove_class-target = "${@'${REMOVE_MACRO_PREFIX_MAP}' if '${GCC_VERSION}' not in ['', 'UNKNOWN'] and [int(v) for v in '${GCC_VERSION}'.split('.')] < [8,0] else ''}"

BUILDCFG_VARS += "EXTERNAL_TOOLCHAIN EXTERNAL_TARGET_SYS GCC_VERSION"
