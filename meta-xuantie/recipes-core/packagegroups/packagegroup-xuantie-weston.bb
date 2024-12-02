DESCRIPTION = "Weston packagegroup"
LICENSE = "MIT"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

RDEPENDS:${PN} += " \
    weston \
    gtk+3 \
    gpgme \
    gnupg \
"
