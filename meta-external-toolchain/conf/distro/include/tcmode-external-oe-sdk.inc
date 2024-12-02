require conf/distro/include/tcmode-external.inc

# OE-built toolchains default to this, so don't pass it explicitly
TARGET_LDFLAGS_BUILD_ID = ""

# OE-built toolchains assume en_US is utf8
EXTERNAL_TOOLCHAIN_FEATURES_DEFAULT ??= "locale-utf8-is-default"

TCMODEOVERRIDES .= ":tcmode-external-oe-sdk"

EXTERNAL_TARGET_SYS ?= "${TARGET_SYS}"

EXTERNAL_SDK_SYS ?= "${SDK_SYS}"
EXTERNAL_TOOLCHAIN_BIN = "${EXTERNAL_TOOLCHAIN}/sysroots/${EXTERNAL_SDK_SYS}/usr/bin/${EXTERNAL_TARGET_SYS}"
EXTERNAL_MULTIMACH_TARGET_SYS ?= "${MULTIMACH_TARGET_SYS}"
EXTERNAL_TOOLCHAIN_SYSROOT = "${EXTERNAL_TOOLCHAIN}/sysroots/${EXTERNAL_MULTIMACH_TARGET_SYS}"

EXTERNAL_CC = "${EXTERNAL_TARGET_SYS}-gcc --sysroot=${EXTERNAL_TOOLCHAIN_SYSROOT}"
EXTERNAL_TOOLCHAIN_FEATURES:append = " locale-utf8-is-default"

EXTERNAL_SETUP_SCRIPT_VARS ?= ""

# Our headers are already multilib
oe_multilib_header:pn-glibc-external = ":"

EXTERNAL_TOOLCHAINS ??= "${EXTERNAL_TOOLCHAIN}"

python extract_env_setup_metadata() {
    from pathlib import Path

    paths = [Path(p) for p in d.getVar('EXTERNAL_TOOLCHAINS').split()]
    paths = [p for p in paths if p.is_absolute() and p.is_dir()]
    if paths:
        setup_external_vars(paths, d, d)

        localdata = bb.data.createCopy(d)
        variants = d.getVar('MULTILIB_VARIANTS') or ''
        for item in variants.split():
            # Load overrides from 'd' to avoid having to reset the value...
            overrides = d.getVar('OVERRIDES', False) + ':virtclass-multilib-' + item
            localdata.setVar('OVERRIDES', overrides)
            localdata.setVar('MLPREFIX', item + '-')
            setup_external_vars(paths, localdata, d)
}
extract_env_setup_metadata[eventmask] = "bb.event.ConfigParsed"
addhandler extract_env_setup_metadata

def setup_external_vars(paths, localdata, d):
    from pathlib import Path

    setup, env = get_setup_script_env(paths, localdata)

    mlprefix = localdata.getVar('MLPREFIX')
    if mlprefix:
        suffix = '_virtclass-multilib-' + mlprefix[:-1]
    else:
        suffix = ''

    d.setVar('EXTERNAL_TOOLCHAIN' + suffix, str(setup.parent))

    for var in localdata.getVar('EXTERNAL_SETUP_SCRIPT_VARS').split():
        d.setVar('EXTERNAL_' + var + suffix, env.get(var) or '')

    target_sys = env.get('TARGET_PREFIX')[:-1]
    native_sysroot = Path(env.get('OECORE_NATIVE_SYSROOT'))
    if str(native_sysroot).startswith('$scriptdir/'):
        native_sysroot = setup.parent / native_sysroot.relative_to('$scriptdir')
    target_sysroot = Path(env.get('SDKTARGETSYSROOT'))
    if str(target_sysroot).startswith('$scriptdir/'):
        target_sysroot = setup.parent / target_sysroot.relative_to('$scriptdir')

    d.setVar('EXTERNAL_TARGET_SYS' + suffix, str(target_sys))
    d.setVar('EXTERNAL_TOOLCHAIN_BIN' + suffix, str(native_sysroot / 'usr' / 'bin' / target_sys))
    d.setVar('EXTERNAL_TOOLCHAIN_SYSROOT' + suffix, str(target_sysroot))

# These are treated as prefixes to items in PACKAGE_ARCHS at this time.
#
# An armv7-a sysroot will run fine with armv7ve machines, as the latter just
# have virtualization extensions. We need to handle this sort of thing
# directly in this case, as for some reason the armv7-a tunes aren't listed in
# the armv7ve PACKAGE_ARCHS.
EXTERNAL_TOOLCHAIN_ARCH_COMPAT[armv7ve] += "armv7a"

