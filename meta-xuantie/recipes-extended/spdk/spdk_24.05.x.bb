SUMMARY = "Storage Performance Development Kit"
HOMEPAGE = "https://github.com/spdk/spdk"
LICENSE = "BSD-2-Clause & BSD-3-Clause & Apache-2.0 & GPL-2.0-only & LGPL-2.1-or-later & MIT"
LIC_FILES_CHKSUM = " \
    file://dpdk/license/bsd-2-clause.txt;md5=da644e998d2ceb0da951ec93a707630b \
    file://dpdk/license/bsd-3-clause.txt;md5=0f00d99239d922ffd13cabef83b33444 \
    file://dpdk/license/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://dpdk/license/isc.txt;md5=aa0f0e6edaff67e63df66799b8d36824 \
    file://dpdk/license/lgpl-2.1.txt;md5=4b54a1fd55a448865a0b32d41598759d \
    file://dpdk/license/mit.txt;md5=bc15373c12d4e178cb41c0a736b68c2b \
"

SRC_URI = " \
    gitsm://github.com/spdk/spdk.git;branch=v24.05.x;protocol=https \
    file://spdk_24.05.x_rv_cross_compile_1218.patch \
"
SRCREV = "796b5908f7fa7e80d29af02095bc38b409c1b835"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

B = "${S}"

CONFIGUREOPTS = " \
    --target-arch=rv64imafdc_zfh \
    --cross-prefix=riscv64-unknown-linux-gnu \
    --prefix=${prefix} \
    --libdir=${libdir} \
    --without-nvme-cuse \
    --without-dpdk-compressdev \
    --sysroot=${STAGING_DIR_TARGET}"
DISABLE_STATIC:remove = "--disable-static"

DEPENDS += "openssl util-linux-libuuid cunit libaio libiscsi \
    meson-native ninja-native qemu-native patchelf-native \
    python3-pip-native python3-pyelftools-native"

HOST_CC_ARCH:remove = "-target ${HOST_SYS}"

do_configure:prepend() {
    ln -sf /usr/bin/ps ${STAGING_DIR_NATIVE}${bindir}
    ln -srf ${STAGING_DIR_NATIVE}${bindir}/pip3 ${STAGING_DIR_NATIVE}${bindir}/pip
    cat > ${S}/dpdk/config/riscv/riscv64_llvm_config << EOF
[binaries]
c = 'clang'
cpp = 'clang++'
ar = 'llvm-ar'
strip = 'llvm-strip'

[host_machine]
system = 'linux'
cpu_family = 'riscv64'
cpu = 'rv64gcv'
endian = 'little'

[properties]
platform = 'generic'
vendor_id = 'generic'
arch_id = 'generic'
c_args = ['-I<numa_install_dir>/include']
c_link_args = ['-L<numa_install_dir>/lib']
EOF
}

do_install:append() {
    sed -i 's|\(Libs: -L\)/[^ ]*/lib|\1${libdir}|' ${D}${libdir}/pkgconfig/spdk_dpdklibs.pc
    mkdir -p ${D}${datadir}/dpdk
    cp ${B}/build/examples/* ${D}${datadir}/dpdk
}

PACKAGES += "${PN}-examples"
FILES:${PN}-dev += "${libdir}/dpdk/pmds-24.1/*.so"
FILES:${PN} += "${libdir}/python3.10 ${libdir}/dpdk/pmds-24.1"
FILES:${PN}-examples = "${datadir}"
RDEPENDS:${PN} += "pciutils python3-core"
RDEPENDS:${PN}-examples += "${PN} bash"

COMPATIBLE_HOST = '(riscv64).*-linux'
