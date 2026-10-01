def get_external_musl_version(d):
    """Detect musl libc version from the external toolchain sysroot."""
    import subprocess
    import glob
    import re

    if (not d.getVar('TCMODE', True).startswith('external') or
            not d.getVar('EXTERNAL_TOOLCHAIN', True)):
        return 'UNKNOWN'

    sysroot = d.getVar('EXTERNAL_TOOLCHAIN_SYSROOT', True)
    if not sysroot:
        return 'UNKNOWN'

    # Method 1: Extract version string from libc.so using strings
    libdir = d.getVar('libdir', True) or '/usr/lib'
    base_libdir = d.getVar('base_libdir', True) or '/lib'

    # Search for libc.so in sysroot (try multiple possible locations).
    # Some prebuilt toolchains place libc.so under base_libdir (e.g. /lib)
    # instead of libdir (e.g. /usr/lib) and may omit the ld-musl symlink,
    # so accept libc.so under either location before falling back to the
    # loader-aliased name.
    libc_candidates = []
    for pattern in [
        os.path.join(sysroot, libdir.lstrip('/'), 'libc.so'),
        os.path.join(sysroot, base_libdir.lstrip('/'), 'libc.so'),
        os.path.join(sysroot, base_libdir.lstrip('/'), 'ld-musl-*.so.1'),
    ]:
        libc_candidates.extend(glob.glob(pattern))

    for libc_path in libc_candidates:
        # Resolve symlinks to find the actual file
        real_path = os.path.realpath(libc_path)
        if not os.path.isfile(real_path):
            continue
        try:
            output = subprocess.check_output(
                ['strings', real_path],
                stderr=subprocess.DEVNULL
            ).decode('utf-8', errors='replace')
            # musl embeds its version string as a simple "X.Y.Z" pattern
            # near the "musl libc" identifier string
            for line in output.splitlines():
                m = re.match(r'^(\d+\.\d+\.\d+)$', line.strip())
                if m:
                    ver = m.group(1)
                    # Filter out false positives like "127.0.0.1"
                    parts = [int(x) for x in ver.split('.')]
                    if parts[0] < 10 and parts[1] < 10:
                        return ver
        except (subprocess.CalledProcessError, OSError):
            pass

    return 'UNKNOWN'

PV:tcmode-external := "${@get_external_musl_version(d)}"
