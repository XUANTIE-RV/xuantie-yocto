HOMEPAGE = "https://github.com/jfrazelle/riddler"
SUMMARY = "Convert `docker inspect` to opencontainers (OCI compatible) runc spec."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20ce4c6a4f32d6ee4a68e3a7506db3f1"

SRC_URI = "git://github.com/jfrazelle/riddler;branch=master;protocol=https;destsuffix=git/src/github.com/jessfraz/riddler \
           file://0001-build-use-to-select-cross-compiler.patch \
          "

SRCREV = "23befa0b232877b5b502b828e24161d801bd67f6"
PV = "0.1.0+git${SRCPV}"

S = "${WORKDIR}/git/src/github.com/jessfraz/riddler"
GO_IMPORT = "github.com/jessfraz/riddler"

inherit goarch
inherit go

# In addition to hosts go does not like, we do not build for mips.
#
COMPATIBLE_HOST:mipsarch = "null"

# This disables seccomp and apparmor, which are on by default in the
# go package. 
EXTRA_OEMAKE="BUILDTAGS=''"

do_compile() {
	export GOARCH="${TARGET_GOARCH}"
	export GOROOT="${STAGING_LIBDIR}/go"
	export GOPATH="${S}/src/import/vendor:${WORKDIR}/git/"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CFLAGS=""
	export LDFLAGS=""
	export CGO_CFLAGS="${TARGET_CFLAGS}"
	export CGO_LDFLAGS="${TARGET_LDFLAGS}"
	export GO111MODULE=off
	export GOBUILDFLAGS="-trimpath"

	cd ${S}

	oe_runmake static
}

do_install() {
	install -d ${D}/${sbindir}
	install ${S}/riddler ${D}/${sbindir}/riddler
}
