SUMMARY = "Skia is a complete 2D graphic library for drawing Text, Geometries, and Images."
DESCRIPTION = "Skia is an open source 2D graphics library which provides common APIs that work across a variety of hardware and software platforms. It serves as the graphics engine for Google Chrome and Chrome OS, Android, Flutter, and many other products."
HOMEPAGE = "https://skia.org/"
BUGTRACKER = "https://github.com/google/skia"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d25bb58a1be2e1af9b58d31565a206dc"

THEAD_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_LINUX_TAG}"

SRC_URI = "git://git@gitee.com/thead-yocto/skia.git;branch=master;protocol=http;"
S = "${WORKDIR}/git"

DEPENDS = " llvm-native fontconfig freetype zlib libjpeg-turbo icu python3-native libxkbcommon wayland wayland-protocols wayland-native virtual/mesa vulkan-loader vulkan-headers "

inherit cmake pkgconfig

OECMAKE_SOURCEPATH = "${S}/out/config"

EXTRA_OECMAKE += " \
	-DCMAKE_BUILD_TYPE=Release \
	-DGR_GL_LOG_CALLS=0 -DGR_GL_CHECK_ERROR=0 "

OECMAKE_CXX_FLAGS_append = " \
	-Wno-error=stringop-overflow \
	-Wno-error=sizeof-pointer-memaccess \
	-Wno-error=stringop-truncation \
	-Wno-error=uninitialized \
	-Wno-error=format-truncation \
	-Wno-error=int-in-bool-context \
	-Wno-error=unused-result \
	-Wno-error=class-memaccess \
	-Wno-error=redundant-move \
	-Wno-error=deprecated-copy \
	-Wno-error=attributes \
	-fvisibility-inlines-hidden \
	-I${WORKDIR}/recipe-sysroot/usr/include/freetype2/ \
	-latomic "

cmake_do_configure() {
        cd ${S}
        ./bin/gn gen ./out/config --ide=json --json-ide-script=../../gn/gn_to_cmake.py --args='target_cpu="riscv64" target_os="linux" skia_use_libwebp=false skia_use_sfntly=false skia_enable_tools=true skia_use_egl=true skia_use_freetype=true skia_use_fontconfig=true skia_use_vulkan=true skia_use_icu=false '
        cd -
        if [ "${OECMAKE_BUILDPATH}" ]; then
                bbnote "cmake.bbclass no longer uses OECMAKE_BUILDPATH.  The default behaviour is now out-of-tree builds with B=WORKDIR/build."
        fi

        if [ "${S}" != "${B}" ]; then
                rm -rf ${B}
                mkdir -p ${B}
                cd ${B}
        else
                find ${B} -name CMakeFiles -or -name Makefile -or -name cmake_install.cmake -or -name CMakeCache.txt -delete
        fi

        # Just like autotools cmake can use a site file to cache result that need generated binaries to run
        if [ -e ${WORKDIR}/site-file.cmake ] ; then
                oecmake_sitefile="-C ${WORKDIR}/site-file.cmake"
        else
                oecmake_sitefile=
        fi

        cmake \
          ${OECMAKE_GENERATOR_ARGS} \
          $oecmake_sitefile \
          ${OECMAKE_SOURCEPATH} \
          -DCMAKE_INSTALL_PREFIX:PATH=${prefix} \
          -DCMAKE_INSTALL_BINDIR:PATH=${@os.path.relpath(d.getVar('bindir'), d.getVar('prefix') + '/')} \
          -DCMAKE_INSTALL_SBINDIR:PATH=${@os.path.relpath(d.getVar('sbindir'), d.getVar('prefix') + '/')} \
          -DCMAKE_INSTALL_LIBEXECDIR:PATH=${@os.path.relpath(d.getVar('libexecdir'), d.getVar('prefix') + '/')} \
          -DCMAKE_INSTALL_SYSCONFDIR:PATH=${sysconfdir} \
          -DCMAKE_INSTALL_SHAREDSTATEDIR:PATH=${@os.path.relpath(d.getVar('sharedstatedir'), d.  getVar('prefix') + '/')} \
          -DCMAKE_INSTALL_LOCALSTATEDIR:PATH=${localstatedir} \
          -DCMAKE_INSTALL_LIBDIR:PATH=${@os.path.relpath(d.getVar('libdir'), d.getVar('prefix') + '/')} \
          -DCMAKE_INSTALL_INCLUDEDIR:PATH=${@os.path.relpath(d.getVar('includedir'), d.getVar('prefix') + '/')} \
          -DCMAKE_INSTALL_DATAROOTDIR:PATH=${@os.path.relpath(d.getVar('datadir'), d.getVar('prefix') + '/')} \
          -DPYTHON_EXECUTABLE:PATH=${PYTHON} \
          -DPython_EXECUTABLE:PATH=${PYTHON} \
          -DPython3_EXECUTABLE:PATH=${PYTHON} \
          -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
          -DCMAKE_INSTALL_SO_NO_EXE=0 \
          -DCMAKE_TOOLCHAIN_FILE=${WORKDIR}/toolchain.cmake \
          -DCMAKE_NO_SYSTEM_FROM_IMPORTED=1 \
          ${EXTRA_OECMAKE} \
          -Wno-dev
}

do_install() {
        mkdir -p ${D}${bindir}/
        mkdir -p ${D}${libdir}/
        cp ${WORKDIR}/build/SkiaSDLExample ${D}${bindir}/
        cp ${WORKDIR}/build/libskia.a ${D}${libdir}/
}

FILES_${PN} = "\
        ${bindir}/* \
        ${libdir}/libskia.a \
"

INSANE_SKIP_${PN} += "dev-so"
