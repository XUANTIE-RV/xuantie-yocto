DESCRIPTION = "A markdown to manpage generator."
HOMEPAGE = "https://github.com/cpuguy83/go-md2man"
SECTION = "devel/go"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE.md;md5=80794f9009df723bbc6fe19234c9f517"

BBCLASSEXTEND = "native"

GO_IMPORT = "github.com/cpuguy83/go-md2man"
#GO_INSTALL = "${GO_IMPORT}/bin/go-md2man"

SRC_URI = "git://${GO_IMPORT}.git;branch=master;protocol=https"

SRCREV = "f79a8a8ca69da163eee19ab442bedad7a35bba5a"
PV = "1.0.10+git${SRCPV}"

S = "${WORKDIR}/git"

inherit go

do_compile:prepend() {
	export GO111MODULE=off
}
