require conf/machine/include/riscv/tune-riscv.inc
require xuantie-clang.inc

DISTRO_NAME = "xuantie C9xx Release Distro"
DISTRO_VERSION = "1.4.0"

XUANTIE_LINUX_TAG = "XuanTie_V1.4.0"

# External toolchain path - dynamically set based on TOOLCHAIN_VENDOR, LIBC, and TUNE_ARCH
# For xuantie vendor: uses xuantie-toolchain (default)
# For riscv-gnu: uses per-variant directories (e.g., riscv-toolchain-gcc-muslc32)
EXTERNAL_TOOLCHAIN ?= "${@_xuantie_get_toolchain_path(d)}"
TCMODE = "external"
TARGET_OS = "linux"
EXTERNAL_TOOLCHAIN_SYSROOT ?= "${EXTERNAL_TOOLCHAIN}/sysroot"

_EXTERNAL_TOOLCHAIN = "${EXTERNAL_TOOLCHAIN_GCC}"
_EXTERNAL_TOOLCHAIN:toolchain-clang = "${EXTERNAL_TOOLCHAIN_CLANG}"
export EXTERNAL_TOOLCHAIN_EXPORT ?= "${_EXTERNAL_TOOLCHAIN}"
BB_BASEHASH_IGNORE_VARS:append = " EXTERNAL_TOOLCHAIN_BIN EXTERNAL_TOOLCHAIN_LIBROOT \
    EXTERNAL_TOOLCHAIN_SYSROOT EXTERNAL_HEADERS_MULTILIB_SUFFIX \
    TOOLCHAIN_DIR EXTERNAL_TOOLCHAIN_EXPORT BUILD_ROOT RV_TOOLCHAIN "
INSANE_SKIP:${PN} += "glibc-so gobject-introspection-so glibc-external"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-xuantie"
PREFERRED_RPROVIDER_libopcodes = "binutils"
PREFERRED_RPROVIDER_libbfd = "binutils"
PREFERRED_RPROVIDER_binutils-src = "binutils-src"
KBUILD_DEFCONFIG ?= "defconfig"
SERIAL_CONSOLES ?= "115200;ttyS0"

PREFERRED_VERSION_op-tee ?= "0.1"
# PREFERRED_VERSION must be set at parse time (not in a python block) to take effect
PREFERRED_VERSION_optee-os = "${@'3.15' if d.getVar('XUANTIE_KERNEL_VER') == '5.10' else '4.8'}"
PREFERRED_VERSION_optee-os-fpga = "${@'3.15' if d.getVar('XUANTIE_KERNEL_VER') == '5.10' else '4.8'}"
PREFERRED_VERSION_optee-client = "${@'3.15' if d.getVar('XUANTIE_KERNEL_VER') == '5.10' else '4.8'}"
PREFERRED_VERSION_optee-examples = "${@'3.15' if d.getVar('XUANTIE_KERNEL_VER') == '5.10' else '4.8'}"
PREFERRED_VERSION_optee-test = "${@'3.15' if d.getVar('XUANTIE_KERNEL_VER') == '5.10' else '4.8'}"
PREFERRED_VERSION_trusted-firmware-a ?= "2.3"
PREFERRED_VERSION_trusted-firmware-a-fpga ?= "2.3"
PREFERRED_VERSION_opensbi = "${@'0.9' if d.getVar('XUANTIE_KERNEL_VER') == '5.10' else '1.7'}"
PREFERRED_VERSION_zsb = "${@'1.2' if d.getVar('XUANTIE_KERNEL_VER') == '5.10' else '1.3'}"
PREFERRED_VERSION_libjpeg-turbo-native ?= "2.1.4"
PREFERRED_VERSION_qemu-native ?= "9.0.94"
PREFERRED_VERSION_kexec-tools ?= "2.0.22"
PREFERRED_VERSION_openssl ?= "3.4.0"
# tiff 4.6.0 / libwebp 1.4.0 (recipes in meta-xuantie) supersede oe-core
# tiff 4.4.0 / libwebp 1.2.4: vllm runtime needs libtiff.so.6 and libsharpyuv
# (ticket 84841963)
PREFERRED_VERSION_tiff = "4.6.0"
PREFERRED_VERSION_libwebp = "1.4.0"
# redis 8.10.0 (recipe in meta-xuantie/recipes-extended/redis/redis_8.10.0.bb)
# supersedes meta-oe's locally patched 7.0.5
PREFERRED_VERSION_redis = "8.10.0"

