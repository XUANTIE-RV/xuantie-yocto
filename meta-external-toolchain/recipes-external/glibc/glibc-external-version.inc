def get_external_libc_version(d):
    if (d.getVar('TCMODE', True).startswith('external') and
            d.getVar('EXTERNAL_TOOLCHAIN', True)):
        sopattern = os.path.join(d.getVar('base_libdir', True), 'libc-*.so')
        found_paths = oe.external.find_sysroot_files([sopattern], d)
        if found_paths:
            so_paths = found_paths[0]
            if so_paths:
                soname = os.path.basename(so_paths[0])
                return soname[5:-3]

    return 'UNKNOWN'

PV:tcmode-external := "${@get_external_libc_version(d)}"