def select_appropriate_setup_script(d, paths):
    from itertools import chain
    from pathlib import Path

    candidates = []

    tune_pkgarch = d.getVar('TUNE_PKGARCH')
    arch_setups = list(chain.from_iterable(Path(path).glob('environment-setup-' + tune_pkgarch + '-*') for path in paths))
    if arch_setups:
        # Exact match, skip processing
        for setup in arch_setups:
            setup_env = parse_setup_script(setup)
            candidates.append((setup, setup_env))
        return candidates

    target_arch = d.getVar('TARGET_ARCH')
    target_os = d.getVar('TARGET_OS')
    baselib = d.getVar('baselib')
    package_archs = d.getVar('PACKAGE_ARCHS').split()

    arch_compat = d.getVarFlags('EXTERNAL_TOOLCHAIN_ARCH_COMPAT')
    setups = chain.from_iterable(Path(path).glob('environment-setup-*') for path in paths)
    for setup in setups:
        setup_env = parse_setup_script(setup)
        if target_arch != setup_get(setup_env, 'OECORE_TARGET_ARCH'):
            bb.debug(1, "tcmode-external-oe-sdk: TARGET_ARCH `{}` doesn't match `{}` in `{}`".format(target_arch, setup_get(setup_env, 'OECORE_TARGET_ARCH'), setup))
            continue
        if target_os != setup_get(setup_env, 'OECORE_TARGET_OS'):
            bb.debug(1, "tcmode-external-oe-sdk: TARGET_OS `{}` doesn't match `{}` in `{}`".format(target_os, setup_get(setup_env, 'OECORE_TARGET_OS'), setup))
            continue
        if baselib != setup_get(setup_env, 'OECORE_BASELIB'):
            bb.debug(1, "tcmode-external-oe-sdk: BASELIB `{}` doesn't match `{}` in `{}`".format(baselib, setup_get(setup_env, 'OECORE_BASELIB'), setup))
            continue

        setup_tune_pkgarch = setup_env.get('TUNE_PKGARCH')
        if setup_tune_pkgarch:
            if setup_tune_pkgarch not in package_archs:
                compatible = False
                for from_arch, compat_archs in arch_compat.items():
                    for compat_arch in compat_archs.split():
                        adjusted_archs = [a.replace(from_arch, compat_arch) for a in package_archs]
                        if setup_tune_pkgarch in adjusted_archs:
                            compatible = True

                if not compatible:
                    bb.debug(1, "tcmode-external-oe-sdk: skipping incompatible {}: TUNE_PKGARCH `{}` not found in PACKAGE_ARCHS `{}`".format(setup, setup_tune_pkgarch, package_archs))
                    continue
        candidates.append((setup, setup_env))

    return candidates

def get_setup_script_env(paths, d):
    from pathlib import Path

    setup = d.getVar('EXTERNAL_TOOLCHAIN_SETUP_SCRIPT')
    if setup:
        setup = Path(setup)
        env = parse_setup_script(setup)
    else:
        candidates = select_appropriate_setup_script(d, paths)
        if not candidates:
            setup = None
        elif len(candidates) > 1:
            bb.fatal("tcmode-external-oe-sdk: multiple candidate setup scripts found, please specify with EXTERNAL_TOOLCHAIN_SETUP_SCRIPT: {}".format(" ".join(str(p) for p, e in candidates)))
        else:
            setup, env = candidates[0]
            bb.debug(1, "tcmode-external-oe-sdk: selected setup script {}".format(setup))

    if not setup:
        bb.fatal("tcmode-external-oe-sdk: no compatible toolchain for tuning `{}` found in TOOLCHAIN_PATHS ({})".format(d.getVar("TUNE_PKGARCH"), ", ".join(str(p) for p in paths)))
    return setup, env

def setup_get(setup, field):
    if not field in setup:
        bb.fatal("tcmode-external-oe-sdk: no variable `{}` found in `{}`".format(field, setup))
    return setup[field]

def parse_setup_script(setup):
    import shlex
    import subprocess

    with open(setup, 'r') as f:
        value = f.read()

    values = {}
    for line in value.splitlines():
        if line.split():
            split = shlex.split(line)
            if len(split) == 2 and split[0] == 'export':
                split = split[1:]
            if len(split) == 1:
                try:
                    k, v = split[0].split('=', 1)
                except ValueError:
                    continue
                else:
                    values[k] = v
    return values
