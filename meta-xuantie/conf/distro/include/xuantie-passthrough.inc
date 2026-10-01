# Unified DISTRO passthrough — auto-detects configuration mode from DISTRO name.
#
# Supported modes (auto-detected):
#
# 1. compatible: {prefix}-k{ver}-{cfg}     e.g. rv64-k6.6-debug, rva23u64-k6.6-default
# 2. cpu: CPU model names                  e.g. c906fdv, c908v, c907fdv-rv32, r908
#    New naming: xt-c9501fdvkmt, xt-r920, xt-c908v-rv32
# 3. arch: ISA strings / profiles          e.g. rv64gc, rv32gc, rva23u64, rvb23u64
# 4. custom: custom-{arch}                 e.g. custom-rv64, custom-rva23, custom-rv32
#    (pre-built conf files with explicit CPU+ISA configuration)
#
# Note: tune_arch mode has been removed. Use custom-{arch} instead.

# --- Mode detection ---

def _xuantie_passthrough_mode(distro):
    """Detect passthrough mode from DISTRO name.
    Returns: 'compatible' | 'cpu' | 'arch' | 'custom' | None

    Supports both legacy naming (c906, r908) and new naming (xt-c9501fdvkmt, xt-r920).
    """
    import re
    # 1. compatible mode (arch-kernel)
    if re.search(r'-k\d+\.\d+-(debug|default)', distro):
        return 'compatible'

    # 2. custom mode: custom-{arch}
    #    Pre-built conf files with explicit CPU+ISA configuration
    #    e.g., custom-rv64, custom-rv32, custom-rva23
    if distro.startswith('custom-'):
        return 'custom'

    # 3. cpu mode: C/R series CPU models
    #    Legacy: c906fdv, c908v, r908, c907fdv-rv32
    #    New: xt-c9501fdvkmt, xt-r920, xt-c908v-rv32
    #    With variants: c908-cp_v2, r908-cp-xt, c920v3-cp, c908x-cp-xt
    #    With arch suffix: c907-rv64, r908-cp-rv64, r908fdvk-cp-xt-rv32
    if re.match(r'^(xt-)?(c|r)\d+', distro):
        return 'cpu'

    # 4. arch mode: standard ISA strings or profiles
    if re.match(r'^(rv(32|64)|rva|rvb)', distro):
        return 'arch'

    # Not a recognized mode
    return None

# --- Architecture detection ---

def _xuantie_detect_arch(distro):
    """Detect architecture (rv32 or rv64) from DISTRO name."""
    if distro.startswith('rv32'):
        return 'rv32'
    if distro.endswith('u32'):
        return 'rv32'
    if '-rv32' in distro:
        return 'rv32'
    return 'rv64'

# --- ABI detection ---

def _xuantie_detect_mabi(distro, arch):
    """Detect ABI from DISTRO name based on mode."""
    import re
    base = 'ilp32' if arch == 'rv32' else 'lp64'
    if re.match(r'rv(32|64)[a-z]', distro):
        ext = distro[4:]
        if 'd' in ext or 'g' in ext:
            return base + 'd'
        if 'f' in ext:
            return base + 'f'
        return base
    # Profile or default: all include double-precision float
    return base + 'd'

# --- Simple profile detection ---
# Simple profiles: just set XUANTIE_MARCH, no dedicated .inc file needed.
# Complex profiles (rva23u64, rvb23u64) have their own .inc files with
# additional toolchain and package settings.

def _xuantie_is_simple_profile(distro):
    """Check if DISTRO is a simple profile (no dedicated .inc file)."""
    return distro in ('rva20u32', 'rva20u64', 'rva22u32', 'rva22u64',
                      'rva23u32', 'rvb21u64', 'rvb22u64')

# --- Arch include name ---
# Determines which .inc file to require based on mode.