# Python 3.13.4 as the product interpreter (recipe in
# recipes-devtools/python/python3_3.13.4.bb). PREFERRED_VERSION must be set at
# parse time (not in a python block) to take effect.
PREFERRED_VERSION_python3 = "3.13.4"
PREFERRED_VERSION_python3-native = "3.13.4"
PREFERRED_VERSION_python3-nativesdk = "3.13.4"
# langdale's classes-recipe/python3-dir.bbclass hardcodes PYTHON_BASEVERSION=3.10
# at class scope, which beats a plain config-level "=". :forcevariable is applied
# last (highest OVERRIDES priority) so all site-packages land in lib/python3.13.
PYTHON_BASEVERSION:forcevariable = "3.13"
# Mask oe-core's 3.10.x recipe so its python3-native class-extension does not
# co-schedule with 3.13.4 ("Multiple versions of python3-native are due to be built").
# BBMASK entries are regexes (re.search), so match any 3.10 point release.
BBMASK += "openembedded-core/meta/recipes-devtools/python/python3_3\.10\.\d+\.bb"

KERNEL_IMAGETYPE = "Image"

MACHINE_FEATURES = "keyboard ext2 ext3 serial"
MACHINE_FEATURES:remove ?= " rtc "

ASSUME_PROVIDED += "\
    libcxx-dev \
    libcxx-staticdev \
"

WARN_QA:remove = " buildpaths "

__BBSEENSRCREV = "1"
do_cleanall[network] = "1"

# QEMU user-mode CPU for RISC-V
# rv32: c907fdv-rv32
# rv64 internal: xt-* distros map to the matching xt-* QEMU cpu model when
#   the model exists in qemu-native (suffixes -gnu/-wo-xthead stripped);
#   others fall back to max with vendor extensions switched on (max alone
#   only enables standard extensions)
# rv64 public: xthead toolchains fall back to c920v3 (the public fork qemu
#   carries a c920v3 zalasr patch), others to max
XT_QEMU_RV64_MODELS = "xt-c9204fdt xt-c9204fdvt xt-c9204fdvkt xt-c9251fdt xt-c9251fdvt xt-c9251fdvkt xt-c925-cp xt-c925v-cp xt-c9301fdt xt-c9301fdvkt xt-c930-cp xt-c930v-cp xt-c9501fdvt xt-c9501fdvmt xt-c9501fdvkt xt-c9501fdvkmt xt-r9201fdt xt-r9201fdvt xt-r9201fdvkt"
XT_QEMU_RV64_FALLBACK = "max,zalasr=on,xtheadba=on,xtheadbb=on,xtheadbs=on,xtheadcmo=on,xtheadcondmov=on,xtheadfmemidx=on,xtheadmac=on,xtheadmemidx=on,xtheadmempair=on,xtheadsync=on"

def get_qemu_riscv_cpu(d):
    if d.getVar('MACHINECONFIG') != 'public':
        distro = d.getVar('DISTRO') or ''
        for suffix in ('-gnu', '-wo-xthead'):
            if distro.endswith(suffix):
                distro = distro[:-len(suffix)]
        if distro in (d.getVar('XT_QEMU_RV64_MODELS') or '').split():
            return distro
        return d.getVar('XT_QEMU_RV64_FALLBACK')
    lib = d.getVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME') or 'lib64xthead'
    return 'max' if 'xthead' not in lib else 'c920v3'

QEMU_RISCV_CPU:riscv32 = "c907fdv-rv32"
QEMU_RISCV_CPU:riscv64 = "${@get_qemu_riscv_cpu(d)}"
QEMU_OPTIONS:append:class-target = " -cpu ${QEMU_RISCV_CPU}"

python() {
    import re
    src_rev = d.getVar('XUANTIE_LINUX_TAG')
    if src_rev is not None:
        pattern = r"^Linux_SDK_V(\d+\.\d+\.\d+)$"
        match = re.match(pattern, src_rev)
        if match:
            version = match.group(1)
            version_limit = '.'.join([version.split('.')[0], version.split('.')[1], '99'])
        else:
            version = "1.0"
            version_limit = "1.99"
    else:
        version = "1.0"
        version_limit = "1.99"
    d.setVar('SDK_VERSION', version)
    d.setVar('BSP_VERSION', version)
    d.setVar('BSP_VERSION_LIMIT', version_limit)
}

XUANTIE_PACKAGE_FEED_ARCH ??= "${TUNE_ARCH}"
PACKAGE_FEED_URIS ?= "http://114.55.254.174/xuantie/deb/release/${DISTRO_VERSION}/${XUANTIE_KERNEL_VERSION}/${XUANTIE_PACKAGE_FEED_ARCH}"
PACKAGE_FEED_ARCHS = "common"

INHERIT += "own-mirrors"
SOURCE_MIRROR_URL ?= "http://114.55.254.174/xuantie/downloads-mirror/1.x/release"
SSTATE_MIRRORS ?= "file://.* http://114.55.254.174/xuantie/sstate-cache/1.1/dev/${XUANTIE_LINUX_TAG}/${XUANTIE_KERNEL_VERSION}/PATH;downloadfilename=PATH"

PYTHON_PIP_URL ?= "http://114.55.254.174/th1520/python3/release/simple/"

IMAGE_FSTYPES += "cpio.gz ext4"

