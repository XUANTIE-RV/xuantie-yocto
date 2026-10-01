BPN = "qemu"

DEPENDS += "glib-2.0-native zlib-native"

require qemu-native.inc

EXTRA_OECONF:append = " --target-list=${@get_qemu_usermode_target_list(d)} --disable-tools --disable-install-blobs --disable-guest-agent"

PACKAGECONFIG ??= "pie"

SRC_URI = " \
    gitsm://github.com/XUANTIE-RV/qemu.git;branch=xuantie-qemu-9.0;protocol=https;name=qemu \
    git://gitlab.com/qemu-project/berkeley-softfloat-3.git;protocol=https;nobranch=1;rev=b64af41c3276f97f0e181920400ee056b9c88037;destsuffix=${S}/subprojects/berkeley-softfloat-3 \
    git://gitlab.com/qemu-project/berkeley-testfloat-3.git;protocol=https;nobranch=1;rev=e7af9751d9f9fd3b47911f51a5cfd08af256a9ab;destsuffix=${S}/subprojects/berkeley-testfloat-3 \
    git://gitlab.com/qemu-project/dtc.git;protocol=https;nobranch=1;rev=b6910bec11614980a21e46fbccc35934b671bd81;destsuffix=${S}/subprojects/dtc \
    git://gitlab.com/qemu-project/keycodemapdb.git;protocol=https;nobranch=1;rev=f5772a62ec52591ff6870b7e8ef32482371f22c6;destsuffix=${S}/subprojects/keycodemapdb \
    git://gitlab.com/libblkio/libblkio.git;protocol=https;nobranch=1;rev=f84cc963a444e4cb34813b2dcfc5bf8526947dc0;destsuffix=${S}/subprojects/libblkio \
    git://gitlab.com/qemu-project/libvfio-user.git;protocol=https;nobranch=1;rev=0b28d205572c80b568a1003db2c8f37ca333e4d7;destsuffix=${S}/subprojects/libvfio-user \
    git://gitlab.freedesktop.org/slirp/libslirp.git;protocol=https;nobranch=1;rev=26be815b86e8d49add8c9a8b320239b9594ff03d;destsuffix=${S}/subprojects/slirp \
    file://powerpc_rom.bin \
    file://run-ptest \
    file://0001-qemu-Add-addition-environment-space-to-boot-loader-q.patch \
    file://0003-apic-fixup-fallthrough-to-PIC.patch \
    file://0004-configure-Add-pkg-config-handling-for-libgcrypt.patch \
    file://0005-qemu-Do-not-include-file-if-not-exists.patch \
    file://0006-qemu-Add-some-user-space-mmap-tweaks-to-address-musl.patch \
    file://0007-qemu-Determinism-fixes.patch \
    file://0008-tests-meson.build-use-relative-path-to-refer-to-file.patch \
    file://0009-Define-MAP_SYNC-and-MAP_SHARED_VALIDATE-on-needed-li.patch \
    file://0010-hw-pvrdma-Protect-against-buggy-or-malicious-guest-d.patch \
    file://0011-target-riscv-enable-zalasr-on-c920v3-cpu-variants.patch \
    file://0002-linux-user-Replace-use-of-lfs64-related-functions-an.patch \
    file://fixedmeson.patch \
    file://no-pip.patch \
    file://qemu-guest-agent.init \
    file://qemu-guest-agent.udev \
"

SRCREV_qemu = "22b2d3b9dacbcac0d1a388ec40f584c464e0fbfd"
S = "${WORKDIR}/git"
SRCREV_FORMAT = "qemu"

do_configure:prepend() {
    cp -r ${S}/subprojects/packagefiles/* ${S}/subprojects/
}
