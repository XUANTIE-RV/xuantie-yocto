SUMMARY = "OCaml compiler and runtime"
DESCRIPTION = "The OCaml programming language compiler and standard libraries."
HOMEPAGE = "https://ocaml.org/"
SECTION = "lang/ocaml"
LICENSE = "CLOSED"


inherit autotools

SRC_URI = "git://github.com/ocaml/ocaml.git;protocol=https;branch=4.13"
SRCREV = "bfc5de586cfd3d8e28e99473c80c5e1c9d13de6f"

S = "${WORKDIR}/git"
B = "${S}"

do_configure() {
    ./configure --prefix=${D}${base_prefix}
}

do_install() {
    oe_runmake 'DESTDIR=/' install
}

BBCLASSEXTEND = "native"

ERROR_QA:remove = " shebang-size"
