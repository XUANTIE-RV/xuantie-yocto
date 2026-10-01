DESCRIPTION = "ZeroMQ looks like an embeddable networking library but acts like a concurrency framework"
HOMEPAGE = "http://www.zeromq.org"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

PACKAGECONFIG ??= "libsodium pgm"
# 4.3.5 gates libsodium behind ENABLE_CURVE (default OFF); both are needed
# for CURVE security and the libsodium.so.23 runtime dependency
PACKAGECONFIG[libsodium] = "-DENABLE_CURVE=ON -DWITH_LIBSODIUM=ON,-DWITH_LIBSODIUM=OFF, libsodium"
# openpgm installs openpgm-5.3.pc; cmake defaults to searching openpgm-5.2
PACKAGECONFIG[pgm] = "-DWITH_OPENPGM=ON -DOPENPGM_PKGCONFIG_NAME=openpgm-5.3,-DWITH_OPENPGM=OFF, openpgm"

SRC_URI = "http://github.com/zeromq/libzmq/releases/download/v${PV}/zeromq-${PV}.tar.gz \
    file://0001-CMakeLists-txt-Avoid-host-specific-path-to-libsodium.patch \
    file://run-ptest \
"
SRC_URI[md5sum] = "ae933b1e98411fd7cb8309f9502d2737"
SRC_URI[sha256sum] = "6653ef5910f17954861fe72332e68b03ca6e4d9c7160eb3a8de5a5a913bfab43"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/libzmq/releases"

inherit cmake ptest pkgconfig

EXTRA_OECMAKE = "${@bb.utils.contains('PTEST_ENABLED', '1', '-DBUILD_TESTS=ON', '-DBUILD_TESTS=OFF', d)} \
                 -DCMAKE_SKIP_RPATH=ON \
"

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    install -m 0755 ${B}/bin/test_* ${D}${PTEST_PATH}/tests
}

FILES:${PN}-doc += "${datadir}/zmq/*.txt"
