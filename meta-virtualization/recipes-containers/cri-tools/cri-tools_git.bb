HOMEPAGE = "https://github.com/kubernetes-sigs/cri-tools"
SUMMARY = "CLI and validation tools for Kubelet Container Runtime Interface (CRI)"
DESCRIPTION = "What is the scope of this project? \
\
cri-tools aims to provide a series of debugging and validation tools for \
Kubelet CRI, which includes: \
\
  * crictl: CLI for kubelet CRI. \
  * critest: validation test suites for kubelet CRI. \
\
What is not in scope for this project? \
\
  * Building a new kubelet container runtime based on CRI. \
  * Managing pods/containers for CRI-compatible runtimes by end-users, e.g. \
    pods created by crictl may be removed automatically by kubelet because of \
    non-exist on the kube-apiserver. \
 "

SRCREV_cri-tools = "77d02a0356e9e1dca58d44e54dd0799e89856387"
SRC_URI = "\
	git://github.com/kubernetes-sigs/cri-tools.git;branch=master;name=cri-tools;protocol=https \
        file://0001-build-allow-environmental-CGO-settings-and-pass-dont.patch \
	"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

GO_IMPORT = "import"

PV = "1.24.2+git${SRCREV_cri-tools}"

RPROVIDES:${PN} += "crictl"
PACKAGES =+ "${PN}-critest"

inherit go
inherit goarch
inherit pkgconfig

EXTRA_OEMAKE="BUILDTAGS=''"

do_compile() {
	# link fixups for compilation
	rm -f ${S}/src/import/vendor/src
	ln -sf ./ ${S}/src/import/vendor/src

	export GOPATH="${S}/src/import/.gopath:${S}/src/import/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go"
	cd ${S}/src/import

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

	oe_runmake crictl
}

do_install() {
    install -d ${D}${bindir}
    install -m 755 -D ${S}/src/import/build/bin/* ${D}/${bindir}
}

FILES:${PN}-critest = "${bindir}/critest"

# don't clobber hooks.d
ALLOW_EMPTY:${PN} = "1"

INSANE_SKIP:${PN} += "ldflags already-stripped textrel"

deltask compile_ptest_base

COMPATIBLE_HOST = "^(?!(qemu)?mips).*"
