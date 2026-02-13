DESCRIPTION = "Agent run inside a virtual machine, which spawns containers and processes"
HOMEPAGE = "https://github.com/kata-containers/agent"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/github.com/kata-containers/agent/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

GO_IMPORT = "github.com/kata-containers/agent"
SRCREV = "e03f7d7453fabffb17e1540f28666c26178d3cbf"
SRC_URI = "git://${GO_IMPORT}.git;branch=master;protocol=https \
          "

RDEPENDS:${PN}-dev:append = "bash"

SKIP_RECIPE[kata-agent] ?= "kata containers are currently broken, patches accepted"

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
	oe_runmake kata-agent
}

do_install() {
	mkdir -p ${D}/${bindir}/
	cp ${WORKDIR}/git/src/${GO_IMPORT}/kata-agent ${D}/${bindir}

	mkdir -p ${D}/${systemd_unitdir}/system
	cp ${WORKDIR}/git/src/${GO_IMPORT}/kata-agent.service ${D}/${systemd_unitdir}/system
}

deltask compile_ptest_base

FILES:${PN} += "${systemd_unitdir}/*"
