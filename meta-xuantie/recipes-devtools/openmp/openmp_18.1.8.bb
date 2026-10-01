DESCRIPTION = "LLVM based C/C++ compiler Runtime"
HOMEPAGE = "http://openmp.llvm.org/"
SECTION = "libs"
PV = "18.1.8"
TOOLCHAIN = "clang"
LICENSE = "CLOSED"

python () {
    import os
    tc = d.getVar('EXTERNAL_TOOLCHAIN_CLANG') or ''
    if not os.path.isfile(os.path.join(tc, 'lib', 'libomp.so')):
        raise bb.parse.SkipRecipe("OpenMP runtime not found in clang toolchain")
}

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -d ${D}${includedir}/cmake/openmp/
    cp -rfd ${EXTERNAL_TOOLCHAIN_CLANG}/include/omp.h                              ${D}${includedir}
    cp -rfd ${EXTERNAL_TOOLCHAIN_CLANG}/include/ompt.h                             ${D}${includedir}
    cp -rfd ${EXTERNAL_TOOLCHAIN_CLANG}/include/ompt-multiplex.h                   ${D}${includedir}
    cp -rfd ${EXTERNAL_TOOLCHAIN_CLANG}/include/omp-tools.h                        ${D}${includedir}
    cp -rfd ${EXTERNAL_TOOLCHAIN_CLANG}/include/ompx.h                             ${D}${includedir}
    cp -rfd ${EXTERNAL_TOOLCHAIN_CLANG}/lib/libarcher.so                           ${D}${libdir}
    cp -rfd ${EXTERNAL_TOOLCHAIN_CLANG}/lib/libarcher_static.a                     ${D}${libdir}
    cp -rfd ${EXTERNAL_TOOLCHAIN_CLANG}/lib/libomp.so                              ${D}${libdir}
    cp -rfd ${EXTERNAL_TOOLCHAIN_CLANG}/lib/libompd.so                             ${D}${libdir}
    cp -rfd ${EXTERNAL_TOOLCHAIN_CLANG}/lib/cmake/openmp/FindOpenMPTarget.cmake    ${D}${includedir}/cmake/openmp/
}

PACKAGES += "${PN}-libomptarget"
FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/lib*${SOLIBSDEV}"
FILES:${PN}-libomptarget = "${libdir}/libomptarget-*.bc"
INSANE_SKIP:${PN} = "dev-so"

COMPATIBLE_HOST:mips64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:powerpc = "null"

BBCLASSEXTEND = "native nativesdk"

