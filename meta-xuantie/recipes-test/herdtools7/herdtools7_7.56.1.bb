DESCRIPTION = "This is herdtools7, a tool suite to test weak memory models"
HOMEPAGE = "https://github.com/herd/herdtools7"
LICENSE = "CLOSED"

SRC_URI = "git://github.com/herd/herdtools7.git;branch=7.56.1-release;protocol=https"
SRCREV = "f544fd4f8d27c2b2c2a0441ab5e8fe5cfd34c2cd"

S = "${WORKDIR}/git"


DEPENDS += "ocaml-native ocaml-dune-native ocaml-menhir-native bc-native coreutils-native"
do_configure[depends] += "ocaml-native:do_install"

do_compile() {
	oe_runmake all PREFIX=${D}${base_prefix}
}

do_install() {
	oe_runmake install PREFIX=${D}${base_prefix}
}

SYSROOT_DIRS:append:class-native = " ${base_prefix}"

BBCLASSEXTEND = "native"
