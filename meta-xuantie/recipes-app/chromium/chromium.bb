SUMMARY = "chromium"
DESCRIPTION = "chromium"
HOMEPAGE = "https://codeup.aliyun.com/t-head_linux_sdk/th1520_linux_sdk_private/"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = ""

PV = "109"
PR = "r14"

# check tags in HOMEPAGE above, should be in format below, for example: "chromium_109-r4"
SRCREV = "chromium_${PV}-${PR}"

SRC_URI = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/Chromium/chromium_109-r14.tar.gz"
SRC_URI[sha256sum] = "0bd930db6caf8abf79b174742787c13132cb198404949cbca6b6bdd87b523a93"
S = "${WORKDIR}/chromium_109_prebuilt_linux"

do_compile() {
}

do_install() {
    install -d ${D}/opt
    install -d ${D}/opt/chromium

    cp -rf ${S}/bin                          ${D}/opt/chromium
    cp -rf ${S}/run-*.sh                     ${D}/opt/chromium

    rm -f ${D}/opt/chromium/bin/*test*
}

FILES:${PN} += " ${bindir} "
FILES:${PN} += " ${includedir} "
FILES:${PN} += " ${libdir} "
FILES:${PN} += " /opt "

PROVIDES = "${PN}"
PACKAGES = "${PN}"
#DEPENDS = "make-chromium-sysroot"
DEPENDS = "nss libdrm libxkbcommon glib-2.0 dbus atk pango cairo at-spi2-atk pipewire pciutils curl libx11 libxcomposite libffi libomxil"
RDEPENDS:${PN} += " libomxil-dev"

INSANE_SKIP:${PN} = "debug-files file-rdeps installed-vs-shipped already-stripped textrel dev-deps"
