SUMMARY = "Low-overhead sampling profiler for Java and native code"
DESCRIPTION = "Async-profiler v4.1 with Java & native profiling support"
HOMEPAGE = "https://github.com/async-profiler/async-profiler"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI = "https://github.com/async-profiler/async-profiler/archive/refs/tags/v${PV}.tar.gz"
SRC_URI[md5sum] = "6bb0712f306446c950773f708ca7d04e"

S = "${WORKDIR}/async-profiler-${PV}"

DEPENDS = "openjdk-native openjdk"

def get_ap_arch(d):
    arch = d.getVar('TARGET_ARCH')
    if arch == "aarch64": return "aarch64"
    elif arch == "arm": return "arm"
    elif arch == "x86_64": return "x64"
    elif arch == "riscv64": return "riscv"
    elif arch == "ppc64le": return "ppc64le"
    else: bb.fatal("Unsupported architecture: %s" % arch)

AP_ARCH = "${@get_ap_arch(d)}"

export JAVA_HOME="${STAGING_DIR_NATIVE}/usr/lib/jvm/openjdk-native"
export jardir="/usr/jar"

EXTRA_OEMAKE = " \
    ARCH=${AP_ARCH} \
    USE_LIBCXX=1 \
    TARGET=${TARGET_OS} \
    DEBUG=0 \
"
do_compile() {
    oe_runmake -C ${S}
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${jardir}
    install -d ${D}${includedir}

    install -m 755 ${S}/build/bin/* ${D}${bindir}/
    install -m 644 ${S}/build/include/* ${D}${includedir}
    install -m 755 ${S}/build/lib/* ${D}${libdir}/
    install -m 644 ${S}/build/jar/* ${D}${jardir}/
}

FILES:${PN} += " ${bindir}/* ${libdir}/* ${includedir}/* ${jardir}/* "

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
PACKAGES = "${PN}"
COMPATIBLE_HOST = "(aarch64|x86_64|arm|riscv64|ppc64le).*-linux"
INSANE_SKIP:${PN} += "already-stripped"
