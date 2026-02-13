XUANTIE_COMPUTE_LIB_NAME ??= "none"
XUANTIE_COMPUTE_LIB_VER ??= "V1.0.0"
XUANTIE_CPU_TYPE ??= "none"
XUANTIE_VECTOR_LEN ??= "128"
XUANTIE_COMPUTE_LIB_DEPENDS ??= ""
XUANTIE_COMPUTE_LIB_HAVE_APP_PACKAGE ??= "yes"

PACKAGE_ARCH:xuantie-buildmode-prebuilt:class-target = "${XUANTIE_MCPU_PACKAGE_ARCH}"

XUANTIE_BUILD_MODE ??= "${@bb.utils.contains('XUANTIE_COMPUTE_LIB_NAME', 'none', 'source', 'prebuilt', d)}"
OVERRIDES =. "${@['', 'xuantie-buildmode-${XUANTIE_BUILD_MODE}:']['${XUANTIE_BUILD_MODE}' != '']}"
OVERRIDES[vardepsexclude] += "XUANTIE_BUILD_MODE"

XUANTIE_PACKAGE_COMMON_NAME = "${XUANTIE_COMPUTE_LIB_NAME}"
PR:xuantie-buildmode-prebuilt:class-target = "${XUANTIE_COMPUTE_LIB_VER}"
XUANTIE_COMPUTE_LIB_BASE_URI ??= "https://test.com/dir"
SRC_URI:xuantie-buildmode-prebuilt:class-target = " \
        ${@bb.utils.contains('XUANTIE_COMPUTE_LIB_HAVE_APP_PACKAGE', 'yes', \
        '${XUANTIE_COMPUTE_LIB_BASE_URI}/${XUANTIE_COMPUTE_LIB_NAME}/${XUANTIE_COMPUTE_LIB_VER}/${XUANTIE_COMPUTE_LIB_NAME}-${XUANTIE_COMPUTE_LIB_VER}-${XUANTIE_CPU_TYPE}-${XUANTIE_VECTOR_LEN}-app.tar.gz;subdir=git;sha256sum=;md5sum=;', '', d)} \
        ${XUANTIE_COMPUTE_LIB_BASE_URI}/${XUANTIE_COMPUTE_LIB_NAME}/${XUANTIE_COMPUTE_LIB_VER}/${XUANTIE_COMPUTE_LIB_NAME}-${XUANTIE_COMPUTE_LIB_VER}-${XUANTIE_CPU_TYPE}-${XUANTIE_VECTOR_LEN}-lib.tar.gz;subdir=git;sha256sum=;md5sum=; \
"
S:xuantie-buildmode-prebuilt:class-target = "${WORKDIR}/git"
BB_STRICT_CHECKSUM:xuantie-buildmode-prebuilt:class-target = "0"

LICENSE:xuantie-buildmode-prebuilt:class-target = "CLOSED"
LIC_FILES_CHKSUM:xuantie-buildmode-prebuilt:class-target = ""

PACKAGECONFIG:xuantie-buildmode-prebuilt:class-target = ""
DEPENDS:xuantie-buildmode-prebuilt:class-target = ""

inherit xuantie-package-deb
PACKAGES:xuantie-buildmode-prebuilt:class-target = "${XUANTIE_PACKAGE_COMMON_NAME}-app ${XUANTIE_PACKAGE_COMMON_NAME}-lib ${XUANTIE_PACKAGE_COMMON_NAME}"
RDEPENDS:${XUANTIE_PACKAGE_COMMON_NAME}-app:xuantie-buildmode-prebuilt:class-target = ""
RDEPENDS:${XUANTIE_PACKAGE_COMMON_NAME}-lib:xuantie-buildmode-prebuilt:class-target = ""
RDEPENDS:${XUANTIE_PACKAGE_COMMON_NAME}:xuantie-buildmode-prebuilt:class-target = ""
EXTRA_DEB_DEPENDS:${XUANTIE_PACKAGE_COMMON_NAME}-app:xuantie-buildmode-prebuilt:class-target = "${XUANTIE_COMPUTE_LIB_DEPENDS}"
EXTRA_DEB_DEPENDS:${XUANTIE_PACKAGE_COMMON_NAME}-lib:xuantie-buildmode-prebuilt:class-target = "${XUANTIE_COMPUTE_LIB_DEPENDS}"
EXTRA_DEB_DEPENDS:${XUANTIE_PACKAGE_COMMON_NAME}:xuantie-buildmode-prebuilt:class-target = "${XUANTIE_PACKAGE_COMMON_NAME}-app ${XUANTIE_PACKAGE_COMMON_NAME}-lib"
FILES:${XUANTIE_PACKAGE_COMMON_NAME}-app = "${datadir}/${XUANTIE_COMPUTE_LIB_NAME}/app_lib/"
FILES:${XUANTIE_PACKAGE_COMMON_NAME}-lib = "/usr"
ALLOW_EMPTY:${XUANTIE_PACKAGE_COMMON_NAME}-app:xuantie-buildmode-prebuilt:class-target = "1"
ALLOW_EMPTY:${XUANTIE_PACKAGE_COMMON_NAME}-lib:xuantie-buildmode-prebuilt:class-target = "1"
ALLOW_EMPTY:${XUANTIE_PACKAGE_COMMON_NAME}:xuantie-buildmode-prebuilt:class-target = "1"
WARN_QA:remove:xuantie-buildmode-prebuilt:class-target = "file-rdeps build-deps staticdev debug-files installed-vs-shipped already-stripped dev-elf dev-deps dev-so"
ERROR_QA:remove:xuantie-buildmode-prebuilt:class-target = "file-rdeps build-deps staticdev debug-files installed-vs-shipped already-stripped dev-elf dev-deps dev-so"

do_patch:xuantie-buildmode-prebuilt:class-target() {
}

do_git:xuantie-buildmode-prebuilt:class-target() {
    exit 0
}

do_unpack_extra:prepend:xuantie-buildmode-prebuilt:class-target() {
    exit 0
}

do_configure:prepend:xuantie-buildmode-prebuilt:class-target() {
    exit 0
}

do_compile:prepend:xuantie-buildmode-prebuilt:class-target() {
    exit 0
}

do_install:prepend:xuantie-buildmode-prebuilt:class-target() {
    install -d ${D}/usr ${D}${datadir}/${XUANTIE_COMPUTE_LIB_NAME}
    cp -rfd ${S}/install_lib/* ${D}/usr
    cp -rfd ${S}/app_lib ${D}${datadir}/${XUANTIE_COMPUTE_LIB_NAME} || true
    exit 0
}

do_compile_ptest:prepend:xuantie-buildmode-prebuilt:class-target() {
    exit 0
}

do_install_ptest:prepend:xuantie-buildmode-prebuilt:class-target() {
    exit 0
}

binconfig_disabled_sysroot_preprocess:prepend:xuantie-buildmode-prebuilt:class-target() {
    exit 0
}
