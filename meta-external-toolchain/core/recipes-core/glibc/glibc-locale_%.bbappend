INHIBIT_PACKAGE_DEBUG_SPLIT_tcmode-external = "1"

# Toolchain shipped binaries weren't necessarily built ideally
WARN_QA_remove = "ldflags textrel"
ERROR_QA_remove = "ldflags textrel"

# Debug files may well have already been split out, or stripped out
INSANE_SKIP_${PN} += "already-stripped"

# localedef needs libgcc & libc
localedef_depends = ""
localedef_depends_tcmode-external = "${MLPREFIX}libgcc:do_packagedata virtual/${MLPREFIX}libc:do_packagedata"

python () {
    depends = d.getVar('localedef_depends', True)
    if depends:
        for task in ['do_package', 'do_package_write_ipk', 'do_package_write_deb'
                     'do_package_write_rpm']:
            d.appendVarFlag(task, 'depends', ' ' + depends)
}
