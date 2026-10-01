SUMMARY = "User space components of the Ceph file system"
LICENSE = "LGPL-2.1-only & GPL-2.0-only & Apache-2.0 & MIT"
LIC_FILES_CHKSUM = "file://COPYING-LGPL2.1;md5=fbc093901857fcd118f065f900982c24 \
                    file://COPYING-GPL2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING;md5=5351120989d78252e65dc1a2a92e3617 \
"
inherit cmake pkgconfig python3native python3-dir systemd useradd
# Disable python pybind support for ceph temporary, when corss compiling pybind,
# pybind mix cmake and python setup environment, would case a lot of errors.

SRC_URI = " \
        http://download.ceph.com/tarballs/ceph-${PV}.tar.gz \
        file://0001-fix-host-library-paths-were-used.patch \
        file://ceph.conf \
        file://0001-avoid-to_string-error.patch \
        file://0001-delete-install-layout-deb.patch \
        file://0001-cephadm-build.py-avoid-using-python3-from-sysroot-wh.patch \
        file://0001-src-common-crc32c_intel_fast.patch \
        file://0003-src-common-bitstr.h.patch \
        file://0008-cmake-modules-Finduring.cmake.patch \
        file://0010-CET-Add-CET-marker-to-crc32c_intel_fast_zero_asm.s.patch \
        file://0011-isa-l-CET-Add-CET-marker-to-x86-64-crc32-assembly-co.patch \
        file://0012-spdk-isa-l-CET-Add-CET-marker-to-x86-64-crc32-assemb.patch \
        file://0016-src-tracing-patch \
        file://0017-gcc-12-omnibus.patch \
        file://0018-src-rgw-store-dbstore-CMakeLists.txt.patch \
        file://0020-src-arrow-cpp-cmake_modules-ThirdpartyToolchain.cmake.patch \
        file://0024-gcc-13.patch \
        file://0029-src-rgw-rgw_amqp.cc.patch \
        file://0032-cmake-modules-BuildBoost.cmake.patch \
        file://0033-boost-asm.patch \
        file://0034-src-pybind-rbd-rbd.pyx.patch \
        file://0035-src-CMakeLists.txt.patch \
        file://0036-18.2.1.release.patch \
        file://ceph-c99-2.patch \
        file://0039-src-common-dout.h.patch \
        file://0040-add-loongarch64-support-for-ceph-18.2.1.patch \
        file://0041-fix-close-can-not-be-find-in-TUDPTransport.cc.patch \
        file://0042-add-riscv64-support-for-ceph-18.2.1.patch \
        file://0043-bugfix-for-CVE-2025-52555.patch \
        file://0044-tracing-Fix-C-type-errors-in-librados-tracing.patch \
        file://0001-pybind-fix-callback-function-type-compatibility-for-.patch \
"

SRC_URI[sha256sum] = "8075b03477f42ad23b1efd0cc1a0aa3fa037611fc059a91f5194e4b51c9d764a"

DEPENDS = " \
        boost bzip2 curl cryptsetup expat gperf-native \
        keyutils libaio lua lz4 \
        nspr nss ninja-native \
        oath openldap openssl \
        python3 python3-native python3-cython-native python3-pyyaml-native \
        rabbitmq-c rocksdb snappy thrift udev \
        zlib libgcc zstd re2 \
        libnl \
"
# 不带 valgrind：仅用其头文件做可选 helgrind 注解（无则退化为空宏），
# WITH_BOOST_VALGRIND 在 WITH_SYSTEM_BOOST=ON 下恒 OFF，不值得为它整包构建


# Bundled rocksdb sub-build: BuildRocksDB.cmake re-invokes cmake with
# -DCMAKE_C_FLAGS=-Wno-* on the command line, which overrides the CFLAGS from
# the (FORCE-less) toolchain file cache entry — -mcpu carried by CFLAGS is
# lost.  The compiler variable is forwarded intact, so bake ${HOST_CC_ARCH}
# (-mcpu=<cpu> in CPU mode) into it alongside the existing --sysroot
# (spec 2026-08-05 §5 evidence chain, option A).
# The baked string is multi-word, and python 3.13 setuptools passes everything
# after the first word of env CC to the compiler as ONE argv element (cython
# rados.pyx build fails: cc1 "unknown CPU") — ship it as single-token wrapper
# scripts instead; flag content is unchanged.
OECMAKE_C_COMPILER = "${WORKDIR}/xuantie-oe-cc"
OECMAKE_CXX_COMPILER = "${WORKDIR}/xuantie-oe-cxx"

