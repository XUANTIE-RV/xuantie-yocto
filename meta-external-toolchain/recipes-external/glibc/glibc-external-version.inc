def find_sysroot_path(path_pattern, d):
    """Look for a single file (optionally with wildcards) in the external toolchain sysroot."""
    found_paths = oe.external.find_sysroot_files([path_pattern], d)
    if found_paths and any(found_paths):
        try:
            return found_paths[0][0]
        except ValueError:
            pass

def get_external_libc_version(d):
    import subprocess

    if (d.getVar('TCMODE', True).startswith('external') and
            d.getVar('EXTERNAL_TOOLCHAIN', True)):
        # Locate /lib/libc-<version>.sh
        sopattern = os.path.join(d.getVar('base_libdir', True), 'libc-*.so')
        so_path = find_sysroot_path(sopattern, d)
        if so_path:
            soname = os.path.basename(so_path)
            return soname[5:-3]
        else:
            # Locate and run ldd --version
            ldd_path = os.path.join(d.getVar('bindir'), 'ldd')
            sysroot_ldd = find_sysroot_path(ldd_path, d)
            if sysroot_ldd:
                try:
                    ldd_version = subprocess.check_output([sysroot_ldd, '--version']).decode('utf-8')
                except subprocess.CalledProcessError as exc:
                    pass
                else:
                    try:
                        line_one = ldd_version.splitlines()[0]
                        return line_one.split()[-1]
                    except ValueError as exc:
                        pass

    return 'UNKNOWN'

PV:tcmode-external := "${@get_external_libc_version(d)}"
