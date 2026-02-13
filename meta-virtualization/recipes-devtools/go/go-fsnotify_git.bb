DESCRIPTION = "A golang registry for global request variables."
HOMEPAGE = "https://github.com/go-fsnotify/fsnotify"
SECTION = "devel/go"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=68f2948d3c4943313d07e084a362486c"

SRCNAME = "fsnotify"

PKG_NAME = "github.com/fsnotify/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;branch=main;protocol=https"

SRCREV = "af855d7e6014ec848882bb2ed7d40d536872d275"
PV = "v1.5.1+git${SRCPV}"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_fsnotify_sysroot_preprocess"

go_fsnotify_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES:${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
