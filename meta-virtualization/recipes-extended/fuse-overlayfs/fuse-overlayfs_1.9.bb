SUMMARY = "FUSE implementation of overlayfs."
DESCRIPTION = "An implementation of overlay+shiftfs in FUSE for rootless \
containers."

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRCREV = "51592ea406f48faeccab288f65dcba6c4a67cd90"
SRC_URI = " \
	git://github.com/containers/fuse-overlayfs.git;nobranch=1;protocol=https \
	file://0001-utils-Add-missing-include-on-limits.h.patch \
"

DEPENDS = "fuse3"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