def _xuantie_arch_include_name(distro):
    """Return the include name for the architecture base."""
    mode = _xuantie_passthrough_mode(distro)
    if mode == 'compatible':
        prefix = _rvk_arch_include(distro)
        # Simple profiles used as arch prefix → resolve to rv32/rv64
        if _xuantie_is_simple_profile(prefix):
            return _xuantie_detect_arch(prefix)
        # rv64/rv32 compatible mode: use XuanTie default ISA (with xtheadc extensions)
        if prefix == 'rv64':
            return 'rv64gc_zfh_xtheadc'
        if prefix == 'rv32':
            return 'rv32gc_zfh_xtheadc'
        return prefix
    if mode == 'arch' and not _xuantie_is_simple_profile(distro):
        # Check if it's a profile with dedicated .inc file
        if distro in ('rva23u64', 'rvb23u64'):
            return distro
    # For arch (ISA strings), cpu, custom, and simple profiles: use rv32 or rv64 directly
    return _xuantie_detect_arch(distro)

# --- Arch-kernel helpers ---

def _rvk_parse_kernel(distro):
    """Extract kernel spec: rv64-k6.6-debug -> '6.6-debug'"""
    import re
    m = re.search(r'-k(\d+\.\d+)-(debug|default)', distro)
    return m.group(1) + '-' + m.group(2) if m else '6.6-default'

def _rvk_arch_include(distro):
    """Extract arch prefix: rv64-k6.6-debug -> 'rv64', rva23u64-k6.6-debug -> 'rva23u64'"""
    import re
    m = re.match(r'([a-z0-9]+)-k\d', distro)
    return m.group(1) if m else 'rv64'

def _rvk_machine_name(distro):
    """Generate backward-compatible MACHINE name from arch-kernel DISTRO."""
    import re
    arch_inc = _rvk_arch_include(distro)
    if arch_inc in ('rv32', 'rv64'):
        bits = arch_inc[2:]
        arch = 'riscv' + bits
    else:
        arch = arch_inc
    km = re.search(r'-k(\d+\.\d+)-(debug|default)', distro)
    if not km:
        return arch + '-tcxt'
    kver, kcfg = km.group(1), km.group(2)
    _kver_tag = {'6.6': 'k6.6ry', '5.10': 'k5.10xt'}
    kver_tag = _kver_tag.get(kver, 'k' + kver)
    return '%s-%s-kc%s-tcxt' % (arch, kver_tag, kcfg)

# --- KERNEL override for compatible mode ---
# Must be set before requiring arch inc (→ base.inc bridge picks it up).
# For other modes, fall through to base.inc's ?= "6.6-default".
KERNEL = "${@_rvk_parse_kernel(d.getVar('DISTRO')) if _xuantie_passthrough_mode(d.getVar('DISTRO')) == 'compatible' else '6.6-default'}"

# --- Architecture base include ---
# Triggers rv64.inc / rv32.inc / rva23u64.inc / rvb23u64.inc → xuantie-base.inc
require conf/distro/include/${@_xuantie_arch_include_name(d.getVar('DISTRO'))}.inc

# --- Mode-specific variable overrides ---
# Use a Python anonymous block to set mode-specific variables.
# This avoids self-reference recursion in BitBake variable expansion.
# The anonymous block runs after all requires/includes are processed,
# so it can safely read and override variables set by rv64.inc / rv32.inc.

XUANTIE_MABI = "${@_xuantie_detect_mabi(d.getVar('DISTRO'), _xuantie_detect_arch(d.getVar('DISTRO')))}"