XUANTIE_MCPU_PACKAGE_ARCH ??= "${XUANTIE_MCPU}"
XUANTIE_PACKAGE_ARCH_SET ?= "NO"
XUANTIE_TUNE_CCARGS_USE_MCPU ??= "NO"

XUANTIE_TUNE_CCARGS ?= "-march=${XUANTIE_MARCH} -mabi=${XUANTIE_MABI}"
# For packages forced to GCC (via TOOLCHAIN:pn-xxx = "gcc") in profile-based DISTRO modes,
# GCC doesn't support profile march names (rva23u64 etc.), fall back to rv64gc/rv32gc
XUANTIE_TUNE_CCARGS:toolchain-gcc = "${@_xuantie_gcc_fallback_tune_ccargs(d)}"
TUNE_CCARGS:append = "${@bb.utils.contains('XUANTIE_PACKAGE_ARCH_SET', 'NO', " ${XUANTIE_TUNE_CCARGS}", ' ', d)}"

# --- RVV 1.0 availability ---------------------------------------------------
# Whether the effective ISA provides RVV 1.0, as 'yes' / 'no' / 'unknown'.
# Vector-gated packages key off this instead of DISTRO names: rvv-bench is RVV
# 1.0 hand-written asm, and the no-vector paths are not one DISTRO but a family
# (rv64gc, rv64-k6.6-default/-debug via rv64gc_zfh_xtheadc, rv32*, rv64imac,
# march-mode CPU confs, ...), so a per-name override list would keep growing and
# keep missing members. This mirrors the deb-side split in linux-image-tools
# config/pkgsets.conf: cat1_basic keeps rvv-bench, cat1_basic_norvv and
# cat1_basic_rvv071 drop it.
#
#   'no'      -- ISA string positively lacks v and zve*/zvl*. xtheadvector does
#                not count: it is RVV 0.7.1, whose assembler rejects RVV 1.0
#                mnemonics, so rvv-bench cannot build there either.
#   'unknown' -- XUANTIE_MARCH is a profile name (rva23u64) or empty (-mcpu
#                mode, where yocto has no parse-time ISA). Reported as unknown
#                rather than guessed, so nothing that builds today gets dropped.
def _xuantie_has_rvv10(d):
    isa = (d.getVar('XUANTIE_MARCH') or '').strip().lower()
    if not isa.startswith(('rv32', 'rv64')):
        return 'unknown'
    head, _, rest = isa[4:].partition('_')
    if 'v' in head:
        return 'yes'
    if any(t.startswith(('zve', 'zvl')) for t in rest.split('_') if t):
        return 'yes'
    return 'no'

XUANTIE_HAS_RVV10 = "${@_xuantie_has_rvv10(d)}"

# Multilib auto-detection: query GCC -print-multi-directory to determine
# the correct library subdirectory (e.g., lib64xthead, lib64, lib32) instead
# of hardcoding it per DISTRO config.

# RISC-V profile names (e.g., rva23u64) are accepted by Clang but NOT by
# riscv-gnu GCC (15.2.0), which only understands ISA strings starting with
# 'rv32' or 'rv64'. For multilib directory selection, only the base arch
# (rv32/rv64) and ABI matter -- the full extension list does not change the
# multilib path. So when querying GCC for the multilib dir, translate any
# profile-name -march to a simple rv32gc/rv64gc equivalent. The original
# XUANTIE_TUNE_CCARGS is left untouched for actual compilation (Clang uses it).
def _xuantie_simplify_march_for_multilib(arg):
    """If arg is '-march=<profile>' (not rv32/rv64), return a simplified
    '-march=rv32gc' or '-march=rv64gc' for GCC multilib query only.
    Returns arg unchanged otherwise."""
    import re
    if not arg.startswith('-march='):
        return arg
    march = arg[len('-march='):]
    if march.startswith('rv32') or march.startswith('rv64'):
        return arg
    # Profile names: rva<digits>u32/u64, rvb<digits>u32/u64, rvm<digits>u32/u64
    m = re.match(r'^rv[abm]\d+u(32|64)$', march)
    if m:
        return '-march=rv' + m.group(1) + 'gc'
    return arg

def _xuantie_sanitize_gcc_args(gcc_args):
    """Apply profile-name translation to every -march= entry in gcc_args."""
    return [_xuantie_simplify_march_for_multilib(a) for a in gcc_args]

def _xuantie_gcc_fallback_tune_ccargs(d):
    """For gcc-forced packages, simplify profile-only march strings to GCC-compatible ones."""
    march = d.getVar('XUANTIE_MARCH') or ''
    mabi = d.getVar('XUANTIE_MABI') or 'lp64d'
    # If march is already GCC-compatible (starts with rv32/rv64), use as-is
    if march.startswith('rv32') or march.startswith('rv64'):
        return '-march=%s -mabi=%s' % (march, mabi)
    # Profile name like rva23u64 - simplify for GCC
    import re
    if re.match(r'^rv[abm]\d+', march):
        if '64' in march:
            return '-march=rv64gc -mabi=%s' % mabi
        else:
            return '-march=rv32gc -mabi=%s' % mabi
    # Default fallback
    return '-march=rv64gc -mabi=%s' % mabi

