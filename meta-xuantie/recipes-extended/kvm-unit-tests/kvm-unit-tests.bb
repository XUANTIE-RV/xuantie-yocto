SUMMARY = "kvm-unit-tests"
DESCRIPTION = "Lightweight emulation-based unit test framework for KVM"
HOMEPAGE = "https://github.com/clementleger/kvm-unit-tests"
SECTION = "devel"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PV = "1.0"
PR = "r4"

SRC_URI = "git://github.com/clementleger/kvm-unit-tests.git;branch=dev/cleger/dbltrp_v1;protocol=https \
           file://0001-fix-build-depends-error.patch \
           file://0002-Rewrite-the-puts-in-io.c-using-opensbi-output.patch "
SRCREV = "3715df0b46700e34aeb0b1f6c61ff1f28ba62fed"

S = "${WORKDIR}/git"
inherit autotools

KVM_UNIT_TESTS_CONFIG = "--arch=riscv64 --cross-prefix=riscv64-linux-"
KVM_UNIT_TESTS_CONFIG:append:toolchain-clang = " --cc=clang"
do_configure() {
    cd ${S}
    ./configure ${KVM_UNIT_TESTS_CONFIG}
}

do_compile() {
    # do not transfer LDFLAGS, which finished in source code
    export LDFLAGS=""
    echo ***********LDFLAGS****************=$LDFLAGS

    cd ${S}
    make
}

do_install() {
    install -d ${D}/${bindir}/kvm-unit-tests
    install -d ${D}/${bindir}/kvm-unit-tests/lib
    install -d ${D}/${bindir}/kvm-unit-tests/lib/libfdt
    install -d ${D}/${bindir}/kvm-unit-tests/riscv
    install -d ${D}/${bindir}/kvm-unit-tests/scripts

    cp -rfL ${S}/lib/asm                ${D}/${bindir}/kvm-unit-tests/lib/
    cp -rfL ${S}/lib/config.h           ${D}/${bindir}/kvm-unit-tests/lib/
    cp -rfL ${S}/lib/libcflat.a         ${D}/${bindir}/kvm-unit-tests/lib/
    cp -rfL ${S}/lib/libfdt/libfdt.a    ${D}/${bindir}/kvm-unit-tests/lib/libfdt/
    cp -rfL ${S}/riscv/*.elf            ${D}/${bindir}/kvm-unit-tests/riscv
    cp -rfL ${S}/riscv/*.flat           ${D}/${bindir}/kvm-unit-tests/riscv
    cp -rfL ${S}/config.mak             ${D}/${bindir}/kvm-unit-tests/
    cp -rfL ${S}/scripts/arch-run.bash  ${D}/${bindir}/kvm-unit-tests/scripts/

    cp -rfL ${S}/riscv/run              ${D}/${bindir}/kvm-unit-tests/riscv
    cd ${D}/${bindir}/kvm-unit-tests/ && ln -s riscv/run riscv-runs; cd -
}

FILES_${PN} = "${bindir}"
INSANE_SKIP:${PN} += "staticdev"
