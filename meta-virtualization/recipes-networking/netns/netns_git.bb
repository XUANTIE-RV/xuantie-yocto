HOMEPAGE = "https://github.com/jfrazelle/netns"
SUMMARY = "Runc hook for setting up default bridge networking."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=7bac31faf84a2d7e88972f562a3ebbe5"

SRC_URI = "git://github.com/genuinetools/netns;branch=master;protocol=https \
           file://Makefile-force-rebuilding-all-packages-to-avoid-cgo.patch \
          "
SRCREV = "00d5d07ab1c8afcf481ffa5958719943b6ecfde4"
PV = "0.5.3+git${SRCPV}"
GO_IMPORT = "import"

S = "${WORKDIR}/git"

inherit goarch
inherit go

EXTRA_OEMAKE = "GO='${GO}'"

do_compile() {
	export GOARCH="${TARGET_GOARCH}"
	export GOROOT="${STAGING_LIBDIR}/go"
	# Setup vendor directory so that it can be used in GOPATH.
	#
	# Go looks in a src directory under any directory in GOPATH but netns
	# uses 'vendor' instead of 'vendor/src'. We can fix this with a symlink.
	#
	# We also need to link in the ipallocator and version directories as
	# they are not under the src directory.
	ln -sfn . "${S}/src/import/vendor/src"
	mkdir -p "${S}/src/import/vendor/src/github.com/genuinetools/netns"

	export GOPATH="${S}/src/import/vendor"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CFLAGS=""
	export LDFLAGS=""
	export CGO_CFLAGS="${TARGET_CFLAGS}"
	export CGO_LDFLAGS="${TARGET_LDFLAGS}"
	export GOFLAGS="-mod=vendor -trimpath"

	cd ${S}/src/import
	# Static builds work but are not recommended. See Makefile*cgo patch.
	#oe_runmake static
	oe_runmake build

	# Golang forces permissions to 0500 on directories and 0400 on files in
	# the module cache which prevents us from easily cleaning up the build
	# directory. Let's just fix the permissions here so we don't have to
	# hack the clean tasks.
	chmod -R u+w vendor/pkg/mod
}

do_install() {
	install -d ${D}/${sbindir}
	install ${S}/src/import/netns ${D}/${sbindir}/netns
}