def _xuantie_gcc_multidir(d, gcc_args):
    """Low-level: run GCC with gcc_args + -print-multi-directory."""
    import subprocess, os
    tc_gcc = d.getVar('EXTERNAL_TOOLCHAIN_GCC')
    target_sys = d.getVar('EXTERNAL_TARGET_SYS')
    if not tc_gcc or not target_sys:
        return None
    gcc = os.path.join(tc_gcc, 'bin', target_sys + '-gcc')
    if not os.path.isfile(gcc):
        return None
    # Strip clang-only flags that GCC ignores but prints warnings for.
    clean_args = [a for a in gcc_args
                  if a not in ('-menable-experimental-extensions',
                               '-Qunused-arguments', '-mlittle-endian')]
    # Try original args first (XuanTie GCC accepts profile names like rva23u64
    # and returns the precise multilib directory).
    try:
        result = subprocess.check_output(
            [gcc] + clean_args + ['-print-multi-directory'],
            stderr=subprocess.DEVNULL, text=True).strip()
        return result
    except Exception:
        pass
    # Fallback: sanitize profile names for riscv-gnu GCC which does not
    # accept them.
    sanitized_args = _xuantie_sanitize_gcc_args(clean_args)
    if sanitized_args != clean_args:
        try:
            result = subprocess.check_output(
                [gcc] + sanitized_args + ['-print-multi-directory'],
                stderr=subprocess.DEVNULL, text=True).strip()
            return result
        except Exception:
            pass
    return None

def get_xuantie_multilib_dir(d):
    """Query GCC -print-multi-directory with XUANTIE_TUNE_CCARGS.
    When mcpu returns '.' (default), re-query with -march/-mabi to get
    the precise multilib directory (e.g., xt-c930v-cp mcpu maps to '.'
    but rva23u64 march maps to lib64/lp64d)."""
    tune_ccargs = d.getVar('XUANTIE_TUNE_CCARGS')
    if not tune_ccargs:
        return None
    result = _xuantie_gcc_multidir(d, tune_ccargs.split())
    if result == '.' and '-mcpu=' in tune_ccargs:
        march = d.getVar('XUANTIE_MARCH')
        mabi = d.getVar('XUANTIE_MABI')
        if march and mabi:
            march_result = _xuantie_gcc_multidir(d, ['-march=' + march, '-mabi=' + mabi])
            if march_result is not None:
                return march_result
    return result

def get_xuantie_lib_dir_name(d):
    """Auto-detect EXTERNAL_TOOLCHAIN_LIB_DIR_NAME from GCC multilib."""
    multidir = get_xuantie_multilib_dir(d)
    if multidir is None:
        return _xuantie_lib_dir_fallback(d)
    if multidir == '.':
        # GCC default multilib is xthead-based in XuanTie toolchain, but
        # non-xthead march values also fall here when GCC normalization
        # prevents an exact multilib match (e.g. rv64gc → rv64imafdc_zicsr_…).
        # Use the fallback heuristic which checks the actual march for xtheadc.
        return _xuantie_lib_dir_fallback(d)
    return multidir.split('/')[0]

def _xuantie_lib_dir_fallback(d):
    """Fallback when GCC is not callable: derive from tune args."""
    tune = d.getVar('XUANTIE_TUNE_CCARGS') or ''
    march = d.getVar('XUANTIE_MARCH') or ''
    is_rv32 = (d.getVar('TUNE_ARCH') == 'riscv32')
    # mcpu mode: all XuanTie CPUs use xthead libs
    if '-mcpu=' in tune:
        # Re-check march: if march lacks xtheadc, use non-xthead lib
        if 'xtheadc' not in march and march:
            return 'lib32' if is_rv32 else 'lib64'
        return 'lib32xthead' if is_rv32 else 'lib64xthead'
    # march mode: check for xtheadc extension
    if 'xtheadc' in march:
        return 'lib32xthead' if is_rv32 else 'lib64xthead'
    elif is_rv32:
        return 'lib32'
    else:
        return 'lib64'

EXTERNAL_TOOLCHAIN_LIB_DIR_NAME ?= "${@get_xuantie_lib_dir_name(d)}"
EXTERNAL_TOOLCHAIN_ABI_DIR_NAME ?= "${XUANTIE_MABI}"
EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT ?= "lib64xthead"
EXTERNAL_TOOLCHAIN_ABI_DIR_NAME_DEFAULT ?= "lp64d"

