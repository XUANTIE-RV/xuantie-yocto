TOOLCHAIN_SANITY_VERSION = "1"

def check_toolchain_sanity(d, generate_events=False):
    import shlex
    import tempfile

    if not d.getVar('TCMODE', True).startswith('external'):
        return

    extpath = d.getVar('EXTERNAL_TOOLCHAIN', True)

    # Test 1: EXTERNAL_TOOLCHAIN exists
    if not os.path.exists(extpath):
        raise_exttc_sanity_error('EXTERNAL_TOOLCHAIN path `%s` does not exist' % extpath, d, generate_events)
    extpath = os.path.realpath(extpath)

    sanity_file = d.expand('${TOPDIR}/conf/exttc_sanity_info')
    version = d.getVar('TOOLCHAIN_SANITY_VERSION', True)
    check, config = should_run(sanity_file, {'version': version, 'path': extpath})
    if not check:
        return
    cfgdata = config['DEFAULT']

    # Test 2: EXTERNAL_TARGET_SYS is set correctly
    if d.getVar('EXTERNAL_TARGET_SYS', True) == 'UNKNOWN':
        raise_exttc_sanity_error('Unable to locate prefixed gcc binary for %s in EXTERNAL_TOOLCHAIN_BIN (%s)' % (d.getVar('TARGET_SYS', True), d.getVar('EXTERNAL_TOOLCHAIN_BIN', True)), d, generate_events)

    # Test 3: gcc binary exists
    gcc = d.expand('${EXTERNAL_TOOLCHAIN_BIN}/${EXTERNAL_TARGET_SYS}-gcc')
    if not os.path.exists(gcc):
        raise_exttc_sanity_error('Compiler path `%s` does not exist' % gcc, d, generate_events)

    # Test 4: we can run it to get the version
    cmd = d.expand('${EXTERNAL_TOOLCHAIN_BIN}/${EXTERNAL_CC} -dumpversion')
    sourcery_version = exttc_sanity_run(shlex.split(cmd), d, generate_events)
    if cfgdata.get('sourcery_version') == sourcery_version:
        return

    # Test 5: we can compile an empty test app
    with tempfile.TemporaryDirectory() as tmpdir:
        with open(os.path.join(tmpdir, 'test.c'), 'w') as f:
            f.write('int main() {}')

        # The external toolchain recipes haven't necessarily been built, so we
        # need to drop --sysroot= and --no-sysroot-suffix and use the bits in
        # the external toolchain sysroots for this test
        l = d.createCopy()
        l.setVar('TOOLCHAIN_OPTIONS', '')
        l.setVar('TARGET_PREFIX', '${EXTERNAL_TARGET_SYS}-')
        l.setVar('HOST_CC_ARCH:remove', '--no-sysroot-suffix')
        cmd = l.expand('${EXTERNAL_TOOLCHAIN_BIN}/${EXTERNAL_CC} ${HOST_CC_ARCH} ${CFLAGS} ${LDFLAGS} test.c -o test')
        exttc_sanity_run(shlex.split(cmd), d, generate_events, tmpdir)

    with open(sanity_file, 'w') as f:
        config.write(f)

def should_run(cfgfile, expected):
    import configparser

    config = configparser.ConfigParser()
    readfiles = config.read(cfgfile)
    cfgdata = config['DEFAULT']

    if cfgfile in readfiles and cfgdata == expected:
        return False, None

    cfgdata.update(expected)
    return True, config

def raise_exttc_sanity_error(msg, d, generate_events):
    msg = 'Sanity check of the external toolchain failed: ' + msg
    if generate_events:
        try:
            bb.event.fire(bb.event.SanityCheckFailed(msg, None), d)
        except TypeError:
            bb.event.fire(bb.event.SanityCheckFailed(msg), d)
    else:
        bb.fatal(msg)

def exttc_sanity_run(cmd, d, generate_events, cwd='/'):
    import subprocess
    try:
        return subprocess.check_output(cmd, stderr=subprocess.STDOUT, cwd=cwd)
    except FileNotFoundError:
        raise_exttc_sanity_error('\n  Command: %s\n  Exit Code: 127\n  Output: no such file or directory' % cmd, d, generate_events)
    except subprocess.CalledProcessError as exc:
        if not isinstance(cmd, str):
            cmd = subprocess.list2cmdline(cmd)
        output = exc.output.decode()
        output_indented = ''.join('    ' + l for l in output.splitlines(keepends=True))
        raise_exttc_sanity_error('\n  Command: %s\n  Exit code: %s\n  Output:\n%s' % (cmd, exc.returncode, output_indented), d, generate_events)

python toolchain_sanity_eventhandler() {
    check_toolchain_sanity(d, e.generateevents)
}
toolchain_sanity_eventhandler[eventmask] = "bb.event.SanityCheck"
addhandler toolchain_sanity_eventhandler
