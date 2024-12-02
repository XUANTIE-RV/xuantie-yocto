SUMMARY = "GNU cc and gcc C compilers"
HOMEPAGE = "http://gcc.org/"
DESCRIPTION = "GNU cc and gcc C compilers."
LICENSE = "CLOSED"

SRC_URI = "ftp://swftp01.damo.xrvm.cn/Test/Test/Toolschain/gnu-riscv/V3.0.0-rc2/gcc_14.1.1_riscv64-6.6.0-rv64gc-lp64d.deb;subdir=${BP}"
SRC_URI[sha256sum] = "c20ec13e7647b43a88e364bc7f069511617c4987398dbf8320fe65cb898ecb13"

PREFERRED_PROVIDER_libgcc = "libgcc-external"
PREFERRED_PROVIDER_libstdc++ = "libstdc++"
INSANE_SKIP_${PN} += "file-rdeps"
INSANE_SKIP:${PN} += "dev-so staticdev"
INSANE_SKIP:${PN} += "already-stripped"
INSANE_SKIP:${PN} += "installed-vs-shipped"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

FILES:${PN} += "\
      * \
"
PACKAGES = "${PN}"
do_install () {
    cp -r ${B}/* ${D}
    rm -rf ${D}/usr/bin/strings
}

VALGRINDARCH ?= "${TARGET_ARCH}"



