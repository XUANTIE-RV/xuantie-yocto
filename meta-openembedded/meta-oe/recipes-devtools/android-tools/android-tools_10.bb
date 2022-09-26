LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://NOTICE;md5=9e0b836a755c0780e75388254f9bb2e4"

DEPENDS = "libbsd libpcre zlib libcap"
DEPENDS_append_class-target = " openssl"

SRC_URI = " \
<<<<<<< HEAD
<<<<<<< HEAD
    git://git@gitlab.alibaba-inc.com/thead-linux/d1_adbd.git;branch=master;protocol=ssh \
=======
>>>>>>> Linux_SDK_V0.9.5
=======
    git://git@gitee.com/thead-yocto/d1_adbd.git;branch=master;protocol=http \
>>>>>>> fix compile issues
    file://adbd-new.mk;subdir=${BPN} \
    file://android-tools-adbd.service"

SRCREV="${AUTOREV}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/${BPN}"

# http://errors.yoctoproject.org/Errors/Details/133881/

inherit systemd

#SYSTEMD_PACKAGES = "${PN}-adbd"
#SYSTEMD_SERVICE_${PN}-adbd = "android-tools-adbd.service"
SYSTEMD_SERVICE_${PN} = "android-tools-adbd.service"

# Find libbsd headers during native builds
CC_append_class-native = " -I${STAGING_INCDIR}"
CC_append_class-nativesdk = " -I${STAGING_INCDIR}"

TOOLS = "adbd-new"

do_compile() {
    # Setting both variables below causing our makefiles to not work with
    # implicit make rules
    export SRCDIR=${S}
    case "${HOST_ARCH}" in
      arm)
        export android_arch=linux-arm
      ;;
      aarch64)
        export android_arch=linux-arm64
      ;;
      riscv64)
        export android_arch=linux-riscv64
      ;;
      mips|mipsel)
        export android_arch=linux-mips
      ;;
      mips64|mips64el)
        export android_arch=linux-mips64
      ;;
      powerpc|powerpc64)
        export android_arch=linux-ppc
      ;;
      i586|i686|x86_64)
        export android_arch=linux-x86
      ;;
    esac

    for tool in ${TOOLS}; do
      mkdir -p ${B}/${tool}
      cp -af ${S}/*.a ${B}/${tool}
      cp -af ${S}/adb_shell ${B}/${tool}
      cp -af ${S}/adb_profile ${B}/${tool}
      #oe_runmake
      oe_runmake -f ${B}/${tool}.mk -C ${B}/${tool}
    done
}

do_install() {
    bbplain "copy the adbd: ${B}, ${D}, ${base_bindir}"
    install -d ${D}${base_bindir}
    install -m 0755 ${B}/adbd-new/adbd ${D}${base_bindir}
    install -m 0755 ${B}/adbd-new/adb_shell ${D}${base_bindir}
    install -d ${D}${sysconfdir}
    install -m 0755 ${B}/adbd-new/adb_profile ${D}${sysconfdir}


    # Outside the if statement to avoid errors during do_package
    bbplain "try to copy adbd service"
    bbplain "bpn is ${BPN}, bn is ${PN}"

    install -D -p -m 0644 ${WORKDIR}/android-tools-adbd.service \
      ${D}${systemd_unitdir}/system/android-tools-adbd.service
}

#PACKAGES =+ "${PN}-fstools ${PN}-adbd"
PACKAGES =+ "${PN}-fstools"

RDEPENDS_${BPN} = "${BPN}-conf"
#RDEPENDS_${PN}-adbd = "${PN}-conf"
RDEPENDS_${PN}-fstools = "bash"

#FILES_${PN}-adbd = "\
#    ${bindir}/adbd \
#    ${systemd_unitdir}/system/android-tools-adbd.service \
#"

BBCLASSEXTEND = "cross"
