HOMEPAGE = "https://github.com/opencontainers/runtime-tools"
SUMMARY = "oci-runtime-tool is a collection of tools for working with the OCI runtime specification"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b355a61a394a504dacde901c958f662c"

SRC_URI = "git://github.com/opencontainers/runtime-tools.git;branch=master;protocol=https;destsuffix=git/src/github.com/opencontainers/runtime-tools \
           file://0001-build-use-for-cross-compiler.patch \
           "

SRCREV = "0105384f68e16803891d0a17d9067b1def6a2778"
PV = "0.9.0+git${SRCPV}"
GO_IMPORT = "import"

INSANE_SKIP:${PN} += "ldflags textrel"

inherit goarch
inherit go

S = "${WORKDIR}/git/src/github.com/opencontainers/runtime-tools"

do_compile() {
	export GOARCH="${TARGET_GOARCH}"
	export GOPATH="${WORKDIR}/git/"
	export GOROOT="${STAGING_LIBDIR}/go"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CFLAGS=""
	export LDFLAGS=""
	export CGO_CFLAGS="${TARGET_CFLAGS}"
	export CGO_LDFLAGS="${TARGET_LDFLAGS}"
	export GO111MODULE=off
	export GOFLAGS="-mod=vendor"

	# fixes:
	# cannot find package runtime/cgo (using -importcfg)
	#        ... recipe-sysroot-native/usr/lib/aarch64-poky-linux/go/pkg/tool/linux_amd64/link:
	#        cannot open file : open : no such file or directory
	# export GO_BUILD_FLAGS="-a -pkgdir dontusecurrentpkgs"
	export EXTRA_FLAGS="-trimpath"

	cd ${S}

	oe_runmake tool
}

do_install() {
	install -d ${D}/${sbindir}
	install ${S}/oci-runtime-tool ${D}/${sbindir}/oci-runtime-tool
}

deltask compile_ptest_base

