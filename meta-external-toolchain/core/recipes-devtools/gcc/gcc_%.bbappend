# FIXME: resolve gcc version mismatch issues with ${libdir}/gcc/*/<version>
# filesystem paths
python () {
    if d.getVar('TCMODE', True).startswith('external'):
        # unwind.h will come from libgcc-external, we don't want to try to pull it
        # from the cross area of the sysroot
        inst = d.getVar('do_install', False).splitlines()
        inst = filter(lambda l: not ('unwind.h' in l and '${STAGING_LIBDIR_NATIVE}' in l), inst)
        d.setVar('do_install', '\n'.join(inst))
}
