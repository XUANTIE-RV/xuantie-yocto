# Handle automatically pointing LIC_FILES_CHKSUM to a common license outside
# the recipe's source tree, based on the value of LICENSE.
LIC_FILES_CHKSUM ?= "${COMMON_LIC_CHKSUM}"

COMMON_LIC_CHKSUM = ""
COMMON_LIC_CHKSUM_CLOSED = ""
COMMON_LIC_CHKSUM_GPL-2.0 = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
COMMON_LIC_CHKSUM_GPL-3.0 = "file://${COREBASE}/meta/files/common-licenses/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"
COMMON_LIC_CHKSUM_GPL-3.0-with-GCC-exception = "file://${COREBASE}/meta/files/common-licenses/GPL-3.0-with-GCC-exception;md5=aef5f35c9272f508be848cd99e0151df"
COMMON_LIC_CHKSUM_LGPL-2.1 = "file://${COREBASE}/meta/files/common-licenses/LGPL-2.1;md5=1a6d268fd218675ffea8be556788b780"


python () {
    import oe.license

    #; Set LIC_FILES_CHKSUM to a common license if it's unset and LICENSE is set
    licensestr = d.getVar('LICENSE', True)
    for pkg in d.getVar('PACKAGES', True).split():
        pkg_lic = d.getVar('LICENSE_%s' % pkg, True)
        if pkg_lic:
            licensestr += ' ' + pkg_lic

    licenses = oe.license.flattened_licenses(licensestr, lambda a, b: a + b)
    checksums = set()
    for license in licenses:
        if license != 'CLOSED' and d.getVar('LIC_FILES_CHKSUM', False) == '${COMMON_LIC_CHKSUM}':
            license = mapped_license(license, d)

            ext_chksum_var = 'COMMON_LIC_CHKSUM_{0}'.format(license)
            if d.getVar(ext_chksum_var, True):
                checksums.add('${%s}' % ext_chksum_var)
            else:
                lic_file_name = '${COREBASE}/meta/files/common-licenses/%s' % license
                lic_file = d.expand(lic_file_name)
                if os.path.exists(lic_file):
                    md5 = bb.utils.md5_file(lic_file)
                    chksum = 'file://{0};md5={1}'.format(lic_file_name, md5)
                    bb.fatal('{0}: No available license checksum info for this license. Either set LIC_FILES_CHKSUM, or define:\n  {1} = "{2}"'.format(d.getVar('PF', True), ext_chksum_var, chksum))
    d.setVar('COMMON_LIC_CHKSUM', ' '.join(checksums))
}

def mapped_license(license, d):
    if license.endswith('+'):
        license = license[:-1]

    mapped = d.getVarFlag('SPDXLICENSEMAP', license, False)
    if mapped:
        license = mapped
    return license
