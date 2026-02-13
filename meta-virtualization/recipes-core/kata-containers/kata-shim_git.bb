DESCRIPTION = " Handle stdio and signals of the container process"
HOMEPAGE = "https://github.com/kata-containers/shim"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/github.com/kata-containers/shim/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

GO_IMPORT = "github.com/kata-containers/shim"
SRCREV = "bcc35aeca3ef6fa0976005c9e93525906aefed2f"
SRC_URI = "git://${GO_IMPORT}.git;branch=master;protocol=https \
          "

SKIP_RECIPE[kata-shim] ?= "kata containers are currently broken, patches accepted"

RDEPENDS:${PN}-dev:append = "bash"

S = "${WORKDIR}/git"

inherit go

do_compile() {
	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export GOARCH=${TARGET_GOARCH}
	export CGO_ENABLED="1"
	export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"

	cd ${S}/src/${GO_IMPORT}
	oe_runmake kata-shim
}

do_install() {
	mkdir -p ${D}/${libexecdir}/kata-containers
	cp ${WORKDIR}/git/src/${GO_IMPORT}/kata-shim ${D}/${libexecdir}/kata-containers
}

deltask compile_ptest_base
