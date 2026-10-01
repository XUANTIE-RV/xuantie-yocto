# Recipe to extract musl C library from an external toolchain
#
# musl-external extracts the musl libc shared library, dynamic linker,
# headers, CRT files, and stub static libraries from the external toolchain
# sysroot. It provides virtual/libc and related virtual packages for the
# musl C library variant.

SUMMARY = "musl C library (from external toolchain)"
DESCRIPTION = "The musl C library extracted from a pre-built external toolchain."
HOMEPAGE = "http://www.musl-libc.org/"
SECTION = "libs"

inherit external-toolchain linuxloader

COMPATIBLE_HOST = ".*-musl.*"

require recipes-external/musl/musl-external-version.inc

# musl does not provide bits/wordsize.h (a glibc concept), but some recipes
# (e.g., gmp wrapper headers) require it. Ship a RISC-V compatible version.
SRC_URI = "file://wordsize.h file://sys-cdefs.h"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Handle multilib sysroot layout for musl toolchains.
# musl toolchains may use multilib directories (e.g., usr/lib32xthead/ilp32d/)
# instead of the standard usr/lib/. Derive the multilib suffix from
# EXTERNAL_TOOLCHAIN_LIBROOT and add a FILES_PREMIRRORS mapping so the
# external-toolchain framework can locate libc.so, crt*.o, etc.
# For XuanTie toolchains, use EXTERNAL_TOOLCHAIN_LIB_DIR_NAME and
# EXTERNAL_TOOLCHAIN_ABI_DIR_NAME if available, as they provide the correct
# multilib path derived from GCC -print-multi-directory.
def get_musl_multilib_os_dir(d):
    import re
    # First, try XuanTie-specific variables which are already computed
    # correctly from GCC multilib query
    lib_dir_name = d.getVar('EXTERNAL_TOOLCHAIN_LIB_DIR_NAME')
    abi_dir_name = d.getVar('EXTERNAL_TOOLCHAIN_ABI_DIR_NAME')
    if lib_dir_name and abi_dir_name and lib_dir_name != 'UNKNOWN' and abi_dir_name != 'UNKNOWN':
        return '%s/%s' % (lib_dir_name, abi_dir_name)
    # Fallback: parse EXTERNAL_TOOLCHAIN_LIBROOT for version-based layout
    libroot = d.getVar('EXTERNAL_TOOLCHAIN_LIBROOT')
    if libroot and libroot != 'UNKNOWN':
        parts = libroot.rstrip('/').split('/')
        for i, part in enumerate(parts):
            if re.match(r'^\d+\.\d+\.\d+$', part):
                multidir = '/'.join(parts[i+1:])
                if multidir:
                    return multidir
    return ''

MUSL_MULTILIB_OS_DIR = "${@get_musl_multilib_os_dir(d)}"
FILES_PREMIRRORS:prepend = "${@'${libdir}/|/usr/${MUSL_MULTILIB_OS_DIR}/\n' if d.getVar('MUSL_MULTILIB_OS_DIR') else ''}"

DEPENDS = "virtual/${TARGET_PREFIX}binutils"
PROVIDES += "\
    musl \
    virtual/libc \
    virtual/libintl \
    virtual/libiconv \
    virtual/crypt \
    linux-libc-headers \
    linux-libc-headers-dev \
"

# License detection: musl uses MIT license
def get_external_musl_license(d):
    if (d.getVar('TCMODE', True).startswith('external') and
            d.getVar('EXTERNAL_TOOLCHAIN', True)):
        copysearch = os.path.join(d.getVar('includedir', True), 'features.h')
        found = oe.external.find_sysroot_files([copysearch], d)
        if found and found[0]:
            return 'MIT'
    return 'UNKNOWN'

LICENSE:tcmode-external := "${@get_external_musl_license(d)}"

MUSL_LDSO_ARCH = "${@get_musl_loader_arch(d)}"

PACKAGES = "${PN}-dbg ${PN} ${PN}-utils ${PN}-dev ${PN}-staticdev ${PN}-doc"

# musl libc.so is both the C library and the dynamic linker
FILES:${PN} = "\
    ${base_libdir}/ld-musl-*.so.1 \
    ${libdir}/libc.so \
    ${sysconfdir}/ld-musl-*.path \
"

FILES:${PN}-utils = "\
    ${bindir}/ldd \
    ${bindir}/iconv \
"

FILES:${PN}-dev = "\
    ${libdir}/*crt*.o \
    ${libdir}/libm.a \
    ${libdir}/libpthread.a \
    ${libdir}/librt.a \
    ${libdir}/libdl.a \
    ${libdir}/libresolv.a \
    ${libdir}/libutil.a \
    ${libdir}/libxnet.a \
    ${libdir}/libcrypt.a \
"

# Include all headers in -dev package
linux_include_subdirs = "asm asm-generic bits drm linux mtd rdma sound video"
FILES:${PN}-dev += "\
    ${includedir}/*.h \
    ${includedir}/sys/*.h \
    ${includedir}/net/*.h \
    ${includedir}/netinet/*.h \
    ${includedir}/netpacket/*.h \
    ${includedir}/arpa/*.h \
    ${includedir}/scsi/*.h \
    ${@' '.join('${includedir}/%s' % d for d in '${linux_include_subdirs}'.split())} \
"

