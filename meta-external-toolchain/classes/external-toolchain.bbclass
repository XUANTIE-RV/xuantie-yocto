# This class provides everything necessary for a recipe to pull bits from an
# external toolchain:
# - Automatically sets LIC_FILES_CHKSUM based on LICENSE if appropriate
# - Searches the external toolchain sysroot and alternate locations for the
#   patterns specified in the FILES variables, with support for checking
#   alternate locations within the sysroot as well
# - Automatically PROVIDES/RPROVIDES the non-external-suffixed names
# - Usual bits to handle packaging of existing binaries
# - Automatically skips the recipe if its files aren't available in the
#   external toolchain

# Since these are prebuilt binaries, there are no source files to checksum for
# LIC_FILES_CHKSUM, so use the license from common-licenses
inherit common-license

# We don't extract anything which will create S, and we don't want to see the
# warning about it
S = "${WORKDIR}"

# Prebuilt binaries, no need for any default dependencies
INHIBIT_DEFAULT_DEPS = "1"

# Missing build deps don't matter when we don't build anything
INSANE_SKIP:${PN} += "build-deps"

EXTERNAL_PN ?= "${@PN.replace('-external', '')}"
PROVIDES += "${EXTERNAL_PN}"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "${COMMON_LIC_CHKSUM}"

# Packaging requires objcopy/etc for split and strip
PACKAGE_DEPENDS += "virtual/${MLPREFIX}${TARGET_PREFIX}binutils"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

EXTERNAL_PV_PREFIX ?= ""
EXTERNAL_PV_SUFFIX ?= ""
PV:prepend = "${@'${EXTERNAL_PV_PREFIX}' if '${EXTERNAL_PV_PREFIX}' else ''}"
PV:append = "${@'${EXTERNAL_PV_SUFFIX}' if '${EXTERNAL_PV_SUFFIX}' else ''}"

EXTERNAL_EXTRA_FILES ?= ""

# Skip this recipe if we don't have files in the external toolchain
EXTERNAL_AUTO_PROVIDE ?= "0"
EXTERNAL_AUTO_PROVIDE:class-target ?= "1"

# We don't care if this path references other variables
EXTERNAL_TOOLCHAIN[vardepvalue] = "${EXTERNAL_TOOLCHAIN}"

# We don't want to rebuild if the path to the toolchain changes, only if the
# toolchain changes
external_toolchain_do_install[vardepsexclude] += "EXTERNAL_TOOLCHAIN"
EXTERNAL_INSTALL_SOURCE_PATHS[vardepsexclude] += "EXTERNAL_TOOLCHAIN"

# Propagate file permissions mode from owner to the rest of the user
# so the toolchain bits would be available for everyone to use even in the
# directories with root permissions.
EXTERNAL_PROPAGATE_MODE ?= "0"

python () {
    # Skipping only matters up front
    if d.getVar('BB_WORKERCONTEXT', True) == '1':
        return

    if not d.getVar('TCMODE', True).startswith('external'):
        raise bb.parse.SkipPackage("External toolchain not configured (TCMODE not set to an external toolchain).")

    # We're not an available provider if there's no external toolchain
    if not d.getVar("EXTERNAL_TOOLCHAIN", True):
        raise bb.parse.SkipPackage("External toolchain not configured (EXTERNAL_TOOLCHAIN not set).")

    if not bb.utils.to_boolean(d.getVar('EXTERNAL_AUTO_PROVIDE', d)):
        return

    sysroots, mirrors, premirrors = oe.external.get_file_search_metadata(d)
    search_patterns = []
    pattern = d.getVar('EXTERNAL_PROVIDE_PATTERN', True)
    if pattern:
        search_patterns.append(pattern)
    else:
        files = oe.external.gather_pkg_files(d)
        search_patterns.extend(filter(lambda f: '.debug' not in f, files))

    expanded = oe.external.expand_paths(search_patterns, mirrors, premirrors)
    paths = oe.external.search_sysroots(expanded, sysroots)
    if not any(f for p, f in paths):
        raise bb.parse.SkipPackage('No files found in external toolchain sysroot for: {}'.format(', '.join(search_patterns)))
}

