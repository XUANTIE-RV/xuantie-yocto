DESCRIPTION = "thead VI memtool utility"
HOMEPAGE = "http://public.pengutronix.de/software/memtool"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = ""

COMPATIBLE_MACHINE = "light-*"

DEPENDS = " openssl cmake-native python3 zlib boost linux-thead"

SRC_URI = "http://public.pengutronix.de/software/memtool/memtool-2016.10.0.tar.xz"
SRC_URI[sha256sum] = "58309d356cb9b45a241cb602ca3850891a70ddaa93ced51d1cced78e14767680"

SRCREV = "${AUTOREV}"

S = "${WORKDIR}"

export SYSROOT_DIR="${RECIPE_SYSROOT}"
export PROJECT_DIR?="${COREBASE}/.."
export ARCH?="riscv"
export BOARD_NAME="${MACHINEOVERRIDES}"
export BUILD_ROOT?="${TOPDIR}"
export BUILDROOT_DIR?="${BUILD_ROOT}"
export CROSS_COMPILE="riscv64-linux-"
export TOOLCHAIN_DIR?="${EXTERNAL_TOOLCHAIN}"
export LINUX_DIR?="${STAGING_KERNEL_BUILDDIR}"
export INSTALL_DIR_ROOTFS?="${IMAGE_ROOTFS}"
export INSTALL_DIR_SDK?="${SDK_DEPLOY}"
export PATH="${SYSROOT_DIR}:${SYSROOT_DIR}/usr/include:${SYSROOT_DIR}/usr/lib:${SYSROOT_DIR}/lib:${SYSROOT_DIR}/include:${RECIPE_SYSROOT_NATIVE}/usr/bin/riscv64-oe-linux:${COREBASE}/scripts:${COREBASE}/bitbake/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin"
export KERNEL_VERSION="$(cat ${BASE_WORKDIR}/kernel_version)"

EXTRA_OEMAKE+="BUILD_SYSTEM='YOCTO_BUILD'"

PARALLEL_MAKEINST = "-j1"

do_compile() {
  cd ${S}/memtool-2016.10.0/
  autoreconf --install --force
  aclocal
  ./configure --target=riscv64-buildroot-linux-gnu --host=riscv64-buildroot-linux-gnu --build=x86_64-pc-linux-gnu --prefix=/usr --exec-prefix=/usr --sysconfdir=/etc --localstatedir=/var --program-prefix= --disable-gtk-doc --disable-gtk-doc-html --disable-doc --disable-docs --disable-documentation --with-xmlto=no --with-fop=no --disable-dependency-tracking --enable-ipv6 --disable-nls --enable-static --enable-shared
  make
}

do_install() {
  install -d ${D}${bindir} 
  cd ${S}/memtool-2016.10.0/
  cp memtool ${D}${bindir}
}

FILES_${PN} += " ${base_libdir} "
FILES_${PN} += " ${libdir} "
FILES_${PN} += " ${datadir} "
FILES_${PN} += " ${bindir} "

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

PACKAGES = "${PN}"

# RDEPENDS_${PN} = " "