FILES:${PN}-staticdev = "${libdir}/libc.a"

FILES:${PN}-doc = ""

# Provide the standard musl package names for dependency resolution
RPROVIDES:${PN} += "musl ldd rtld(GNU_HASH)"
RPROVIDES:${PN}-dev += "musl-dev virtual-libc-dev linux-libc-headers-dev bsd-headers-dev libssp-nonshared-staticdev"
RPROVIDES:${PN}-staticdev += "musl-staticdev"
RPROVIDES:${PN}-dbg += "musl-dbg"
RPROVIDES:${PN}-utils += "musl-utils musl-utils-iconv"

LEAD_SONAME = "libc.so"
INSANE_SKIP:${PN} += "dev-so libdir"
INSANE_SKIP:${PN}-dev += "staticdev"

# We don't need linux-libc-headers as a separate dependency
LINUX_LIBC_RDEP_REMOVE ?= "linux-libc-headers-dev"
RDEPENDS:${PN}-dev:remove = "${LINUX_LIBC_RDEP_REMOVE}"

python do_install () {
    bb.build.exec_func('external_toolchain_do_install', d)
    bb.build.exec_func('musl_external_do_install_extra', d)
}

musl_external_do_install_extra () {
    if [ ! -e ${D}${libdir}/libc.so ]; then
        bbfatal "Unable to locate installed libc.so file (${libdir}/libc.so)." \
                "This may mean that your external toolchain uses a different" \
                "multi-lib setup than your machine configuration"
    fi

    # Fix ld-musl dynamic linker symlink after multilib relocation.
    # The toolchain may use multilib paths (e.g., ../usr/lib64/lp64d/libc.so)
    # but the external-toolchain framework remaps them to ${libdir}.
    # Some prebuilt sysroots are packaged for link-only use and omit the
    # loader symlink; in musl it is the same file as libc.so, so create
    # it ourselves so qemu-user can resolve PT_INTERP for meson sanity.
    if [ -L ${D}${base_libdir}/ld-musl-${MUSL_LDSO_ARCH}.so.1 ]; then
        ln -rsf ${D}${libdir}/libc.so ${D}${base_libdir}/ld-musl-${MUSL_LDSO_ARCH}.so.1
    elif [ ! -e ${D}${base_libdir}/ld-musl-${MUSL_LDSO_ARCH}.so.1 ]; then
        install -d ${D}${base_libdir}
        ln -rs ${D}${libdir}/libc.so ${D}${base_libdir}/ld-musl-${MUSL_LDSO_ARCH}.so.1
    fi

    # Generate musl ld.so path configuration
    install -d ${D}${sysconfdir}
    echo "${base_libdir}" > ${D}${sysconfdir}/ld-musl-${MUSL_LDSO_ARCH}.path
    echo "${libdir}" >> ${D}${sysconfdir}/ld-musl-${MUSL_LDSO_ARCH}.path

    # Create ldd symlink if not present (musl's ldd is just libc.so invoked as ldd)
    if [ ! -e ${D}${bindir}/ldd ]; then
        install -d ${D}${bindir}
        ln -rs ${D}${libdir}/libc.so ${D}${bindir}/ldd
    fi

    # Install bits/wordsize.h if not already present from the toolchain.
    # musl does not provide this header, but some recipes need it
    # (e.g., gmp multilib header wrapper).
    if [ ! -e ${D}${includedir}/bits/wordsize.h ]; then
        install -d ${D}${includedir}/bits
        install -m 0644 ${WORKDIR}/wordsize.h ${D}${includedir}/bits/wordsize.h
    fi

    # Install sys/cdefs.h stub if not already present.
    # glibc provides this header; musl does not. Many packages include it.
    if [ ! -e ${D}${includedir}/sys/cdefs.h ]; then
        install -d ${D}${includedir}/sys
        install -m 0644 ${WORKDIR}/sys-cdefs.h ${D}${includedir}/sys/cdefs.h
    fi
}

# External toolchain musl libc.so may not have a SONAME set, which prevents
# OE-Core's automatic shlibs registration. Manually populate the shlibs2
# database so consumers can resolve the libc.so runtime dependency.
python musl_populate_shlibs() {
    import os
    shlibsworkdir = d.getVar('SHLIBSWORKDIR')
    os.makedirs(shlibsworkdir, exist_ok=True)
    pkg = d.getVar('PN')
    pkgver = d.getVar('PKGV')
    libdir = d.getVar('libdir')
    shlibs_file = os.path.join(shlibsworkdir, pkg + '.list')
    if not os.path.exists(shlibs_file):
        with open(shlibs_file, 'w') as f:
            f.write('libc.so:%s:%s\n' % (libdir, pkgver))
}
do_package[postfuncs] += "musl_populate_shlibs"

# musl's utils need libgcc
do_package[depends] += "${MLPREFIX}libgcc:do_packagedata"
do_package_write_ipk[depends] += "${MLPREFIX}libgcc:do_packagedata"
do_package_write_deb[depends] += "${MLPREFIX}libgcc:do_packagedata"
do_package_write_rpm[depends] += "${MLPREFIX}libgcc:do_packagedata"

# musl may need libssp for -fstack-protector builds
do_packagedata[depends] += "gcc-runtime:do_packagedata"
