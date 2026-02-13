DESCRIPTION = "WebDAV server protocol compliance test suite"
HOMEPAGE = "http://www.webdav.org/neon/litmus/"
SECTION = "tests"
LICENSE = "CLOSED"
inherit kernelsrc
inherit module
DEPENDS = "klitmus7-native"
INHIBIT_PACKAGE_DEBUG_SPLIT="1"
S = "${WORKDIR}/klitmus"
KLITMUS_SRC_DIR = "tools/memory-model/litmus-tests"
do_configure[prefuncs] += "copy_klitmus_source_from_kernel"
python copy_klitmus_source_from_kernel() {
    sources = d.getVar("KLITMUS_SRC_DIR")
    src_dir = d.getVar("STAGING_KERNEL_DIR")
    dest_dir = d.getVar("S")
    src = oe.path.join(src_dir, sources)
    dest = oe.path.join(dest_dir, sources)
    os.makedirs(dest,exist_ok=True)
    oe.path.copyhardlinktree(src, dest)
}
do_configure() {
    echo "do_configure"
}
do_compile:prepend() {
    export CROSS_COMPILE=${CCACHE}${HOST_PREFIX}
}
do_compile() {
    cd ${S}
    files=$(ls ${KLITMUS_SRC_DIR}/*.litmus)
    workdir=$(dirname $(which klitmus7))/../share/litmus
    cd $workdir
    echo "CROSS_COMPILE:${CROSS_COMPILE}"
    for f in $files
    do
        echo $f
        f=${f%.litmus}
        build_dir=${S}/build/$(basename $f)
        echo "build_dir: ${build_dir}"
        mkdir -p ${build_dir}
        klitmus7 -o ${build_dir} ${S}/${f}.litmus
        sed -i "s:/lib/modules/\$(shell uname -r)/build:${STAGING_KERNEL_DIR}:g" ${build_dir}/Makefile
        sed -i '2,3d' ${build_dir}/Makefile
        sed -i '3,6d' ${build_dir}/Makefile
        (cd ${build_dir}; oe_runmake ARCH=riscv CROSS_COMPILE="${CROSS_COMPILE}"; cd -)
    done
    echo "do_compile_finished"
}
KERNEL_VERSION = "6.6.36-yocto-standard+"
INSTALL_PATH = "${prefix}/../lib/modules/${KERNEL_VERSION}/kernel/klitmus"
do_install() {
    echo "do_install"
    mkdir -p ${D}${INSTALL_PATH}
    cp -r ${S}/build/* ${D}${INSTALL_PATH}
    rm -rf ${D}/usr
}
FILES:${PN} += "/lib /etc "
PACKAGES = "${PN}"
