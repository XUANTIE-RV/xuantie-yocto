SUMMARY = "The GNU Compiler Collection - libgfortran"
HOMEPAGE = "http://www.gnu.org/software/gcc/"
SECTION = "devel"
PV = "${GCC_VERSION}"

inherit external-toolchain

LICENSE = "GPL-3.0-with-GCC-exception"

external_libroot = "${@os.path.realpath('${EXTERNAL_TOOLCHAIN_LIBROOT}').replace(os.path.realpath('${EXTERNAL_TOOLCHAIN}') + '/', '/')}"
FILES_MIRRORS =. "${libdir}/gcc/${TARGET_SYS}/${GCC_VERSION}/|${external_libroot}/\n"

EXTERNAL_PROVIDE_PATTERN = "${FILES_${PN}}"

# We don't copy the static binaries and headers, since they don't belong to the
# target sysroot, but need to be in the native one (that's the place where compiler
# and linker are looking for them).
FILES_${PN} = "${libdir}/libgfortran.so.*"
FILES_${PN}-dev = "\
    ${libdir}/libgfortran*.so \
    ${libdir}/libgfortran.spec \
    ${libdir}/libgfortran.la \
"
FILES_${PN}-staticdev = "${libdir}/libgfortran.a"

do_package_write_ipk[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_deb[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_rpm[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
