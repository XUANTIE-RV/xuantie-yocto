SUMMARY = "The tiny Init service for HyperContainer"
DESCRIPTION = "The init Task for HyperContainer"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit autotools-brokensep 

SRC_URI = "git://github.com/hyperhq/hyperstart.git;branch=master;protocol=https"
SRC_URI += "file://0001-container.c-Fix-compiler-errors-that-gcc-8.1.0-repor.patch"

SRCREV = "c0c07d218b482dd07f9068b52a6e7468ae4172ac"
PV = "v0.2+git${SRCPV}"

S = "${WORKDIR}/git"

CACHED_CONFIGUREVARS = "ac_cv_file__usr_include_linux_vm_sockets_h=true"

do_install() {
	install -d ${D}/var/lib/hyper/

        install -m644 ${S}/build/hyper-initrd.img ${D}/var/lib/hyper/
        install -m644 ${S}/build/arch/x86_64/kernel ${D}/var/lib/hyper/
}

FILES:${PN} += "/var/lib/hyper"
