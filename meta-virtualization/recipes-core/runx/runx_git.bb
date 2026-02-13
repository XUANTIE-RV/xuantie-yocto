HOMEPAGE = "https://github.com/lf-edge/runx"
SUMMARY = "runx stuff"
DESCRIPTION = "Xen Runtime for OCI"

SRCREV_runx = "edc9350a79ede0365066c9743080e3dc6430d602"

KERNEL_SRC_VER="linux-5.4"
KERNEL_URL_VER="v5.x"

SRC_URI = "\
	  git://github.com/lf-edge/runx;nobranch=1;name=runx;protocol=https \
          https://www.kernel.org/pub/linux/kernel/${KERNEL_URL_VER}/${KERNEL_SRC_VER}.tar.xz;destsuffix=git/kernel/build \
          file://0001-make-kernel-cross-compilation-tweaks.patch \
          file://0001-make-kernel-bump-to-v5.4.104-for-gcc10-fixes.patch \
          file://0001-make-initrd-allow-externally-provided-busybox.patch \
	  "

SRC_URI[md5sum] = "ce9b2d974d27408a61c53a30d3f98fb9"
SRC_URI[sha256sum] = "bf338980b1670bca287f9994b7441c2361907635879169c64ae78364efc5f491"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=945fc9aa694796a6337395cc291ddd8c"

S = "${WORKDIR}/git"
PV = "v1.0-git${SRCREV_runx}"

inherit features_check
REQUIRED_DISTRO_FEATURES = "vmsep"

inherit pkgconfig
# for the kernel build
inherit kernel-arch

# we have a busybox bbappend that makes /bin available to the
# sysroot, and hence gets us the target binary that we need
DEPENDS = "busybox-initrd go-build"
DEPENDS += "resolvconf"

# for the kernel build phase
DEPENDS += "openssl-native coreutils-native util-linux-native xz-native bc-native"
DEPENDS += "elfutils-native"
DEPENDS += "qemu-native bison-native"

RDEPENDS:${PN} += " jq bash"
RDEPENDS:${PN} += " xen-tools-xl go-build socat daemonize"
RDEPENDS:${PN} += " qemu-system-i386 ca-certificates qemu qemu-keymaps"

RUNX_USE_INTERNAL_BUSYBOX ?= ""

do_compile() {
    # we'll need this for the initrd later, so lets error if it isn't what
    # we expect (statically linked)
    file ${STAGING_DIR_HOST}/bin/busybox.nosuid
    
    # prep steps to short circuit some of make-kernel's fetching and
    # building.
    mkdir -p ${S}/kernel/build
    mkdir -p ${S}/kernel/src
    cp ${DL_DIR}/${KERNEL_SRC_VER}.tar.xz ${S}/kernel/build/

    # In the future, we might want to link the extracted kernel source (if
    # we move patches to recipe space, but for now, we need make-kernel to
    # extract a copy and possibly patch it.
    # ln -sf ${WORKDIR}/${KERNEL_SRC_VER} ${S}/kernel/src/

    # build the kernel
    echo "[INFO]: runx: building the kernel"

    export KERNEL_CC="${KERNEL_CC}"
    export KERNEL_LD="${KERNEL_LD}"
    export ARCH="${ARCH}"
    export HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"
    export HOSTCPP="${BUILD_CPP}"
    export CROSS_COMPILE="${CROSS_COMPILE}"
    export build_vars="HOSTCC='$HOSTCC' STRIP='$STRIP' OBJCOPY='$OBJCOPY' ARCH=$ARCH CC='$KERNEL_CC' LD='$KERNEL_LD'"
        
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE

    # We want make-kernel, to have the following build lines:
    #    make O=$kernel_builddir HOSTCC="${HOSTCC}" ARCH=$ARCH oldconfig
    #    make -j4 O=$kernel_builddir HOSTCC="${HOSTCC}" STRIP="$STRIP" OBJCOPY="$OBJCOPY" ARCH=$ARCH CC="$KERNEL_CC" LD="$KERNEL_LD" $image
    ${S}/kernel/make-kernel

    # construct the initrd
    bbnote "runx: constructing the initrd"
    if [ -z "${RUNX_USE_INTERNAL_BUSYBOX}" ]; then
        bbnote "runx: using external busybox"
        cp ${STAGING_DIR_HOST}/bin/busybox.nosuid ${WORKDIR}/busybox
        export QEMU_USER="`which qemu-${HOST_ARCH}` -L ${STAGING_BASELIBDIR}/.."
        export BUSYBOX="${WORKDIR}/busybox"
        export busybox="${WORKDIR}/busybox"
        export CROSS_COMPILE="${TARGET_PREFIX}"
    else
        bbnote "runx: using internal busybox"
        export CC="${CC}"
        export LD="${LD}"
        export CFLAGS="${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS} ${CFLAGS}"
        export LDFLAGS="${TOOLCHAIN_OPTIONS} ${HOST_LD_ARCH} ${LDFLAGS}"
        export HOSTCFLAGS="${BUILD_CFLAGS} ${BUILD_LDFLAGS}"
        export CROSS_COMPILE="${TARGET_PREFIX}"
    fi
    ${S}/initrd/make-initrd
}

do_install() {
    install -d ${D}${bindir}
    install -m 755 ${S}/runX ${D}${bindir}
    
    install -d ${D}${datadir}/runX
    install -m 755 ${S}/kernel/out/kernel ${D}/${datadir}/runX
    install -m 755 ${S}/initrd/out/initrd ${D}/${datadir}/runX
    install -m 755 ${S}/files/start ${D}/${datadir}/runX
    install -m 755 ${S}/files/create ${D}/${datadir}/runX
    install -m 755 ${S}/files/state ${D}/${datadir}/runX
    install -m 755 ${S}/files/delete ${D}/${datadir}/runX
    install -m 755 ${S}/files/serial_start ${D}/${datadir}/runX


}

deltask compile_ptest_base

FILES:${PN} += "${bindir}/* ${datadir}/runX/*"

INHIBIT_PACKAGE_STRIP = "1"
INSANE_SKIP:${PN} += "ldflags already-stripped"
