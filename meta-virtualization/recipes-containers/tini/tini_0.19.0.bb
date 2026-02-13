HOMEPAGE = "http://github.com/krallin/tini"
SUMMARY = "Minimal init for containers"
DESCRIPTION = "Tini is the simplest init you could think of. All Tini does is \
spawn a single child (Tini is meant to be run in a container), and wait for \
it to exit all the while reaping zombies and performing signal forwarding. "

SRCREV = "b9f42a0e7bb46efea0c9e3d8610c96ab53b467f8"
SRC_URI = " \
  git://github.com/krallin/tini.git;branch=master;protocol=https \
  file://0001-Do-not-strip-the-output-binary-allow-yocto-to-do-thi.patch \
  file://0001-tini.c-a-function-declaration-without-a-prototype-is.patch \
  "

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ffc9091894702bc5dcf4cc0085561ef5"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

# tini links with -static, so no PIE for us
SECURITY_CFLAGS:pn-${PN} = "${SECURITY_NO_PIE_CFLAGS}"

inherit cmake

do_install() {
  mkdir -p ${D}/${bindir}
  install -m 0755 ${B}/tini-static ${D}/${bindir}/docker-init
}

# Tini is the currently the provider for docker-init
PROVIDES += "docker-init"
RPROVIDES:${PN} = "docker-init"
