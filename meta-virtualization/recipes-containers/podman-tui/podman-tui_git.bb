HOMEPAGE = "https://github.com/containers/podman-tui"
SUMMARY =  "podman-tui is a Terminal User Interface to interact with podman"
DESCRIPTION = "podman-tui is a Terminal User Interface to interact with the podman v4. \
podman bindings has been used to communicate with podman environment through rest api API (unix socket). \
    "

inherit features_check
REQUIRED_DISTRO_FEATURES ?= "seccomp ipv6"

DEPENDS = " \
    libseccomp \
    libdevmapper \
    lvm2 \
    btrfs-tools \
    gpgme \
    libassuan \
    libgpg-error \
"

SRCREV = "8f2b80ffbd2518c2e95ddd092e009cc385a0633c"
SRC_URI = " \
    git://github.com/containers/podman-tui;protocol=https;branch=main \
"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

GO_IMPORT = "import"

S = "${WORKDIR}/git"

PV = "v0.5.0+git${SRCPV}"

PODMAN_PKG = "github.com/containers/podman-tui"

inherit go goarch
inherit pkgconfig

do_configure[noexec] = "1"

BUILD_TAGS ?= "exclude_graphdriver_btrfs exclude_graphdriver_devicemapper"

do_compile() {
	cd ${S}/src

	rm -rf .gopath
	mkdir -p .gopath/src/"$(dirname "${PODMAN_PKG}")"
	ln -sf ../../../../import/ .gopath/src/"${PODMAN_PKG}"

	cd ${S}/src/.gopath/src/"${PODMAN_PKG}"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export GOFLAGS="-mod=vendor"

	# oe_runmake BUILDTAGS="${BUILDTAGS}"
	${GO} build -trimpath -tags "$(BUILDTAGS)" ${GO_LDFLAGS} -o bin/podman-tui
}

do_install() {
	cd ${S}/src/.gopath/src/"${PODMAN_PKG}"

	install -d ${D}/${bindir}
	install bin/podman-tui ${D}/${bindir}/
}

RDEPENDS:${PN} += "podman"
