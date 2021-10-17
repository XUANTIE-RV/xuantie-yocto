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

EXTERNAL_TOOLCHAIN_SYSROOT ?= "${@external_run(d, '${EXTERNAL_CC}', *(TARGET_CC_ARCH.split() + ['-print-sysroot'])).rstrip()}"
EXTERNAL_TOOLCHAIN_LIBROOT ?= "${@external_run(d, '${EXTERNAL_CC}', *(TARGET_CC_ARCH.split() + ['-print-file-name=crtbegin.o'])).rstrip().replace('/crtbegin.o', '')}"
EXTERNAL_HEADERS_MULTILIB_SUFFIX ?= "${@external_run(d, '${EXTERNAL_CC}', *(TARGET_CC_ARCH.split() + ['-print-sysroot-headers-suffix'])).rstrip()}"
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

    bb.debug(1, 'external-common.bbclass: failed to find kernel version header in {}'.format(p))
    return ''