# Multilib sysroot path mapping: the external toolchain stores libraries in
# multilib subdirectories (e.g., /usr/lib64/lp64d/) instead of /usr/lib/.
# These premirrors let meta-external-toolchain find the correct files.
FILES_PREMIRRORS:prepend = "\
    ${libdir}/|/usr/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}/\n \
    ${base_libdir}/|/${EXTERNAL_TOOLCHAIN_LIB_DIR_NAME}/${EXTERNAL_TOOLCHAIN_ABI_DIR_NAME}/\n \
"

# mirrors
XUANTIE_GIT ?= "git://git@gitee.com/xuantie-yocto"
MIRRORS += "\
${XUANTIE_GIT} git://git@github.com/XUANTIE-RV \
${XUANTIE_GIT} git://git@github.com/ruyisdk/ \
"

XUANTIE_GITSM ?= "gitsm://git@gitee.com/xuantie-yocto"
MIRRORS += "\
${XUANTIE_GITSM} gitsm://git@github.com/XUANTIE-RV \
${XUANTIE_GITSM} gitsm://git@github.com/ruyisdk/ \
"
PREFERRED_VERSION_opencv ?= "4.12.0"
PREFERRED_VERSION_libvirt ?= "v11.1.0+git"
PREFERRED_VERSION_openmpbench ?= "3.1"
PREFERRED_VERSION_openblas ?= "0.3.30"
# PREFERRED_VERSION_sysbench ?= "0.5"
PREFERRED_VERSION_sysbench ?= "1.0.20"
PREFERRED_VERSION_bcc ?= "0.35.0"
# PREFERRED_VERSION_ffmpeg ?= "7.0"
PREFERRED_VERSION_ffmpeg ?= "7.1"
# meta-xuantie backport of meta-webserver master recipe; meta-webserver's
# nginx 1.21.1 is already disabled via DEFAULT_PREFERENCE="-1" upstream.
PREFERRED_VERSION_nginx = "1.30.4"

## according to arch to select version
## 1.1 for prebuild; 1.2 for source code
PREFERRED_VERSION_spec2006 ?= "1.2"
PREFERRED_VERSION_spec2017 ?= "1.0.6"
PREFERRED_VERSION_lmbench ?= "3.0-a9"
# 8.4.11 for source build; 8.4.3 for prebuilt binary
PREFERRED_VERSION_mysql ?= "8.4.11"
# meta-xuantie ceph 18.2.1 over meta-virtualization ceph 15.2.15
PREFERRED_VERSION_ceph ?= "18.2.1"

# build static librarie
DISABLE_STATIC:pn-libarchive = ""
DISABLE_STATIC:pn-xz = ""
DISABLE_STATIC:pn-elfutils = ""
DISABLE_STATIC:pn-libtirpc = ""
DISABLE_STATIC:pn-libunwind = ""
DISABLE_STATIC:pn-cunit = ""
DISABLE_STATIC:pn-flex = ""
DISABLE_STATIC:pn-libxcrypt = ""

# DISTRO_FEATURES:append:libc-musl = " xtstring-opt"

LD_LIBXTSTRING ?= ""
LD_LIBXTSTRING:libc-musl = "${@bb.utils.contains('DISTRO_FEATURES', 'xtstring-opt', '-lXTString', '', d)}"

ROOT_HOME ?= "/root"

# LIBC bridge: environment variable -> internal XUANTIE_TCLIBC
# Usage: LIBC=muslc DISTRO=c907fdv bitbake ...
LIBC ?= "glibc"
XUANTIE_TCLIBC ?= "${LIBC}"
require xuantie-libc-${XUANTIE_TCLIBC}.inc

# kernel bridge: environment variable -> kernel version + config settings
# Usage: KERNEL=6.6-default DISTRO=c907fdv bitbake ...
# Valid values: 6.6-default, 6.6-debug, 5.10-default, 5.10-debug
# KERNEL value is decomposed into version (6.6/5.10) and config (default/debug),
# version-specific settings are applied via Python conditionals.
KERNEL ?= "6.6-default"
XUANTIE_KERNEL ?= "${KERNEL}"
XUANTIE_KERNEL_VER = "${@d.getVar('XUANTIE_KERNEL').rsplit('-', 1)[0]}"
XUANTIE_KERNEL_CFG = "${@d.getVar('XUANTIE_KERNEL').rsplit('-', 1)[-1]}"

# Add kernel version to OVERRIDES for conditional variable resolution
# This enables EXTERNAL_TOOLCHAIN_GCC:kernel-5.10 to work
OVERRIDES:append = "${@':kernel-' + d.getVar('XUANTIE_KERNEL_VER') if d.getVar('XUANTIE_KERNEL_VER') in ('5.10', '6.6') else ''}"

# Map TOOLCHAIN variable to OVERRIDES for conditional override selection
# This enables :toolchain-clang or :toolchain-gcc conditional overrides in bbclasses
OVERRIDES:append = "${@':toolchain-' + d.getVar('TOOLCHAIN') if d.getVar('TOOLCHAIN') in ('gcc', 'clang') else ''}"

