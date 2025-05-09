# enable for public release
# enable by xuantie-build/xuantie/linux-image-tools/scripts/yocto_process.py
# if enable XUANTIE_LINUX_TAG need set
MACHINECONFIG = "public"

XUANTIE_SRC_URI_PATH_EXT = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/${XUANTIE_LINUX_TAG}"
XUANTIE_GCC_SRC_URI_PATH = "${XUANTIE_SRC_URI_PATH_EXT}"
XUANTIE_CLANG_SRC_URI_PATH = "${XUANTIE_SRC_URI_PATH_EXT}"
XUANTIE_QEMU_NATIVE_SRC_URI_PATH = "${XUANTIE_SRC_URI_PATH_EXT}"
