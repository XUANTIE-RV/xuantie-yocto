HOMEPAGE = "git://github.com/kubernetes/kubernetes;branch=master;protocol=https"
SUMMARY = "Production-Grade Container Scheduling and Management"
DESCRIPTION = "Kubernetes is an open source system for managing containerized \
applications across multiple hosts, providing basic mechanisms for deployment, \
maintenance, and scaling of applications. \
"

PV = "v1.24.0+git${SRCREV_kubernetes}"
SRCREV_kubernetes = "8b1b4db3834ddf7cf1b97137180f413cb9e2186f"
SRCREV_kubernetes-release = "7c1aa83dac555de6f05500911467b70aca4949f0"
PE = "1"

BBCLASSEXTEND = "devupstream:target"
LIC_FILES_CHKSUM:class-devupstream = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
DEFAULT_PREFERENCE:class-devupstream = "-1"
SRC_URI:class-devupstream = "git://github.com/kubernetes/kubernetes.git;branch=master;name=kubernetes;protocol=https \
                             git://github.com/kubernetes/release;branch=master;name=kubernetes-release;destsuffix=git/release;protocol=https \
                            "
SRCREV_kubernetes:class-devupstream = "d2f6eb6339de25cef04850b6d9be8335d52324cd"
SRCREV_kubernetes-release:class-devupstream = "7c1aa83dac555de6f05500911467b70aca4949f0"
PV:class-devupstream = "v1.23-alpha+git${SRCPV}"

SRCREV_FORMAT ?= "kubernetes_release"

SRC_URI = "git://github.com/kubernetes/kubernetes.git;branch=release-1.24;name=kubernetes;protocol=https;destsuffix=git/src/github.com/kubernetes/kubernetes \
           git://github.com/kubernetes/release;branch=master;name=kubernetes-release;destsuffix=git/release;protocol=https"

SRC_URI:append = " \
           file://0001-hack-lib-golang.sh-use-CC-from-environment.patch \
           file://0001-cross-don-t-build-tests-by-default.patch \
           file://0001-build-golang.sh-convert-remaining-go-calls-to-use.patch \
           file://cni-containerd-net.conflist \
           file://k8s-init \
           file://99-kubernetes.conf \
          "

DEPENDS += "rsync-native \
            coreutils-native \
            go-native \
           "

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

GO_IMPORT = "import"
S = "${WORKDIR}/git/src/github.com/kubernetes/kubernetes"

inherit systemd
inherit go
inherit goarch
inherit cni_networking

COMPATIBLE_HOST = '(x86_64.*|arm.*|aarch64.*|riscv64.*)-linux'

do_compile() {
	export GOPATH="${S}/src/import/.gopath:${S}/src/import/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go:${WORKDIR}/git/"
	cd ${S}

	# Build the host tools first, using the host compiler
	export GOARCH="${BUILD_GOARCH}"
	# Pass the needed cflags/ldflags so that cgo can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CFLAGS="${BUILD_CFLAGS}"
	export LDFLAGS="${BUILD_LDFLAGS}"
	export CGO_CFLAGS="${BUILD_CFLAGS}"
	# as of go 1.15.5, there are some flags the CGO doesn't like. Rather than
	# clearing them all, we sed away the ones we don't want.
	export CGO_LDFLAGS="$(echo ${BUILD_LDFLAGS} | sed 's/-Wl,-O1//g' | sed 's/-Wl,--dynamic-linker.*?\( \|$\)//g')"
	export CC="${BUILD_CC}"
	export LD="${BUILD_LD}"

	make generated_files GO="go" KUBE_BUILD_PLATFORMS="${HOST_GOOS}/${BUILD_GOARCH}"

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

	# to limit what is built, use 'WHAT', i.e. make WHAT=cmd/kubelet
	make cross CGO_FLAGS=${CGO_FLAGS} GO=${GO} KUBE_BUILD_PLATFORMS=${GOOS}/${GOARCH} GOLDFLAGS=""
}

do_install() {
    install -d ${D}${bindir}

    install -d ${D}${sysconfdir}/kubernetes/manifests/

    install -m 755 -D ${S}/_output/local/go/bin/${TARGET_GOOS}_${TARGET_GOARCH}/* ${D}/${bindir}

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
	install -d "${D}${BIN_PREFIX}/bin"
	install -m 755 "${WORKDIR}/k8s-init" "${D}${BIN_PREFIX}/bin"

	install -d ${D}${sysconfdir}/sysctl.d
	install -m 0644 "${WORKDIR}/99-kubernetes.conf" "${D}${sysconfdir}/sysctl.d"
    fi
}

CNI_NETWORKING_FILES ?= "${WORKDIR}/cni-containerd-net.conflist"

PACKAGES =+ "kubeadm kubectl kubelet kube-proxy ${PN}-misc ${PN}-host"

ALLOW_EMPTY:${PN} = "1"
INSANE_SKIP:${PN} += "ldflags already-stripped"
INSANE_SKIP:${PN}-misc += "ldflags already-stripped textrel"

# Note: we are explicitly *not* adding docker to the rdepends, since we allow
#       backends like cri-o to be used.
RDEPENDS:${PN} += "kubeadm \
                   kubectl \
                   kubelet \
                   kubernetes-cni"

RDEPENDS:kubeadm = "kubelet kubectl cri-tools conntrack-tools"
FILES:kubeadm = "${bindir}/kubeadm"

RDEPENDS:kubelet = "iptables socat util-linux ethtool iproute2 ebtables iproute2-tc"
FILES:kubelet = "${bindir}/kubelet"

FILES:kubectl = "${bindir}/kubectl"
FILES:kube-proxy = "${bindir}/kube-proxy"
FILES:${PN}-misc = "${bindir} ${sysconfdir}/sysctl.d"

ALLOW_EMPTY:${PN}-host = "1"
FILES:${PN}-host = "${BIN_PREFIX}/bin/k8s-init"
RDEPENDS:${PN}-host = "${PN}"

RRECOMMENDS:${PN} = "\
                     kernel-module-xt-addrtype \
                     kernel-module-xt-nat \
                     kernel-module-xt-multiport \
                     kernel-module-xt-conntrack \
                     kernel-module-xt-comment \
                     kernel-module-xt-mark \
                     kernel-module-xt-connmark \
                     kernel-module-vxlan \
                     kernel-module-xt-masquerade \
                     kernel-module-xt-statistic \
                     kernel-module-xt-physdev \
                     kernel-module-xt-nflog \
                     kernel-module-xt-limit \
                     kernel-module-nfnetlink-log \
                     "

deltask compile_ptest_base
