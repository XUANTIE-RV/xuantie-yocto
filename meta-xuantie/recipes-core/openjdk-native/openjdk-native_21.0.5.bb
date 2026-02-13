DESCRIPTION = "Java runtime based upon the OpenJDK Project, the community \
builds using source code from OpenJDK project"
LICENSE = "GPL-2.0-with-classpath-exception"
LIC_FILES_CHKSUM = "file://legal/java.base/LICENSE;md5=3e0b59f8fac05c3c03d4a26bbda13f8f"

SRC_URI[md5sum] = "9f332cf1123a653b7e6da96b9638a566"
SRC_URI = "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.5%2B11/OpenJDK21U-jdk_x64_linux_hotspot_21.0.5_11.tar.gz"

S = "${WORKDIR}/jdk-21.0.5+11"

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
	install -d ${D}${libdir}/jvm/openjdk-native
	cp -rf ${S}/* ${D}${libdir}/jvm/openjdk-native
}

inherit native
INHIBIT_SYSROOT_STRIP = "1"

python __anonymous() {
    if d.getVar("BUILD_ARCH") != "x86_64":
        msg =  "\nThe pre-build openjdk-21-native does not support %s host," % d.getVar("BUILD_ARCH")
        msg += "\nplease use the one in meta-java to replace,"
        msg += "\nadd meta-java to BBLAYERS in conf/bblayers.conf"
        raise bb.parse.SkipPackage(msg)
}
