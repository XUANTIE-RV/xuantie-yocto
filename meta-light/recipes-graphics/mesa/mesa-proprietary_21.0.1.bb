require mesa.inc

DESCRIPTION = "mesa libraries"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-*"


do_compile[noexec] = "1"
do_configure[noexec] = "1"
do_install() {
    install -d ${D}${includedir}
    install -d ${D}${includedir}/EGL
    install -d ${D}${includedir}/GL
    install -d ${D}${includedir}/GL/internal
    install -d ${D}${includedir}/GLES
    install -d ${D}${includedir}/GLES2
    install -d ${D}${includedir}/GLES3
    install -d ${D}${includedir}/KHR
    install -d ${D}${libdir}
    install -d ${D}${libdir}/dri
    install -d ${D}${libdir}/pkgconfig
    install -d ${D}${datadir}
    install -d ${D}${datadir}/drirc.d
    install -d ${D}${datadir}/mesa
    install -d ${D}${datadir}/pkgconfig

    cp -r -d --no-preserve=ownership ${S}/usr/include/* ${D}${includedir}
    cp -r -d --no-preserve=ownership ${S}/usr/include/EGL/*.h                      ${D}${includedir}/EGL
    cp -r -d --no-preserve=ownership ${S}/usr/include/GL/*.h                       ${D}${includedir}/GL
    cp -r -d --no-preserve=ownership ${S}/usr/include/GL/internal/*.h              ${D}${includedir}/GL/internal
    cp -r -d --no-preserve=ownership ${S}/usr/include/GLES/*.h                     ${D}${includedir}/GLES
    cp -r -d --no-preserve=ownership ${S}/usr/include/GLES2/*.h                    ${D}${includedir}/GLES2
    cp -r -d --no-preserve=ownership ${S}/usr/include/GLES3/*.h                    ${D}${includedir}/GLES3
    cp -r -d --no-preserve=ownership ${S}/usr/include/KHR/*.h                      ${D}${includedir}/KHR

    cp -r -d --no-preserve=ownership ${S}/usr/lib/lib*.so*                         ${D}${libdir}
    cp -r -d --no-preserve=ownership ${S}/usr/lib/dri/*.so*                        ${D}${libdir}/dri
    cp -r -d --no-preserve=ownership ${S}/usr/lib/pkgconfig/*.pc                   ${D}${libdir}/pkgconfig
    
    cp -r -d --no-preserve=ownership ${S}/usr/share/drirc.d/*.conf                 ${D}${datadir}/drirc.d
    cp -r -d --no-preserve=ownership ${S}/usr/share/mesa/*.xml                     ${D}${datadir}/mesa
    cp -r -d --no-preserve=ownership ${S}/usr/share/pkgconfig/*.pc                 ${D}${datadir}/pkgconfig
}
do_populate_lic[noexec] = "1"

FILES_${PN} += "${datadir}/mesa/wayland-drm.xml"