fakeroot python do_install () {
    bb.build.exec_func('external_toolchain_do_install', d)
    pass # Sentinel
}

def external_toolchain_propagate_mode (d, installdest):
    import stat

    propagate = d.getVar('EXTERNAL_PROPAGATE_MODE', True)
    if propagate == '0':
        return

    for root, dirs, files in os.walk(installdest):
        for bit in dirs + files:
            path = os.path.join(root, bit)
            try:
                bitstat = os.stat(path)
            except ValueError:
                continue
            else:
                newmode = bitstat.st_mode
                if bitstat.st_mode & stat.S_IRUSR:
                    newmode |= stat.S_IRGRP | stat.S_IROTH
                if bitstat.st_mode & stat.S_IXUSR:
                    newmode |= stat.S_IXGRP | stat.S_IXOTH
                os.chmod(path, newmode)

python external_toolchain_do_install () {
    import subprocess
    installdest = d.getVar('D', True)
    sysroots, mirrors, premirrors = oe.external.get_file_search_metadata(d)
    files = oe.external.gather_pkg_files(d)
    oe.external.copy_from_sysroots(files, sysroots, mirrors, premirrors, installdest)
    if 'do_install_extra' in d:
        bb.build.exec_func('do_install_extra', d)
    external_toolchain_propagate_mode(d, installdest)
}
external_toolchain_do_install[vardeps] += "${@' '.join('FILES:%s' % pkg for pkg in '${PACKAGES}'.split())}"

# Change do_install's CWD to EXTERNAL_TOOLCHAIN for convenience
do_install[dirs] = "${D} ${EXTERNAL_TOOLCHAIN}"

python () {
    # Deal with any do_install:append
    install = d.getVar('do_install', False)
    try:
        base, appended = install.split('# Sentinel', 1)
    except ValueError:
        pass
    else:
        d.setVar('do_install', base)
        if appended.strip():
            d.setVar('do_install_added', appended)
            d.setVarFlag('do_install_added', 'func', '1')
            d.appendVarFlag('do_install', 'postfuncs', ' do_install_added')
}

# Toolchain shipped binaries weren't necessarily built ideally
WARN_QA:remove = "ldflags textrel"
ERROR_QA:remove = "ldflags textrel"

# Debug files may well have already been split out, or stripped out
INSANE_SKIP:${PN} += "already-stripped"

RPROVIDES:${PN} += "${EXTERNAL_PN}"
RPROVIDES:${PN}-dev += "${EXTERNAL_PN}-dev"
RPROVIDES:${PN}-staticdev += "${EXTERNAL_PN}-staticdev"
RPROVIDES:${PN}-dbg += "${EXTERNAL_PN}-dbg"
RPROVIDES:${PN}-doc += "${EXTERNAL_PN}-doc"
RPROVIDES:${PN}-locale += "${EXTERNAL_PN}-locale"
LOCALEBASEPN = "${EXTERNAL_PN}"

FILES:${PN} = ""
FILES:${PN}-dev = ""
FILES:${PN}-staticdev = ""
FILES:${PN}-doc = ""
FILES:${PN}-locale = ""

def debug_paths(d):
    l = d.createCopy()
    l.finalize()
    paths = []
    exclude = [
        l.getVar('datadir', True),
        l.getVar('includedir', True),
    ]
    for p in l.getVar('PACKAGES', True).split():
        if p.endswith('-dbg'):
            continue
        for f in (l.getVar('FILES:%s' % p, True) or '').split():
            if any((f == x or f.startswith(x + '/')) for x in exclude):
                continue
            d = os.path.dirname(f)
            b = os.path.basename(f)
            paths.append('/usr/lib/debug{0}/{1}.debug'.format(d, b))
            paths.append('{0}/.debug/{1}'.format(d, b))
            paths.append('{0}/.debug/{1}.debug'.format(d, b))
    return set(paths)

FILES:${PN}-dbg = "${@' '.join(debug_paths(d))}"