do_configure:prepend() {
    cat > ${WORKDIR}/xuantie-oe-cc <<EOF
#!/bin/sh
exec ${@oecmake_map_compiler('CC', d)[0]} ${HOST_CC_ARCH} --sysroot=${RECIPE_SYSROOT} "\$@"
EOF
    cat > ${WORKDIR}/xuantie-oe-cxx <<EOF
#!/bin/sh
exec ${@oecmake_map_compiler('CXX', d)[0]} ${HOST_CC_ARCH} --sysroot=${RECIPE_SYSROOT} "\$@"
EOF
    chmod +x ${WORKDIR}/xuantie-oe-cc ${WORKDIR}/xuantie-oe-cxx
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --user-group --home-dir /var/lib/ceph --shell /sbin/nologin ceph"

SYSTEMD_SERVICE:${PN} = " \
        ceph-radosgw@.service \
        ceph-radosgw.target \
        ceph-mon@.service \
        ceph-mon.target \
        ceph-mds@.service \
        ceph-mds.target \
        ceph-osd@.service \
        ceph-osd.target \
        cephfs-mirror@.service \
        cephfs-mirror.target \
        ceph.target \
        ceph-rbd-mirror@.service \
        ceph-rbd-mirror.target \
        ceph-volume@.service \
        ceph-mgr@.service \
        ceph-mgr.target \
        ceph-crash.service \
        rbdmap.service \
        ceph-immutable-object-cache@.service \
        ceph-immutable-object-cache.target \
"

# -DWITH_PYTHON3=${PYTHON_BASEVERSION}: ceph 18.2.1 bundles an old
# cmake/modules/FindPython (versions list tops out at 3.10). With the default
# WITH_PYTHON3="3" the module probes each listed minor and rejects the
# main-tree native python 3.13, then falls back to the host interpreter
# (3.10) whose Development artifacts cannot satisfy the cross sysroot ->
# configure fails with "Could NOT find Python3 (missing: Python3_LIBRARY
# Development)". Pinning the exact base version takes the module's EXACT
# path straight to 3.13 (validated against the native interpreter) and
# resolves library/include from the target sysroot.
EXTRA_OECMAKE += " \
        -DWITH_MANPAGE=OFF \
        -DWITH_JAEGER=OFF \
        -DWITH_SYSTEM_ZSTD=ON \
        -DWITH_FUSE=OFF \
        -DWITH_SPDK=OFF \
        -DWITH_LEVELDB=OFF \
        -DWITH_LTTNG=OFF \
        -DWITH_BABELTRACE=OFF \
        -DWITH_TESTS=OFF \
        -DWITH_RADOSGW_SELECT_PARQUET=OFF \
        -DWITH_RADOSGW_ARROW_FLIGHT=OFF \
        -DWITH_MGR=OFF \
        -DWITH_MGR_DASHBOARD_FRONTEND=OFF \
        -DWITH_SYSTEM_BOOST=ON \
        -DWITH_SYSTEM_ROCKSDB=OFF \
        -DWITH_RDMA=OFF \
        -DWITH_RADOSGW_AMQP_ENDPOINT=OFF \
        -DWITH_RADOSGW_KAFKA_ENDPOINT=OFF \
        -DWITH_REENTRANT_STRSIGNAL=ON \
        -DPYTHON_DESIRED=3 \
        -DWITH_PYTHON3=${PYTHON_BASEVERSION} \
        -DPython3_EXECUTABLE=${STAGING_BINDIR_NATIVE}/python3-native/python3 \
        -DPython3_INCLUDE_DIR=${STAGING_INCDIR}/python${PYTHON_BASEVERSION} \
        -DPython3_LIBRARY=${STAGING_LIBDIR}/libpython${PYTHON_BASEVERSION}.so \
        -DCMAKE_TOOLCHAIN_FILE:FILEPATH=${WORKDIR}/toolchain.cmake \
        -DSYSTEMD_SYSTEM_UNIT_DIR=${systemd_unitdir} \
"

LDFLAGS:append:class-target = " -Wl,--no-as-needed -latomic -Wl,--as-needed"


do_configure:prepend () {
    echo "set( CMAKE_SYSROOT \"${RECIPE_SYSROOT}\" )" >> ${WORKDIR}/toolchain.cmake
    echo "set( CMAKE_DESTDIR \"${D}\" )" >> ${WORKDIR}/toolchain.cmake
    echo "set( PYTHON_SITEPACKAGES_DIR \"${PYTHON_SITEPACKAGES_DIR}\" )" >> ${WORKDIR}/toolchain.cmake
    # echo "set( CMAKE_C_COMPILER_WORKS TRUE)" >> ${WORKDIR}/toolchain.cmake
    # echo "set( CMAKE_CXX_COMPILER_FORCED TRUE)" >> ${WORKDIR}/toolchain.cmake
    echo "set( CMAKE_C_COMPILER_FORCED TRUE )" >> ${WORKDIR}/toolchain.cmake
}

do_compile:prepend() {
    export BUILD_DOC=1
}

do_install:prepend() {
    export BUILD_DOC=1
}

do_install:append () {
    sed -i -e 's:^#!/usr/bin/python$:&3:' \
        -e 's:${WORKDIR}.*python3:${bindir}/python3:' \
        ${D}${bindir}/ceph ${D}${bindir}/ceph-crash \
        ${D}${bindir}/cephfs-top \
        ${D}${sbindir}/ceph-volume ${D}${sbindir}/ceph-volume-systemd
    find ${D} -name SOURCES.txt | xargs sed -i -e 's:${WORKDIR}::'
    install -d ${D}${sysconfdir}/ceph
    install -m 644 ${WORKDIR}/ceph.conf ${D}${sysconfdir}/ceph/
    install -d ${D}${systemd_unitdir}
    mv ${D}${libexecdir}/ceph/ceph-osd-prestart.sh ${D}${libdir}/ceph
    mv ${D}${libexecdir}/ceph/ceph_common.sh ${D}${libdir}/ceph
    # WITH_FUSE is set to OFF, remove ceph-fuse related units
    rm -rf ${D}${systemd_unitdir}/system/ceph-fuse.target ${D}${systemd_unitdir}/system/ceph-fuse@.service
}

do_install:append:class-target () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /var/lib/ceph/crash/posted 0755 root root - -" > ${D}${sysconfdir}/tmpfiles.d/ceph-placeholder.conf
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/default/volatiles
        echo "d root root 0755 /var/lib/ceph/crash/posted none" > ${D}${sysconfdir}/default/volatiles/99_ceph-placeholder
    fi
}

