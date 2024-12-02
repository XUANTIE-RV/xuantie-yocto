SUMMARY = "linux libc headers for musl_git.bb"
LICENSE = "CLOSED"

PV = "1.0.0"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

INHIBIT_DEFAULT_DEPS = "1"

PROVIDES += "linux-libc-headers \
             linux-libc-headers-dev"
PACKAGES += "linux-libc-headers \
             linux-libc-headers-dev"

S = "${WORKDIR}"

do_install() {
	install -d ${D}${includedir}/asm/
	install -d ${D}${includedir}/asm-generic/
	install -d ${D}${includedir}/drm/
	install -d ${D}${includedir}/linux/
	install -d ${D}${includedir}/misc/
	install -d ${D}${includedir}/mtd/
	install -d ${D}${includedir}/rdma/
	install -d ${D}${includedir}/sound/
	install -d ${D}${includedir}/video/
	install -d ${D}${includedir}/xen/
	install -d ${D}${includedir}/linux/
	install -d ${D}${includedir}/bits/
	install -d ${D}${syslibdir}
	install -m 0644 ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/include/asm/*.h         ${D}${includedir}/asm/
	install -m 0644 ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/include/asm-generic/*.h ${D}${includedir}/asm-generic/
	install -m 0644 ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/include/drm/*.h         ${D}${includedir}/drm/
	install -m 0644 ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/include/linux/*.h       ${D}${includedir}/linux/
	install -m 0644 ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/include/misc/*.h        ${D}${includedir}/misc/
	install -m 0644 ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/include/mtd/*.h         ${D}${includedir}/mtd/
	install -m 0644 ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/include/rdma/*.h        ${D}${includedir}/rdma/
	install -m 0644 ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/include/sound/*.h       ${D}${includedir}/sound/
	install -m 0644 ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/include/video/*.h       ${D}${includedir}/video/
	install -m 0644 ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/include/xen/*.h         ${D}${includedir}/xen/
	install -m 0644 ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/include/bits/wordsize.h ${D}${includedir}/bits/
	cp -rfa ${EXTERNAL_TOOLCHAIN_SYSROOT}/usr/include/linux/*                 ${D}${includedir}/linux/
}

FILES:${PN}-dev = ""
FILES:linux-libc-headers-dev += "${includedir}"
FILES:linux-libc-headers += "${syslibdir}"

COMPATIBLE_HOST = ".*-musl.*"
