PV = "${GCC_VERSION}"
BINV = "${GCC_VERSION}"

require recipes-devtools/gcc/gcc-sanitizers.inc
inherit external-toolchain

# Undo various bits we don't want from the upstream include
EXTRA_OECONF = ""
BBCLASSEXTEND = ""
COMPILERDEP = ""

python () {
    gccs = d.expand('gcc-source-${PV}')

    lic_deps = d.getVarFlag('do_populate_lic', 'depends', True).split()
    d.setVarFlag('do_populate_lic', 'depends', ' '.join(filter(lambda d: d != '{}:do_unpack'.format(gccs), lic_deps)))

    cfg_deps = d.getVarFlag('do_configure', 'depends', True).split()
    d.setVarFlag('do_configure', 'depends', ' '.join(filter(lambda d: d != '{}:do_preconfigure'.format(gccs), cfg_deps)))
}

do_package[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
