HOMEPAGE = "https://github.com/lf-edge/runx"
SUMMARY = "console for runx"
DESCRIPTION = "Xen Runtime for OCI"

SRCREV_runx = "f24efd33fb18469e9cfe4d1bfe8e2c90ec8c4e93"
SRCREV_runc = "1e7bb5b773162b57333d57f612fd72e3f8612d94"

SRC_URI = "\
	  git://github.com/lf-edge/runx;nobranch=1;name=runx;protocol=https \
          git://github.com/opencontainers/runc;branch=release-1.1;name=runc;protocol=https;destsuffix=git/src/import/gobuild/go/src/github.com/opencontainers/runc \
          file://0001-build-use-instead-of-go.patch \
	  "
SRC_URI[md5sum] = "0d701ac1e2a67d47ce7127432df2c32b"
SRC_URI[sha256sum] = "5a26478906d5005f4f809402e981518d2b8844949199f60c4b6e1f986ca2a769"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=945fc9aa694796a6337395cc291ddd8c"

S = "${WORKDIR}/git"
PV = "0.1-git${SRCREV_runx}"

inherit features_check
REQUIRED_DISTRO_FEATURES = "vmsep"
GO_PARALLEL_BUILD ?= "${@oe.utils.parallel_make_argument(d, '-p %d')}"

inherit pkgconfig

GO_IMPORT = "import"
inherit go

RDEPENDS:${PN}-dev = "bash"

do_compile() {

    export GOARCH="${TARGET_GOARCH}"
    cd ${S}/src/import/gobuild

    # Build the target binaries
    export GOARCH="${TARGET_GOARCH}"
    # Pass the needed cflags/ldflags so that cgo can find the needed headers files and libraries
    export CGO_ENABLED="1"
    export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
    export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
    export CFLAGS=""
    export LDFLAGS=""
    export CC="${CC}"
    export LD="${LD}"
    export GOBIN=""
    export GO111MODULE=off

    oe_runmake GO=${GO}
}

do_install() {

    install -d ${D}${datadir}/runX
    install -m 755 ${B}/src/import/gobuild/serial_fd_handler ${D}${datadir}/runX/
}

FILES:${PN} += "${datadir}/runX/*"
