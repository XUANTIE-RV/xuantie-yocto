DESCRIPTION = "The LLVM Compiler Infrastructure"
HOMEPAGE = "http://llvm.org"
LICENSE = "Apache-2.0-with-LLVM-exception"
SECTION = "devel"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=8a15a0759ef07f2682d2ba4b893c9afe"
  
DEPENDS = "libffi libxml2 zlib libedit libatomic-ops ninja-native"
DEPENDS_append_class-target = " llvm-native"

RDEPENDS_${PN}_append_class-target = " ncurses-terminfo"

MAJOR_VERSION = "10"
MINOR_VERSION = "0"
PATCH_VERSION = "2"
PV = "${MAJOR_VERSION}.${MINOR_VERSION}.${PATCH_VERSION}"
LLVM_RELEASE = "${PV}"

UPSTREAM_CHECK_GITTAGREGEX = "llvmorg-(?P<pver>\d+(\.\d+)+)"

BRANCH = "release/${MAJOR_VERSION}.x"
SRCREV = "ef32c611aa214dea855364efd7ba451ec5ec3f74"
SRC_URI = "git://github.com/llvm/llvm-project.git;branch=${BRANCH} \
           file://0006-llvm-TargetLibraryInfo-Undefine-libc-functions-if-th.patch;striplevel=2 \
           file://0007-llvm-allow-env-override-of-exe-path.patch;striplevel=2 \
           file://0001-fix-the-atomic-build-failure.patch;patchdir=.. \
           file://0001-Force-to-link-the-dl-to-wa-the-find-dl-lib-issue.patch;patchdir=.. \
          "

S = "${WORKDIR}/git/llvm"

inherit cmake pkgconfig

PROVIDES_append_class-target = "llvm"
PROVIDES_append_class-native = "llvm-native"

SYSROOT_DIRS = " \
    ${includedir} \
    ${libdir} \
    ${bindir} \
    ${sbindir} \
    ${base_bindir} \
    ${base_sbindir} \
    ${libexecdir} \
    ${sysconfdir} \
    ${localstatedir} \
    ${datadir} \
"

def get_llvm_experimental_arch(bb, d, arch_var):
    import re
    a = d.getVar(arch_var, True)
    return ""

def get_llvm_arch(bb, d, arch_var):
    import re
    a = d.getVar(arch_var, True)
    if   re.match('(i.86|athlon|x86.64)$', a):         return 'X86'
    elif re.match('arm$', a):                          return 'ARM'
    elif re.match('armeb$', a):                        return 'ARM'
    elif re.match('aarch64$', a):                      return 'AArch64'
    elif re.match('aarch64_be$', a):                   return 'AArch64'
    elif re.match('mips(isa|)(32|64|)(r6|)(el|)$', a): return 'Mips'
    elif re.match('riscv32$', a):                      return 'riscv32'
    elif re.match('riscv64$', a):                      return 'riscv64'
    elif re.match('p(pc|owerpc)(|64)', a):             return 'PowerPC'
    else:
        bb.note("'%s' is not a primary llvm architecture" % a)
    return ""

def get_llvm_host_arch(bb, d):
    return get_llvm_arch(bb, d, 'HOST_ARCH')

def get_llvm_target_arch(bb, d):
    return get_llvm_arch(bb, d, 'TARGET_ARCH')

def get_llvm_experimental_target_arch(bb, d):
    return get_llvm_experimental_arch(bb, d, 'TARGET_ARCH')

# Default to build all OE-Core supported target arches (user overridable).
LLVM_TARGETS ?= "RISCV"

EXTRA_OECMAKE += "-DLLVM_ENABLE_ASSERTIONS=OFF \
                  -DLLVM_ENABLE_EXPENSIVE_CHECKS=OFF \
                  -DLLVM_ENABLE_PIC=ON \
                  -DLLVM_BINDINGS_LIST='' \
                  -DLLVM_LINK_LLVM_DYLIB=ON \
                  -DLLVM_ENABLE_FFI=ON \
                  -DLLVM_ENABLE_RTTI=ON \
                  -DFFI_INCLUDE_DIR=$(pkg-config --variable=includedir libffi) \
                  -DLLVM_OPTIMIZED_TABLEGEN=ON \
                  -DLLVM_TARGETS_TO_BUILD='${LLVM_TARGETS}' \
                  -DLLVM_TEMPORARILY_ALLOW_OLD_TOOLCHAIN=ON \
                  -DLLVM_BUILD_EXTERNAL_COMPILER_RT=ON \
                  -DPYTHON_EXECUTABLE=${HOSTTOOLS_DIR}/python3 \
                  -DLLVM_ENABLE_PROJECTS='clang;libclc;' \
                  -DCMAKE_SYSTEM_NAME=Linux \
                  -DCMAKE_BUILD_TYPE=Release \
                  -DCMAKE_CXX_FLAGS_RELEASE='${CXXFLAGS} -DNDEBUG -g0' \
                  -DCMAKE_C_FLAGS_RELEASE='${CFLAGS} -DNDEBUG -g0' \
                  -G Ninja"

