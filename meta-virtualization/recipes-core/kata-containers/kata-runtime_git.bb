DESCRIPTION = " The Command-Line Interface (CLI) part of the Kata Containers runtime component"
HOMEPAGE = "https://github.com/kata-containers/runtime"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/github.com/kata-containers/runtime/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

GO_IMPORT = "github.com/kata-containers/runtime"
SRCREV = "04c77eb20e9bd603cab5c711bcbe7c69db58b040"
SRC_URI = "git://${GO_IMPORT}.git;branch=master;protocol=https \
           file://0001-makefile-allow-SKIP_GO_VERSION_CHECK-to-be-overriden.patch \
          "
RDEPENDS:${PN}-dev:append = "bash"

CONTAINER_KERNEL ?= ""
CONTAINER_INITRD ?= ""
RDEPENDS:${PN} = " \
                 qemu \
                 ${CONTAINER_KERNEL} \
		 ${CONTAINER_INITRD} \
                 "
DEPENDS += "yq-native"

SKIP_RECIPE[kata-runtime] ?= "kata containers are currently broken, patches accepted"

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

	export SKIP_GO_VERSION_CHECK="1"
	oe_runmake runtime
}

do_install() {
	mkdir -p ${D}/${bindir}
	cp ${WORKDIR}/git/src/${GO_IMPORT}/kata-runtime ${D}/${bindir}
	
	mkdir -p ${D}/${datadir}/defaults/kata-containers/
	cp ${WORKDIR}/git/src/${GO_IMPORT}/cli/config/configuration-qemu.toml ${D}/${datadir}/defaults/kata-containers/configuration.toml

	sed -e 's|/usr/bin/qemu-lite-system-x86_64|/usr/bin/qemu-system-x86_64|' -i ${D}/${datadir}/defaults/kata-containers/configuration.toml
	sed -e 's|/usr/share/kata-containers/vmlinuz.container|/var/lib/hyper/kernel|' -i ${D}/${datadir}/defaults/kata-containers/configuration.toml
	sed -e 's|/usr/share/kata-containers/kata-containers-initrd.img|/var/lib/hyper/hyper-initrd.img|' -i ${D}/${datadir}/defaults/kata-containers/configuration.toml
	sed -e 's/^\(image =.*\)/# \1/g' -i ${D}/${datadir}/defaults/kata-containers/configuration.toml
	
	# TODO: modify the config file for the configured kernel and fix the location of the qemu-system-binary

	# /usr/share/defaults/kata-containers/configuration.toml: file /usr/libexec/kata-containers/kata-shim does not exist
	# fork/exec /usr/libexec/kata-containers/kata-proxy: no such file or directory
}

FILES:${PN} += "${datadir}/defaults/kata-containers/*"

deltask compile_ptest_base
