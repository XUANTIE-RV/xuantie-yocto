import os.path
import re
import shlex
import subprocess
import oe.path
import bb


def run(d, cmd, *args):
    topdir = d.getVar('TOPDIR')
    toolchain_path = d.getVar('EXTERNAL_TOOLCHAIN')
    if toolchain_path:
        target_prefix = d.getVar('EXTERNAL_TARGET_SYS') + '-'
        if not cmd.startswith(target_prefix):
            cmd = target_prefix + cmd

        toolchain_bin = d.getVar('EXTERNAL_TOOLCHAIN_BIN')
        path = os.path.join(toolchain_bin, cmd)
        args = shlex.split(path) + list(args)

        bb.debug(1, 'oe.external.run({})'.format(repr(args)))
        try:
            output, _ = bb.process.run(args, cwd=topdir)
        except bb.process.CmdError as exc:
            bb.debug(1, 'oe.external.run: {} failed: {}'.format(subprocess.list2cmdline(args), exc))
        else:
            return output

    return 'UNKNOWN'


def parse_mirrors(mirrors_string):
    mirrors, invalid = [], []
    for entry in mirrors_string.replace('\\n', '\n').split('\n'):
        entry = entry.strip()
        if not entry:
            continue
        try:
            pathname, subst = entry.strip().split('|', 1)
        except ValueError:
            invalid.append(entry)
        mirrors.append(('^' + re.escape(pathname), subst))
    return mirrors, invalid


def get_file_search_metadata(d):
    '''Given the metadata, return the mirrors and sysroots to operate against.'''

    premirrors, invalid = parse_mirrors(d.getVar('FILES_PREMIRRORS'))
    for invalid_entry in invalid:
        bb.warn('Invalid FILES_MIRRORS entry: {0}'.format(invalid_entry))

    mirrors, invalid = parse_mirrors(d.getVar('FILES_MIRRORS'))
    for invalid_entry in invalid:
        bb.warn('Invalid FILES_MIRRORS entry: {0}'.format(invalid_entry))

    source_paths = [os.path.realpath(p)
                    for p in d.getVar('EXTERNAL_INSTALL_SOURCE_PATHS').split()]

    return source_paths, mirrors, premirrors


def gather_pkg_files(d):
    '''Given the metadata, return all the files we want to copy to ${D} for
    this recipe.'''
    import itertools
    files = []
    for pkg in d.getVar('PACKAGES').split():
        files = itertools.chain(files, (d.getVar('EXTERNAL_FILES_{}'.format(pkg)) or d.getVar('FILES_{}'.format(pkg)) or '').split())
    files = itertools.chain(files, d.getVar('EXTERNAL_EXTRA_FILES').split())
    return files


def copy_from_sysroots(pathnames, sysroots, mirrors, premirrors, installdest):
    '''Copy the specified files from the specified sysroots, also checking the
    specified mirror patterns as alternate paths, to the specified destination.'''
    expanded_pathnames = expand_paths(pathnames, mirrors, premirrors)
    searched_paths = search_sysroots(expanded_pathnames, sysroots)
    for path, files in searched_paths:
        if not files:
            bb.debug(1, 'oe.external: failed to find `{}`'.format(path))
        else:
            destdir = oe.path.join(installdest, os.path.dirname(path))
            bb.utils.mkdirhier(destdir)
            subprocess.check_call(['cp', '-PR', '--preserve=mode,timestamps', '--no-preserve=ownership'] + list(files) + [destdir + '/'])
            bb.note('Copied `{}`  to `{}/`'.format(', '.join(files), destdir))

def expand_paths(pathnames, mirrors, premirrors):
    '''Apply search/replace to paths to get alternate search paths.

    Returns a generator with tuples of (pathname, expanded_paths).'''
    import re
    for pathname in pathnames:
        expanded_paths = []

        for search, replace in premirrors:
            try:
                new_pathname = re.sub(search, replace, pathname, count=1)
            except re.error as exc:
                bb.warn("Invalid pattern for `%s`" % search)
                continue
            if new_pathname != pathname:
                expanded_paths.append(new_pathname)

        expanded_paths.append(pathname)

        for search, replace in mirrors:
            try:
                new_pathname = re.sub(search, replace, pathname, count=1)
            except re.error as exc:
                bb.warn("Invalid pattern for `%s`" % search)
                continue
            if new_pathname != pathname:
                expanded_paths.append(new_pathname)

        yield pathname, expanded_paths

def search_sysroots(path_entries, sysroots):
    '''Search the supplied sysroots for the supplied paths, checking supplied
    alternate paths. Expects entries in the format (pathname, all_paths).

    Returns a generator with tuples of (pathname, found_paths).'''
    import glob
    import itertools
    for path, pathnames in path_entries:
        for sysroot, pathname in ((s, p) for s in sysroots
                                         for p in pathnames):
            check_path = sysroot + os.sep + pathname
            found_paths = glob.glob(check_path)
            if found_paths:
                yield path, found_paths
                break
        else:
            yield path, None


def find_sysroot_files(paths, d):
    sysroots, mirrors, premirrors = get_file_search_metadata(d)
    expanded = expand_paths(paths, mirrors, premirrors)
    search_results = list(search_sysroots(expanded, sysroots))
    return [v for k, v in search_results]
