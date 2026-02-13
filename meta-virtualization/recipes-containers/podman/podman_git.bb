HOMEPAGE = "https://podman.io/"
SUMMARY =  "A daemonless container engine"
DESCRIPTION = "Podman is a daemonless container engine for developing, \
    managing, and running OCI Containers on your Linux System. Containers can \
    either be run as root or in rootless mode. Simply put: \
    `alias docker=podman`. \
    "

inherit features_check
REQUIRED_DISTRO_FEATURES ?= "seccomp ipv6"

DEPENDS = " \
    go-metalinter-native \
    go-md2man-native \
    gpgme \
    libseccomp \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"

SRCREV = "754ec89a8a185d308ca5ed08afaf34d6cbda08da"
SRC_URI = " \
    git://github.com/containers/libpod.git;branch=v4.2;protocol=https \
    ${@bb.utils.contains('PACKAGECONFIG', 'rootless', 'file://50-podman-rootless.conf', '', d)} \
    file://run-ptest \
"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=3d9b931fa23ab1cacd0087f9e2ee12c0"

GO_IMPORT = "import"

S = "${WORKDIR}/git"

PV = "4.2.0+git${SRCPV}"

PACKAGES =+ "${PN}-contrib"

PODMAN_PKG = "github.com/containers/libpod"
BUILDTAGS ?= "seccomp varlink \
${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
exclude_graphdriver_btrfs exclude_graphdriver_devicemapper"

# overide LDFLAGS to allow podman to build without: "flag provided but not # defined: -Wl,-O1
export LDFLAGS=""

# https://github.com/llvm/llvm-project/issues/53999
TOOLCHAIN = "gcc"

# podmans Makefile expects BUILDFLAGS to be set but go.bbclass defines them in GOBUILDFLAGS
export BUILDFLAGS="${GOBUILDFLAGS}"

inherit go goarch
inherit systemd pkgconfig ptest

do_configure[noexec] = "1"

EXTRA_OEMAKE = " \
     PREFIX=${prefix} BINDIR=${bindir} LIBEXECDIR=${libexecdir} \
     ETCDIR=${sysconfdir} TMPFILESDIR=${nonarch_libdir}/tmpfiles.d \
     SYSTEMDDIR=${systemd_unitdir}/system USERSYSTEMDDIR=${systemd_unitdir}/user \
"

# remove 'docker' from the packageconfig if you don't want podman to
# build and install the docker wrapper. If docker is enabled in the
# packageconfig, the podman package will rconfict with docker.
PACKAGECONFIG ?= "docker"

do_compile() {
	cd ${S}/src
	rm -rf .gopath
	mkdir -p .gopath/src/"$(dirname "${PODMAN_PKG}")"
	ln -sf ../../../../import/ .gopath/src/"${PODMAN_PKG}"

	ln -sf "../../../import/vendor/github.com/varlink/" ".gopath/src/github.com/varlink"

	export GOARCH="${BUILD_GOARCH}"
	export GOPATH="${S}/src/.gopath"
	export GOROOT="${STAGING_DIR_NATIVE}/${nonarch_libdir}/${HOST_SYS}/go"

	cd ${S}/src/.gopath/src/"${PODMAN_PKG}"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export GOARCH=${TARGET_GOARCH}
	export CGO_ENABLED="1"
	export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"

	oe_runmake BUILDTAGS="${BUILDTAGS}"
}

do_install() {
	cd ${S}/src/.gopath/src/"${PODMAN_PKG}"

	export GOARCH="${BUILD_GOARCH}"
	export GOPATH="${S}/src/.gopath"
	export GOROOT="${STAGING_DIR_NATIVE}/${nonarch_libdir}/${HOST_SYS}/go"

	oe_runmake install DESTDIR="${D}"
	if ${@bb.utils.contains('PACKAGECONFIG', 'docker', 'true', 'false', d)}; then
		oe_runmake install.docker DESTDIR="${D}"
	fi

	# Silence docker emulation warnings.
	mkdir -p ${D}/etc/containers
	touch ${D}/etc/containers/nodocker

	if ${@bb.utils.contains('PACKAGECONFIG', 'rootless', 'true', 'false', d)}; then
		install -d "${D}${sysconfdir}/sysctl.d"
		install -m 0644 "${WORKDIR}/50-podman-rootless.conf" "${D}${sysconfdir}/sysctl.d"
	fi
}

do_install_ptest () {
	cp ${S}/src/import/Makefile ${D}${PTEST_PATH}
	install -d ${D}${PTEST_PATH}/test
	cp -r ${S}/src/import/test/system ${D}${PTEST_PATH}/test

	# Some compatibility links for the Makefile assumptions.
	install -d ${D}${PTEST_PATH}/bin
	ln -s ${bindir}/podman ${D}${PTEST_PATH}/bin/podman
	ln -s ${bindir}/podman-remote ${D}${PTEST_PATH}/bin/podman-remote
}

FILES:${PN} += " \
    ${systemd_unitdir}/system/* \
    ${systemd_unitdir}/user/* \
    ${nonarch_libdir}/tmpfiles.d/* \
    ${sysconfdir}/cni \
"

SYSTEMD_SERVICE:${PN} = "podman.service podman.socket"

# The other option for this is "busybox", since meta-virt ensures
# that busybox is configured with nsenter
VIRTUAL-RUNTIME_base-utils-nsenter ?= "util-linux-nsenter"

RDEPENDS:${PN} += "\
	conmon virtual-runc iptables cni skopeo ${VIRTUAL-RUNTIME_base-utils-nsenter} \
	${@bb.utils.contains('PACKAGECONFIG', 'rootless', 'fuse-overlayfs slirp4netns', '', d)} \
"
RRECOMMENDS:${PN} += "slirp4netns kernel-module-xt-masquerade kernel-module-xt-comment"
RCONFLICTS:${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'docker', 'docker', '', d)}"

RDEPENDS:${PN}-ptest += " \
	bash \
	bats \
	buildah \
	catatonit \
	coreutils \
	file \
	gnupg \
	jq \
	make \
	tar \
"
