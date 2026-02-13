SUMMARY = "Dune build system for OCaml"
DESCRIPTION = "A fast build system for OCaml projects."
HOMEPAGE = "https://dune.build/"
LICENSE = "CLOSED"

SRC_URI = "git://github.com/ocaml/dune.git;protocol=https;branch=3.19"
SRCREV = "76c0c3941798f81dcc13a305d7abb120c191f5fa"

S = "${WORKDIR}/git"

DEPENDS += "ocaml-native"
do_configure[depends] += "ocaml-native:do_install"

# inherit host

do_compile() {
    oe_runmake release PREFIX=${D}${base_prefix}
}

do_install() {
    oe_runmake install PREFIX=${D}${base_prefix}
}


BBCLASSEXTEND = "native"