pkg_postinst:${PN}() {
    if [ -z "$D" ] && [ -e ${sysconfdir}/init.d/populate-volatile.sh ] ; then
        ${sysconfdir}/init.d/populate-volatile.sh update
    fi
}

FILES:${PN} += "\
        ${libdir}/rados-classes/*.so.* \
        ${libdir}/ceph/compressor/*.so \
        ${libdir}/rados-classes/*.so \
        ${libdir}/ceph/*.so \
        ${libdir}/libcephsqlite.so \
"

FILES:${PN} += " \
    /etc/tmpfiles.d/ceph-placeholder.conf \
    /etc/default/volatiles/99_ceph-placeholder \
"

FILES:${PN}-dev = " \
    ${includedir} \
    ${libdir}/libcephfs.so \
    ${libdir}/librados*.so \
    ${libdir}/librbd.so \
    ${libdir}/librgw.so \
"

FILES:${PN}-python = "\
                ${PYTHON_SITEPACKAGES_DIR}/* \
"
RDEPENDS:${PN} += "\
        python3-core \
        python3-misc \
        python3-modules \
        python3-prettytable \
        ${PN}-python \
        gawk \
        bash \
"
PACKAGES += " \
    ${PN}-python \
"
INSANE_SKIP:${PN}-python += "ldflags buildpaths"
INSANE_SKIP:${PN} += "dev-so"
INSANE_SKIP:${PN}-dbg += "buildpaths"
CCACHE_DISABLE = "1"

CVE_PRODUCT = "ceph ceph_storage ceph_storage_mon ceph_storage_osd"
# Main-tree class name (content identical to vcpu's xuantie-clang18-compatible)
inherit xuantie-clang-compatible
# Per-mcpu packaging: PACKAGE_ARCH = <cpu model>, .deb lands in
# tmp/deploy/deb/<cpu model>/ and registers into the per-CPU feed.
inherit xuantie-set-package-arch-with-mcpu

# Extend cpu configuration for different xuantie cpu (same pattern as
# iperf3/hpl/openssl-xuantie): the class above sets XUANTIE_PACKAGE_ARCH_SET=YES,
# which suppresses the global TUNE_CCARGS injection in xuantie-base.inc, so the
# recipe must add -mcpu back itself — otherwise no channel carries it.
python () {
    if d.getVar('XUANTIE_TUNE_CCARGS_USE_MCPU') != 'NO':
        d.setVar('TUNE_CCARGS', d.getVar('TUNE_CCARGS') + " -mcpu=" + d.getVar('XUANTIE_MCPU'))
    else:
        d.setVar('TUNE_CCARGS', d.getVar('TUNE_CCARGS') + " " + d.getVar('XUANTIE_TUNE_CCARGS_GENERIC'))
}
