inherit python3native python3-dir python3targetconfig

export STAGING_INCDIR
export STAGING_LIBDIR
export BUILD_SYS 
export HOST_SYS

RDEPENDS:${PN}-python += "python3"
PACKAGECONFIG_${PN}-python[xen] = ",,,xen-python"

PACKAGES += "${PN}-python-staticdev ${PN}-python-dev ${PN}-python-dbg ${PN}-python"

FILES:${PN}-python-staticdev += "${PYTHON_SITEPACKAGES_DIR}/*.a"
FILES:${PN}-python-dev += "${PYTHON_SITEPACKAGES_DIR}/*.la"
FILES:${PN}-python-dbg += "${PYTHON_SITEPACKAGES_DIR}/.debug/"
FILES:${PN}-python = "${bindir}/* ${libdir}/* ${libdir}/${PYTHON_DIR}/*"

SRC_URI += "http://libvirt.org/sources/python/libvirt-python-${PV}.tar.gz;name=libvirt_python"

SRC_URI[libvirt_python.sha256sum] = "a21ecfab6d29ac1bdd1bfd4aa3ef58447f9f70919aefecd03774613f65914e43"

export LIBVIRT_API_PATH = "${S}/docs/libvirt-api.xml"
export LIBVIRT_CFLAGS = "-I${S}/include"
export LIBVIRT_LIBS = "-L${B}/src/.libs -lvirt -ldl"
export LDFLAGS="-L${B}/src/.libs"

LIBVIRT_INSTALL_ARGS = "--root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${datadir}"

python __anonymous () {
    pkgconfig = d.getVar('PACKAGECONFIG')
    if ('python') in pkgconfig.split():
        d.setVar('LIBVIRT_PYTHON_ENABLE', '1')
    else:
        d.setVar('LIBVIRT_PYTHON_ENABLE', '0')
}

do_compile:append() {
	if [ "${LIBVIRT_PYTHON_ENABLE}" = "1" ]; then
		# we need the python bindings to look into our source dir, not
		# the syroot staged pkgconfig entries. So we clear the sysroot
		# for just this portion.
		export PKG_CONFIG_SYSROOT_DIR=
		cd ${WORKDIR}/${BPN}-python-${PV} && \
		  ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py build
	fi
}

do_install:append() {
	if [ "${LIBVIRT_PYTHON_ENABLE}" = "1" ]; then
		# we need the python bindings to look into our source dir, not
		# the syroot staged pkgconfig entries. So we clear the sysroot
		# for just this portion.
		export PKG_CONFIG_SYSROOT_DIR=
		cd ${WORKDIR}/${BPN}-python-${PV} && \
		  ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py install \
                       --install-lib=${D}/${PYTHON_SITEPACKAGES_DIR} ${LIBVIRT_INSTALL_ARGS}
	fi
}
