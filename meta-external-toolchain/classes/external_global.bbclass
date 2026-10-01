def fixed_oe_import(d, modules=None):
    import importlib
    import sys

    def inject(name, value):
        """Make a python object accessible from the metadata"""
        if hasattr(bb.utils, "_context"):
            bb.utils._context[name] = value
        else:
            __builtins__[name] = value

    bbpath = d.getVar("BBPATH").split(":")
    layerpaths = [os.path.join(dir, "lib") for dir in bbpath]
    sys.path[0:0] = layerpaths

    if modules is None:
        import oe.data
        modules = oe.data.typed_value("OE_IMPORTS", d)

    has_reloaded = set()
    for toimport in modules:
        # If we're importing something in a namespace package, and it's
        # already been imported, reload it, to ensure any namespace package
        # extensions to __path__ are picked up
        imp_project = toimport
        while True:
            try:
                imp_project, _ = imp_project.rsplit(".", 1)
            except ValueError:
                break
            if imp_project in sys.modules and imp_project not in has_reloaded:
                mod = sys.modules[imp_project]
                if hasattr(mod, '__path__'):
                    bb.debug(1, 'Reloading %s' % imp_project)
                    importlib.reload(mod)
                    has_reloaded.add(imp_project)

        project = toimport.split(".", 1)[0]
        imported = importlib.import_module(toimport)
        sys.modules[toimport] = imported
        inject(project, sys.modules[project])
        bb.debug(1, 'Imported and injected %s' % toimport)

    return ""

EXTERNAL_IMPORTED := "${@fixed_oe_import(d, ['oe.external'])}"

# RISC-V profile names (e.g., -march=rva23u64) are accepted by Clang but NOT
# by riscv-gnu GCC (15.2.0), which only understands ISA strings starting with
# 'rv32' or 'rv64'. The GCC queries below (-print-sysroot, -print-file-name,
# -print-sysroot-headers-suffix) only depend on the base arch (rv32/rv64) and
# ABI -- the exact extension list does not affect the result. So translate any
# profile-name -march to a simple rv32gc/rv64gc equivalent before invoking GCC.
# Actual compilation flags (TARGET_CC_ARCH) are left untouched.
def _sanitize_cc_arch_for_gcc(cc_arch_str):
    import re
    args = (cc_arch_str or '').split()
    result = []
    for arg in args:
        if arg.startswith('-march='):
            march_val = arg[len('-march='):]
            if not march_val.startswith('rv32') and not march_val.startswith('rv64'):
                if re.match(r'^rv[abm]\d+u(32|64)$', march_val):
                    if 'u64' in march_val:
                        arg = '-march=rv64gc'
                    else:
                        arg = '-march=rv32gc'
        result.append(arg)
    return result

EXTERNAL_TOOLCHAIN_SYSROOT ?= "${@external_run(d, '${EXTERNAL_CC}', *(_sanitize_cc_arch_for_gcc(d.getVar('TARGET_CC_ARCH')) + ['-print-sysroot'])).rstrip()}"
EXTERNAL_TOOLCHAIN_LIBROOT ?= "${@external_run(d, '${EXTERNAL_CC}', *(_sanitize_cc_arch_for_gcc(d.getVar('TARGET_CC_ARCH')) + ['-print-file-name=crtbegin.o'])).rstrip().replace('/crtbegin.o', '')}"
EXTERNAL_HEADERS_MULTILIB_SUFFIX ?= "${@external_run(d, '${EXTERNAL_CC}', *(_sanitize_cc_arch_for_gcc(d.getVar('TARGET_CC_ARCH')) + ['-print-sysroot-headers-suffix'])).rstrip()}"
EXTERNAL_LIBC_KERNEL_VERSION ?= "${@external_get_kernel_version(d, "${EXTERNAL_TOOLCHAIN_SYSROOT}${prefix}")}"

EXTERNAL_INSTALL_SOURCE_PATHS = "\
    ${EXTERNAL_TOOLCHAIN_SYSROOT} \
    ${EXTERNAL_TOOLCHAIN}/${EXTERNAL_TARGET_SYS} \
    ${EXTERNAL_TOOLCHAIN_SYSROOT}/.. \
    ${EXTERNAL_TOOLCHAIN} \
    ${D} \
"

# Potential locations within the external toolchain sysroot
FILES_PREMIRRORS = "\
    ${bindir}/|/usr/${baselib}/bin/\n \
"

FILES_MIRRORS = "\
    ${base_libdir}/|/usr/${baselib}/\n \
    ${libexecdir}/|/usr/libexec/\n \
    ${libexecdir}/|/usr/${baselib}/${PN}\n \
    ${mandir}/|/usr/share/man/\n \
    ${mandir}/|/usr/man/\n \
    ${mandir}/|/man/\n \
    ${mandir}/|/share/doc/*-${EXTERNAL_TARGET_SYS}/man/\n \
    ${prefix}/|${base_prefix}/\n \
"

EXTERNAL_CC ?= "${EXTERNAL_TARGET_SYS}-gcc"

def external_run(d, *args):
    """Convenience wrapper"""
    if (not d.getVar('TCMODE', True).startswith('external') or
            not d.getVar('EXTERNAL_TOOLCHAIN', True)):
        return 'UNKNOWN'

    sys.path.append(os.path.join(d.getVar('LAYERDIR_external-toolchain', True), 'lib'))
    import oe.external
    return oe.external.run(d, *args)

external_run[vardepsexclude] += "LAYERDIR_external-toolchain"

def external_get_kernel_version(d, p):
    if (not d.getVar('TCMODE', True).startswith('external') or
            not d.getVar('EXTERNAL_TOOLCHAIN', True)):
        return 'UNKNOWN'

    import re
    for fn in ['include/linux/utsrelease.h', 'include/generated/utsrelease.h',
               'include/linux/version.h']:
        fn = os.path.join(p, fn)
        if os.path.exists(fn):
            break
    else:
        return ''

    try:
        f = open(fn)
    except IOError:
        pass
    else:
        with f:
            lines = f.readlines()

        for line in lines:
            m = re.match(r'#define LINUX_VERSION_CODE (\d+)$', line)
            if m:
                code = int(m.group(1))
                a = code >> 16
                b = (code >> 8) & 0xFF
                return '%d.%d' % (a, b)

    bb.debug(1, 'external_global.bbclass: failed to find kernel version header in {}'.format(p))
    return ''
