SUMMARY = "Middleware layer between GObject-using C libraries and language bindings"
DESCRIPTION = "GObject Introspection is a project for providing machine \
readable introspection data of the API of C libraries. This introspection \
data can be used in several different use cases, for example automatic code \
generation for bindings, API verification and documentation generation."
HOMEPAGE = "https://wiki.gnome.org/action/show/Projects/GObjectIntrospection"

do_configure:prepend() {
   cp -rf ${QEMU_TOOLCHAIN_PATH}/*  ${WORKDIR}/recipe-sysroot-native/usr/bin/ 
}
