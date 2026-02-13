inherit xuantie-prebuilt-src
EXTRA_DEB_DEPENDS:${XUANTIE_PACKAGE_COMMON_NAME}-app:xuantie-buildmode-prebuilt:class-target = "${XUANTIE_COMPUTE_LIB_DEPENDS} ${XUANTIE_PACKAGE_COMMON_NAME}"
EXTRA_DEB_DEPENDS:${XUANTIE_PACKAGE_COMMON_NAME}-lib:xuantie-buildmode-prebuilt:class-target = "${XUANTIE_COMPUTE_LIB_DEPENDS} ${XUANTIE_PACKAGE_COMMON_NAME}"
EXTRA_DEB_DEPENDS:${XUANTIE_PACKAGE_COMMON_NAME}:xuantie-buildmode-prebuilt:class-target = ""
FILES:${XUANTIE_PACKAGE_COMMON_NAME}-lib:xuantie-buildmode-prebuilt = ""
FILES:${XUANTIE_PACKAGE_COMMON_NAME}:xuantie-buildmode-prebuilt = "/usr"
