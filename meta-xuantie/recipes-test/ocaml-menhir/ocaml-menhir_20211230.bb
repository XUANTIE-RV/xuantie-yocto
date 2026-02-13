SUMMARY = "Menhir parser generator for OCaml"
DESCRIPTION = "A LR(1) parser generator for the OCaml language."
HOMEPAGE = "https://gitlab.inria.fr/fpottier/menhir"
LICENSE = "CLOSED"


DEPENDS = "ocaml-native ocaml-dune-native"
do_configure[depends] += "ocaml-native:do_install"

SRC_URI = "https://gitlab.inria.fr/fpottier/menhir/-/archive/${PV}/menhir-${PV}.tar.gz"

SRC_URI[sha256sum] = "2cb08d3768ea6617c8cd16fc07130d987c58905ce630ba4106d57b279f373099"

S = "${WORKDIR}/menhir-${PV}"

# inherit host


do_compile() {
   dune build @install
}

do_install() {
   dune install --prefix=${D}${base_prefix}
}

BBCLASSEXTEND = "native"