EXTRA_OECMAKE_append_class-target = "\
                  -DCMAKE_CROSSCOMPILING:BOOL=ON \
                  -DLLVM_BUILD_TOOLS:BOOL=ON \
                  -DLLVM_INSTALL_UTILS:BOOL=ON \
                  -DLLVM_INSTALL_MODULEMAPS:BOOL=ON \
                  -DLLVM_INSTALL_BINUTILS_SYMLINKS:BOOL=ON \
                  -DLLVM_INSTALL_CCTOOLS_SYMLINKS:BOOL=ON \
                  -DLLVM_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-tblgen \
                  -DCLANG_TABLEGEN=${STAGING_BINDIR_NATIVE}/clang-tblgen \
                  -DLLVM_TARGET_ARCH=${@get_llvm_target_arch(bb, d)} \
                  -DLLVM_CONFIG_PATH=${STAGING_BINDIR_NATIVE}/llvm-config \
                 "

EXTRA_OECMAKE_append_class-native = "\
                  -DLLVM_BUILD_TOOLS:BOOL=ON \
                  -DLLVM_INSTALL_UTILS:BOOL=ON \
                  -DLLVM_INSTALL_MODULEMAPS:BOOL=ON \
                  -DLLVM_INSTALL_BINUTILS_SYMLINKS:BOOL=ON \
                  -DLLVM_INSTALL_CCTOOLS_SYMLINKS:BOOL=ON \
                 "

do_compile_class-target() {
	ninja -v ${PARALLEL_MAKE} llvm-config llvm-tblgen llvm-link llvm-as clang-tblgen
}

do_install_class-target() {
	DESTDIR=${D} ninja -v install

        # sed -i 's;${_IMPORT_PREFIX}/lib;${_IMPORT_PREFIX_LIBRARY};g' ${D}${libdir}/cmake/llvm/LLVMExports-release.cmake
        sed -i 's;${_IMPORT_PREFIX}/bin;${_IMPORT_PREFIX_BIN};g' ${D}${libdir}/cmake/llvm/LLVMExports-release.cmake
        # sed -i "4i execute_process(COMMAND \"llvm-config\" \"--bindir\" OUTPUT_VARIABLE _IMPORT_PREFIX_BIN OUTPUT_STRIP_TRAILING_WHITESPACE)\n" ${D}${libdir}/cmake/llvm/LLVMExports-release.cmake
        sed -i "4i set(_IMPORT_PREFIX_BIN \"\${STAGING_DIR_NATIVE}/usr/bin\")\n" ${D}${libdir}/cmake/llvm/LLVMExports-release.cmake
}

do_configure_prepend() {
        sed -i "s|sys::path::parent_path(CurrentPath))\.str()|sys::path::parent_path(sys::path::parent_path(CurrentPath))).str()|g" ${S}/tools/llvm-config/llvm-config.cpp
}

do_compile_class-native() {
        ninja -v ${PARALLEL_MAKE} llvm-config llvm-tblgen llvm-link llvm-as clang-tblgen
}

do_install_class-native() {
        DESTDIR=${D} ninja -v install
        mkdir -p ${D}${bindir}/
        cp ${WORKDIR}/build/bin/clang-tblgen ${D}${bindir}/ 
}

PACKAGES =+ "${PN}-libllvm"

FILES_${PN}-libllvm = "\
    	${bindir}/clang* \
    	${bindir}/llvm* \
    	${libdir}/libLLVM*.so \
        ${libdir}/clang/* \
    	${libdir}/libLTO.so.* \
    	${libdir}/libRemarks.so.* \
        ${libdir}/libclang.so.* \
        ${datadir}/clang/* \
        ${datadir}/scan* \
    	${datadir}/opt-viewer \
"

FILES_${PN}-staticdev += "\
        ${libdir}/*.a \
 "
INSANE_SKIP_${PN}-libllvm += "dev-so"

BBCLASSEXTEND = "native"