# --- Clang experimental extensions flag (for recipes using XUANTIE_CLANG_EXP_FLAGS directly) ---
# This declarative variable is kept for backward compatibility with recipes
# like zsb.inc that reference XUANTIE_CLANG_EXP_FLAGS in CFLAGS.
# The actual flag is now included in XUANTIE_TUNE_CCARGS via the python block.
# Applied when TOOLCHAIN=clang and either:
#   - TOOLCHAIN_VENDOR=xuantie (XuanTie clang always enables experimental exts), or
#   - the selected arch profile signals it needs experimental extensions
#     (e.g. rva23u64/rvb23u64 set XUANTIE_ARCH_NEEDS_EXP_EXTENSIONS=1, because
#     extensions like zimop are still experimental in clang regardless of vendor).
XUANTIE_CLANG_EXP_FLAGS = "${@' -menable-experimental-extensions' if d.getVar('TOOLCHAIN') == 'clang' and (d.getVar('TOOLCHAIN_VENDOR') == 'xuantie' or d.getVar('XUANTIE_ARCH_NEEDS_EXP_EXTENSIONS') == '1') else ''}"
python () {
    distro = d.getVar('DISTRO')

    mode = _xuantie_passthrough_mode(distro)
    arch = _xuantie_detect_arch(distro)
    is_rv32 = (arch == 'rv32')

    # Detect clang toolchain — needed for injecting extra flags
    is_clang = d.getVar('TOOLCHAIN') == 'clang'

    # Helper: append clang extra flags to XUANTIE_TUNE_CCARGS when needed.
    # -menable-experimental-extensions is required when:
    #   1. TOOLCHAIN_VENDOR=xuantie (XuanTie clang always needs it), or
    #   2. The arch profile contains experimental extensions in clang
    #      (e.g. rva23u64/rvb23u64 contain zimop which is still experimental).
    #      Such profiles set XUANTIE_ARCH_NEEDS_EXP_EXTENSIONS=1 in their .inc.
    # Open-source clang (riscv-gnu) on plain rv32/rv64 ISA strings does NOT need it.
    def _xuantie_clang_extra_flags():
        """Return extra flags for clang based on vendor and arch profile."""
        if not is_clang:
            return ''
        vendor = d.getVar('TOOLCHAIN_VENDOR') or 'xuantie'
        needs_exp = d.getVar('XUANTIE_ARCH_NEEDS_EXP_EXTENSIONS') == '1'
        if vendor == 'xuantie' or needs_exp:
            return ' -menable-experimental-extensions'
        return ''

    # Helper: calculate mabi from march string
    def _calculate_mabi(march_str):
        base = 'ilp32' if march_str.startswith('rv32') else 'lp64'
        ext = march_str[4:] if march_str.startswith(('rv32', 'rv64')) else ''
        if 'd' in ext or 'g' in ext:
            return base + 'd'
        if 'f' in ext:
            return base + 'f'
        return base

    if mode == 'cpu':
        # CPU mode: parse CPU model and arch from DISTRO
        # Supports both legacy (c906fdv, r908) and new (xt-c9501fdvkmt, xt-r920) naming
        # XuanTie toolchain natively supports -rv32 suffix CPU models
        # e.g., -mcpu=c907fdv-rv32 is valid
        # -wo-xthead suffix means xuantie toolchain without xthead extensions
        
        # Check if distro conf explicitly requests march mode
        # (set by gen-cpu-confs.sh when toolchain doesn't support this CPU)
        use_march_mode = (d.getVar('XUANTIE_USE_MARCH_MODE') or '') == '1'
        
        cpu_model = distro
        is_wo_xthead = False
        if distro.endswith('-wo-xthead'):
            is_wo_xthead = True
            cpu_model = distro[:-len('-wo-xthead')]
        
        if cpu_model.endswith('-rv32'):
            arch = 'rv32'
            is_rv32 = True
        else:
            arch = 'rv64'
            is_rv32 = False

        if use_march_mode:
            # March mode: toolchain doesn't support this CPU, use ISA from mapping
            # XUANTIE_MARCH and XUANTIE_MABI already set in distro conf
            d.setVar('XUANTIE_MCPU', '')
            d.setVar('XUANTIE_MCPU_PACKAGE_ARCH', distro)
            d.setVar('XUANTIE_TUNE_CCARGS_USE_MCPU', 'NO')
            d.setVar('XUANTIE_TUNE_CCARGS', '-march=${XUANTIE_MARCH} -mabi=${XUANTIE_MABI}' + _xuantie_clang_extra_flags())
            d.setVar('XUANTIE_TUNE_CCARGS_GENERIC', '-march=${XUANTIE_MARCH} -mabi=${XUANTIE_MABI}' + _xuantie_clang_extra_flags())

            # Add the PACKAGE_ARCH to PACKAGE_EXTRA_ARCHS so sstate can find
            # manifests for packages with custom PACKAGE_ARCH.
            pkg_archs = distro + ' ' + distro + '_external'
            cur = d.getVar('PACKAGE_EXTRA_ARCHS') or ''
            d.setVar('PACKAGE_EXTRA_ARCHS', cur + ' ' + pkg_archs)
            
            # Determine lib dir based on xtheadc in XUANTIE_MARCH
            march_val = d.getVar('XUANTIE_MARCH') or ''
            if 'xtheadc' in march_val:
                if is_rv32:
                    d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib32xthead')
                else:
                    d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib64xthead')
            else:
                if is_rv32:
                    d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib32')
                else:
                    d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib64')
        else:
            # CPU mode: toolchain supports this CPU
            d.setVar('XUANTIE_MCPU', cpu_model)
            d.setVar('XUANTIE_MARCH', '')  # CPU mode uses -mcpu, no -march
            d.setVar('XUANTIE_TUNE_CCARGS_USE_MCPU', 'YES')
            d.setVar('XUANTIE_TUNE_CCARGS', '-mcpu=${XUANTIE_MCPU}' + _xuantie_clang_extra_flags())
            d.setVar('XUANTIE_TUNE_CCARGS_GENERIC', '-mcpu=${XUANTIE_MCPU}' + _xuantie_clang_extra_flags())
            
            # For xt- prefix CPU models, also add the unprefixed version to PACKAGE_EXTRA_ARCHS
            # e.g., xt-c9501fdvkmt -> add both xt-c9501fdvkmt and c9501fdvkmt
            pkg_archs = '${XUANTIE_MCPU} ${XUANTIE_MCPU}_external'
            if cpu_model.startswith('xt-'):
                legacy_cpu = cpu_model[3:]  # Remove 'xt-' prefix
                pkg_archs += ' ' + legacy_cpu + ' ' + legacy_cpu + '_external'
            
            cur = d.getVar('PACKAGE_EXTRA_ARCHS') or ''
            d.setVar('PACKAGE_EXTRA_ARCHS', cur + ' ' + pkg_archs)

            # multilib: determine library directory based on CPU model variant
            # -cp (without -xt): standard lib (no xthead in runtime)
            # -cp-xt / regular CPU models: libxthead
            is_cp_no_xt = '-cp' in cpu_model and '-cp-xt' not in cpu_model
            if is_wo_xthead or is_cp_no_xt:
                if is_rv32:
                    d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib32')
                else:
                    d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib64')
            else:
                if is_rv32:
                    d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib32xthead')
                else:
                    d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib64xthead')

        # EXTERNAL_TOOLCHAIN_LIB_DIR_NAME was already evaluated at parse time
        # (via ?=) using the stale XUANTIE_MCPU default (c9xx-riscv64 from
        # rv64.inc).  Re-evaluate with the real -mcpu:
        #   Rule 1: GCC-recognized CPU → use -print-multi-directory output
        #   Rule 2: -wo-xthead → always standard lib (lib64/lib32)
        if is_wo_xthead:
            d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME',
                     'lib32' if is_rv32 else 'lib64')
        else:
            multidir = get_xuantie_multilib_dir(d)
            if multidir and multidir != '.':
                d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME', multidir.split('/')[0])
            else:
                d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME',
                         'lib32' if is_rv32 else 'lib64')

    elif mode == 'custom':
        # Custom mode: pre-built conf files with explicit CPU+ISA configuration
        # e.g., custom-rv64, custom-rv32, custom-rva23
        # These conf files already set XUANTIE_TUNE_CCARGS, XUANTIE_MABI, etc.
        # No need to override anything here - let the conf file settings take effect.
        pass

    elif mode == 'arch':
        # ARCH mode: ISA string or profile
        d.setVar('XUANTIE_MARCH', distro)
        d.setVar('XUANTIE_TUNE_CCARGS_USE_MCPU', 'NO')
        mabi = _calculate_mabi(distro)
        d.setVar('XUANTIE_MABI', mabi)
        # Note: -menable-experimental-extensions for rva23u64/rvb23u64 is
        # injected via _xuantie_clang_extra_flags(), driven by the
        # XUANTIE_ARCH_NEEDS_EXP_EXTENSIONS signal set in the corresponding
        # .inc files (rva23u64.inc, rvb23u64.inc inherits it).
        # This works for both TOOLCHAIN_VENDOR=xuantie and =riscv-gnu.
        d.setVar('XUANTIE_TUNE_CCARGS', '-march=${XUANTIE_MARCH} -mabi=${XUANTIE_MABI}' + _xuantie_clang_extra_flags())
        # Set generic tune args for benchmark bbclass compatibility
        d.setVar('XUANTIE_TUNE_CCARGS_GENERIC', d.getVar('XUANTIE_TUNE_CCARGS'))

        # Own PACKAGE_ARCH, same as cpu mode: without this, PACKAGE_ARCH falls
        # back to rv64.inc's XUANTIE_MCPU default (c9xx-riscv64), so an
        # arch-mode build's packages land in the same dir as compatible-mode
        # generic packages — one deb dir then feeds two different tunes.
        d.setVar('XUANTIE_MCPU_PACKAGE_ARCH', distro)
        pkg_archs = distro + ' ' + distro + '_external'
        cur = d.getVar('PACKAGE_EXTRA_ARCHS') or ''
        d.setVar('PACKAGE_EXTRA_ARCHS', cur + ' ' + pkg_archs)

        # multilib: check for any xthead* extensions (xtheadc, xtheadv, xtheadm, etc.)
        has_xthead = any(ext.startswith('xthead') for ext in distro.split('_'))
        if has_xthead:
            if is_rv32:
                d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib32xthead')
            else:
                d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib64xthead')
        else:
            if is_rv32:
                d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib32')
            else:
                d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib64')

    elif mode == 'compatible':
        # compatible mode: arch-kernel combination
        d.setVar('XUANTIE_TUNE_CCARGS_USE_MCPU', 'NO')
        d.setVar('XUANTIE_TUNE_CCARGS', '-march=${XUANTIE_MARCH} -mabi=${XUANTIE_MABI}' + _xuantie_clang_extra_flags())
        d.setVar('XUANTIE_TUNE_CCARGS_GENERIC', '-march=${XUANTIE_MARCH} -mabi=${XUANTIE_MABI}' + _xuantie_clang_extra_flags())
        # For simple profiles used as arch-kernel prefix, override lib dir
        if _xuantie_is_simple_profile(_rvk_arch_include(distro)):
            d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME', 'lib32' if is_rv32 else 'lib64')

    # Vendor/DISTRO compatibility is now enforced once at conf-parse time in
    # xuantie-base.inc (`_xuantie_distro_is_gnu_compatible` + bb.fatal). Per-
    # recipe warnings here would fire thousands of times per build.

    # Softfloat override: CPUs without hardware FPU use soft-float ABI
    if d.getVar('XUANTIE_SOFTFLOAT') == '1':
        if is_rv32:
            d.setVar('XUANTIE_MARCH', 'rv32imac')
            d.setVar('XUANTIE_MABI', 'ilp32')
            d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib32')
        else:
            d.setVar('XUANTIE_MARCH', 'rv64imac')
            d.setVar('XUANTIE_MABI', 'lp64')
            d.setVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT', 'lib64')
        d.setVar('TARGET_FPU', 'soft')
        # Update TUNE_CCARGS for non-cpu/custom modes
        if mode not in ('cpu', 'custom'):
            d.setVar('XUANTIE_TUNE_CCARGS', '-march=${XUANTIE_MARCH} -mabi=${XUANTIE_MABI}' + _xuantie_clang_extra_flags())
}

# --- MACHINE naming ---

def _xuantie_machine_name(distro):
    mode = _xuantie_passthrough_mode(distro)
    if mode == 'compatible':
        return _rvk_machine_name(distro)
    if mode == 'cpu':
        return distro + ('-tcxt' if '-rv32' in distro else '-rv64-tcxt')
    if mode == 'arch':
        arch = _xuantie_detect_arch(distro)
        return distro + '-' + arch + '-tcxt'
    if mode == 'custom':
        # custom mode: use distro name directly
        return distro + '-rv64-tcxt'
    return distro + '-rv64-tcxt'

MACHINE ?= "${@_xuantie_machine_name(d.getVar('DISTRO'))}"

DEFAULTTUNE ?= "riscv64"
