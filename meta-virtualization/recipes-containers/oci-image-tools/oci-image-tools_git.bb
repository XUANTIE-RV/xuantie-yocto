HOMEPAGE = "https://github.com/opencontainers/image-tools"
SUMMARY = "A collection of tools for working with the OCI image format specification"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS = "\
           oci-image-spec \
           oci-runtime-spec \
           go-digest \
           go-errors \
           spf13-cobra \
           spf13-pflag \
          "

SRC_URI = "git://github.com/opencontainers/image-tools.git;branch=master;protocol=https;destsuffix=git/src/github.com/opencontainers/image-tools \
           file://0001-config-make-Config.User-mapping-errors-a-warning.patch \
           file://0001-tool-respect-GO-and-GOBUILDFLAGS-when-building.patch"

SRCREV = "11f9988298305d36f64248a6ee55318e60bf170b"
PV = "1.0.0-rc3+git${SRCPV}"
GO_IMPORT = "import"

inherit goarch
inherit go

# This disables seccomp and apparmor, which are on by default in the
# go package. 
EXTRA_OEMAKE="BUILDTAGS=''"

S = "${WORKDIR}/git/src/github.com/opencontainers/image-tools"

do_compile() {
	export GOARCH="${TARGET_GOARCH}"
	export GOROOT="${STAGING_LIBDIR}/go"
	export GOPATH="${WORKDIR}/git/"

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

	oe_runmake tool
}

do_install() {
	install -d ${D}/${sbindir}
	install ${S}/oci-image-tool ${D}/${sbindir}/
}

INSANE_SKIP:${PN} += "ldflags textrel"