def _xuantie_kernel_settings(d):
    """Apply kernel-version-specific settings based on XUANTIE_KERNEL_VER."""
    ver = d.getVar('XUANTIE_KERNEL_VER') or '6.6'
    d.setVar('XUANTIE_KERNEL_VERSION', 'kernel_' + ver)
    if ver == '5.10':
        d.setVar('SERIAL_CONSOLES', '115200;ttyS0')
    elif ver == '6.6':
        d.setVar('SERIAL_CONSOLES', '0;console')

# PREFERRED_VERSION must be set at parse time (not in python block) to take effect
PREFERRED_VERSION_linux-xuantie = "${@'5.10.y' if d.getVar('XUANTIE_KERNEL_VER') == '5.10' else '6.6.y'}"

MACHINE_FEATURES += "kc${XUANTIE_KERNEL_CFG}"

# TOOLCHAIN_VENDOR bridge: switch between xuantie and open-source toolchains
# Usage: TOOLCHAIN=riscv-gcc DISTRO=rv64-k6.6-debug bitbake ...
#   (oe-buildenv-internal decomposes: riscv-gcc → TOOLCHAIN=gcc TOOLCHAIN_VENDOR=riscv-gnu)
# Composite values: xuantie-gcc, xuantie-llvm, riscv-gcc, riscv-llvm
# Direct usage also supported: TOOLCHAIN_VENDOR=riscv-gnu TOOLCHAIN=gcc
TOOLCHAIN_VENDOR ?= "xuantie"

# Force task-signature change when toolchain identity flips, so sstate is
# correctly invalidated. Without these vardeps, switching TOOLCHAIN_VENDOR
# between 'xuantie' and 'riscv-gnu' (or TOOLCHAIN between gcc/clang, or
# LIBC between glibc/muslc) does not perturb the do_compile/do_install
# signatures of most recipes — the compile flags and EXTERNAL_TOOLCHAIN
# path differ, but the path is intentionally excluded from the signature
# in meta-external-toolchain/classes/external-toolchain.bbclass to avoid
# spurious rebuilds. The side effect is that stale build artefacts from
# the previous toolchain (e.g. XuanTie-clang non-PIC .o objects that
# break open-source clang's PIE-by-default link) survive into the new
# build. See hello recipe's history for the original symptom.
do_configure[vardeps] += "TOOLCHAIN_VENDOR TOOLCHAIN LIBC"
do_compile[vardeps]   += "TOOLCHAIN_VENDOR TOOLCHAIN LIBC"
do_install[vardeps]   += "TOOLCHAIN_VENDOR TOOLCHAIN LIBC"
XUANTIE_VENDOR ?= "${TOOLCHAIN_VENDOR}"

# Helper function to calculate EXTERNAL_TOOLCHAIN path based on vendor, LIBC, and arch
def _xuantie_get_toolchain_path(d):
    """Calculate EXTERNAL_TOOLCHAIN path based on TOOLCHAIN_VENDOR, TOOLCHAIN, LIBC, and TUNE_ARCH."""
    topdir = d.getVar('TOPDIR') or ''
    vendor = d.getVar('TOOLCHAIN_VENDOR') or 'xuantie'
    toolchain = d.getVar('TOOLCHAIN') or 'gcc'
    libc = d.getVar('LIBC') or 'glibc'
    tune_arch = d.getVar('TUNE_ARCH') or 'riscv64'
    
    is_clang = (toolchain == 'clang')
    compiler = 'clang' if is_clang else 'gcc'
    
    if vendor == 'riscv-gnu':
        # Open-source GCC and Clang toolchains both use per-variant directories
        # (libc + bits split): riscv-toolchain-{gcc,clang}-{glibc,muslc,uclibc-ng}{32,64}.
        is_rv32 = (tune_arch == 'riscv32')
        bits = '32' if is_rv32 else '64'

        libc_map = {
            'glibc': 'glibc',
            'muslc': 'muslc',
            'uclibc-ng': 'uclibc-ng',
        }
        libc_suffix = libc_map.get(libc, 'glibc')
        tc_dir = f"riscv-toolchain-{compiler}-{libc_suffix}{bits}"
        return f"{topdir}/../../{tc_dir}"
    else:
        # XuanTie toolchain: single directory per compiler type
        if is_clang:
            return f"{topdir}/../../xuantie-toolchain-clang"
        return f"{topdir}/../../xuantie-toolchain-gcc"

require xuantie-vendor-${XUANTIE_VENDOR}.inc

