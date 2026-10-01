# uclibc-ng version information from external toolchain
# This file is included by uclibc-ng-external.bb

UCLIBC_NG_VERSION = "${@get_external_toolchain_gcc_version(d)}"
PV = "${UCLIBC_NG_VERSION}"

def get_external_toolchain_gcc_version(d):
    import subprocess, os
    extpath = d.getVar('EXTERNAL_TOOLCHAIN', True)
    if not extpath:
        return 'UNKNOWN'
    gcc_path = os.path.join(extpath, 'bin', d.getVar('EXTERNAL_TARGET_SYS', True) + '-gcc')
    if not os.path.exists(gcc_path):
        return 'UNKNOWN'
    try:
        result = subprocess.check_output([gcc_path, '--version'], text=True)
        return result.split('\n')[0].split()[-1]
    except:
        return 'UNKNOWN'