# Decide whether DISTRO is safe to use with the open-source GNU toolchain.
# Returns True for: *-gnu (vendor extensions stripped via GNU_BLOCKLIST),
# custom-* (user-edited -march), pure standard ISA distros, and RVA/RVB
# profile distros. Anything else (CPU-named distros like c906fdv, *-wo-xthead
# variants that may retain non-xthead vendor extensions) returns False.
def _xuantie_distro_is_gnu_compatible(distro):
    import re
    if not distro:
        return False
    if distro.endswith('-gnu'):
        return True
    if distro.startswith('custom-'):
        return True
    if re.fullmatch(r'rv(32|64)(gc|gcv|imac)', distro):
        return True
    if re.fullmatch(r'rv[ab]\d+u\d+(-k\d+\.\d+-(default|debug))?', distro):
        return True
    return False

python () {
    import os
    topdir = d.getVar('YOCTOROOT') or d.getVar('TOPDIR') or ''

    # Apply kernel version settings
    _xuantie_kernel_settings(d)

    libc = d.getVar('LIBC')
    if libc and libc not in ('glibc', 'muslc', 'uclibc-ng'):
        bb.fatal("Invalid LIBC='%s'. Valid values: glibc, muslc, uclibc-ng" % libc)

    kernel_val = d.getVar('XUANTIE_KERNEL')
    if kernel_val:
        valid = ('6.6-default', '6.6-debug', '5.10-default', '5.10-debug')
        if kernel_val not in valid:
            bb.fatal("Invalid kernel='%s'. Valid values: %s" % (kernel_val, ', '.join(valid)))

    tc = d.getVar('TOOLCHAIN')
    if tc and tc not in ('gcc', 'clang'):
        bb.fatal("Invalid TOOLCHAIN='%s'. Valid values: gcc, clang" % tc)
    vendor = d.getVar('TOOLCHAIN_VENDOR')
    if vendor and vendor not in ('xuantie', 'riscv-gnu'):
        bb.fatal("Invalid TOOLCHAIN_VENDOR='%s'. Valid values: xuantie, riscv-gnu" % vendor)
    
    # XuanTie toolchain does not support muslc + clang combination
    # Open-source toolchain (riscv-gnu) supports muslc + clang
    if libc == 'muslc' and tc == 'clang' and vendor == 'xuantie':
        raise bb.parse.SkipRecipe("XuanTie toolchain: LIBC=muslc + TOOLCHAIN=clang is not supported. Use TOOLCHAIN_VENDOR=riscv-gnu for muslc+clang support.")

    # When using the open-source GNU toolchain, refuse DISTROs whose -march
    # contains XuanTie-only extensions. Fail fast with an actionable hint
    # instead of letting recipes break later on `unknown extension xthead*`.
    if vendor == 'riscv-gnu':
        distro = d.getVar('DISTRO') or ''
        if not _xuantie_distro_is_gnu_compatible(distro):
            arch_hint = 'rv64' if (d.getVar('TUNE_ARCH') or 'riscv64') == 'riscv64' else 'rv32'
            bb.fatal(
                "TOOLCHAIN_VENDOR=riscv-gnu is incompatible with DISTRO=%s.\n"
                "Open-source GCC/Clang cannot consume XuanTie-only -march "
                "extensions (xthead*, xvqdotq, smmtt, ...).\n"
                "Pick one of:\n"
                "  DISTRO=%s-gnu        (auto-stripped vendor extensions)\n"
                "  DISTRO=custom-%s     (edit conf/distro/custom-%s.conf to set -march/-mabi)\n"
                "  DISTRO=rv64gc | rv32gc | rva22u64 | rvb22u64 | ...\n"
                "                       (pure standard ISA / RVA·RVB profile)\n"
                "Note: rva23u64/rvb23u64 may not be supported by the bundled "
                "open-source GCC version; if it fails, switch to %s-gnu or custom-%s."
                % (distro, distro, arch_hint, arch_hint, distro, arch_hint)
            )

    # Toolchain vendor mismatch detection: verify downloaded toolchain matches selected vendor.
    # Open-source toolchain directories (per-variant).
    opensource_dirs = [
        'riscv-toolchain-gcc-glibc64', 'riscv-toolchain-gcc-glibc32',
        'riscv-toolchain-gcc-muslc64', 'riscv-toolchain-gcc-muslc32',
        'riscv-toolchain-gcc-uclibc-ng64', 'riscv-toolchain-gcc-uclibc-ng32',
        'riscv-toolchain-clang-glibc64', 'riscv-toolchain-clang-glibc32',
        'riscv-toolchain-clang-muslc64', 'riscv-toolchain-clang-muslc32',
        'riscv-toolchain-clang-uclibc-ng64', 'riscv-toolchain-clang-uclibc-ng32',
    ]
    xuantie_dirs = ['xuantie-toolchain-gcc', 'xuantie-toolchain-clang']

    has_opensource = any(os.path.isdir(os.path.join(topdir, dirname)) for dirname in opensource_dirs)
    has_xuantie = any(os.path.isdir(os.path.join(topdir, dirname)) for dirname in xuantie_dirs)

    if vendor == 'riscv-gnu' and not has_opensource:
        if has_xuantie:
            bb.fatal("Toolchain vendor mismatch: downloaded XuanTie toolchain but TOOLCHAIN_VENDOR=riscv-gnu. "
                     "Run: bash meta-xuantie/scripts/toolchain-init.sh --vendor riscv-gnu")
        else:
            bb.fatal("Open-source toolchain not found. "
                     "Run: bash meta-xuantie/scripts/toolchain-init.sh --vendor riscv-gnu")

    if vendor == 'xuantie' and not has_xuantie:
        if has_opensource:
            bb.fatal("Toolchain vendor mismatch: downloaded open-source toolchain but TOOLCHAIN_VENDOR=xuantie. "
                     "Run: bash meta-xuantie/scripts/toolchain-init.sh --vendor xuantie")
        else:
            bb.fatal("XuanTie toolchain not found. "
                     "Run: bash meta-xuantie/scripts/toolchain-init.sh --vendor xuantie")
}

# One-shot summary banner — fires once per build at conf-parse time, replacing
# the per-recipe bb.warn that previously spammed thousands of lines.
addhandler xuantie_config_banner
xuantie_config_banner[eventmask] = "bb.event.ConfigParsed"
python xuantie_config_banner () {
    distro = d.getVar('DISTRO') or '<unset>'
    vendor = d.getVar('TOOLCHAIN_VENDOR') or 'xuantie'
    tc     = d.getVar('TOOLCHAIN') or 'gcc'
    libc   = d.getVar('LIBC') or 'glibc'
    tag    = 'XuanTie' if vendor == 'xuantie' else 'open-source GNU'
    bb.plain("=== XuanTie build: DISTRO=%s  TOOLCHAIN=%s  VENDOR=%s (%s)  LIBC=%s ==="
             % (distro, tc, vendor, tag, libc))

    # EXTERNAL_TOOLCHAIN_LIB_DIR_NAME: anonymous blocks (which set the real
    # XUANTIE_MCPU in passthrough.inc) run AFTER ConfigParsed handlers.
    # The ?= default was evaluated at parse time with stale XUANTIE_MCPU
    # (c9xx-riscv64 from rv64.inc).  Re-evaluate for CPU mode distros using
    # the DISTRO-derived CPU model before displaying the audit banner.
    import re
    if re.match(r'^(xt-)?(c|r)\d+', distro):
        is_wo_xthead = distro.endswith('-wo-xthead')
        cpu_model = distro[:-len('-wo-xthead')] if is_wo_xthead else distro
        is_rv32 = cpu_model.endswith('-rv32')
        if is_wo_xthead:
            d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME',
                     'lib32' if is_rv32 else 'lib64')
        else:
            tune = d.getVar('XUANTIE_TUNE_CCARGS') or ''
            if '-mcpu=' in tune:
                tune = tune.replace('${XUANTIE_MCPU}', cpu_model)
            else:
                tune = '-mcpu=' + cpu_model
            multidir = _xuantie_gcc_multidir(d, tune.split())
            if multidir and multidir != '.':
                d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME',
                         multidir.split('/')[0])
            else:
                d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME',
                         'lib32' if is_rv32 else 'lib64')

    # Toolchain selection transparency: 显示 multi-override 链解析后的最终 EXTERNAL_TOOLCHAIN_* 值
    # 替代手动跑 bitbake -e 排查 override 优先级问题.
    # 矩阵期望见 docs/toolchain-matrix.md, 审计脚本见 meta-xuantie/scripts/validate-toolchain-matrix.sh.
    # Determine tune mode from passthrough mode
    tune_mode = _xuantie_passthrough_mode(distro) or 'unknown'
    # cpu-mode distros with XUANTIE_USE_MARCH_MODE=1 actually use -march
    # (toolchain doesn't support the CPU)
    if tune_mode == 'cpu' and d.getVar('XUANTIE_USE_MARCH_MODE') == '1':
        tune_mode = 'arch'

    bb.plain("[xuantie-toolchain-audit] arch=%s kernel=%s mode=%s\n"
             "                          → EXTERNAL_TOOLCHAIN_GCC   = %s\n"
             "                          → EXTERNAL_TOOLCHAIN_CLANG = %s\n"
             "                          → EXTERNAL_TARGET_SYS      = %s\n"
             "                          → EXTERNAL_TOOLCHAIN_LIB_DIR_NAME = %s" % (
        d.getVar('TARGET_ARCH') or '<unset>',
        d.getVar('XUANTIE_KERNEL_VER') or '<unset>',
        tune_mode,
        d.getVar('EXTERNAL_TOOLCHAIN_GCC') or '<unset>',
        d.getVar('EXTERNAL_TOOLCHAIN_CLANG') or '<unset>',
        d.getVar('EXTERNAL_TARGET_SYS') or '<unset>',
        d.getVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME') or '<unset>',
    ))
}

require xuantie-proprietary.inc

require xuantie-proprietary.inc

require xuantie-proprietary.inc
