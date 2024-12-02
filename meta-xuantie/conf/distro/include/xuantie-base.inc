require conf/machine/include/riscv/tune-riscv.inc

DISTRO_NAME = "xuantie C9xx Release Distro"
DISTRO_VERSION = "1.0.6"

XUANTIE_LINUX_TAG = "XuanTie_V1.0.6"

EXTERNAL_TOOLCHAIN ?= "${TOPDIR}/../../riscv-toolchain"
TCMODE = "external"
TARGET_PREFIX = "riscv64-linux-"
TARGET_OS = "linux"
QEMU_TOOLCHAIN_PATH ?= "${EXTERNAL_TOOLCHAIN}/csky-qemu/bin/"
EXTERNAL_TOOLCHAIN_SYSROOT ?= "${EXTERNAL_TOOLCHAIN}/sysroot"

export EXTERNAL_TOOLCHAIN_EXPORT ?= "${EXTERNAL_TOOLCHAIN}"
BB_BASEHASH_IGNORE_VARS:append = " EXTERNAL_TOOLCHAIN_BIN EXTERNAL_TOOLCHAIN_LIBROOT \
    EXTERNAL_TOOLCHAIN_SYSROOT EXTERNAL_HEADERS_MULTILIB_SUFFIX QEMU_TOOLCHAIN_PATH \
    TOOLCHAIN_DIR EXTERNAL_TOOLCHAIN_EXPORT BUILD_ROOT RV_TOOLCHAIN "

INSANE_SKIP:${PN} += "glibc-so gobject-introspection-so glibc-external"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-xuantie"
KBUILD_DEFCONFIG ?= "defconfig"

PREFERRED_VERSION_op-tee ?= "0.1"
PREFERRED_VERSION_opensbi ?= "1.3"
PREFERRED_VERSION_libjpeg-turbo-native ?= "2.1.4"


KERNEL_IMAGETYPE = "Image"

MACHINE_FEATURES = "keyboard ext2 ext3 serial"
MACHINE_FEATURES:remove ?= " rtc "

ASSUME_PROVIDED += "\
    libcxx-dev \
    libcxx-staticdev \
    compiler-rt-dev \
    compiler-rt-staticdev \
"

WARN_QA:remove = " buildpaths "

__BBSEENSRCREV = "1"
do_cleanall[network] = "1"

python() {
    import re
    src_rev = d.getVar('XUANTIE_LINUX_TAG')
    if src_rev is not None:
        pattern = r"^Linux_SDK_V(\d+\.\d+\.\d+)$"
        match = re.match(pattern, src_rev)
        if match:
            version = match.group(1)
            version_limit = '.'.join([version.split('.')[0], version.split('.')[1], '99'])
        else:
            version = "1.0"
            version_limit = "1.99"
    else:
        version = "1.0"
        version_limit = "1.99"
    d.setVar('SDK_VERSION', version)
    d.setVar('BSP_VERSION', version)
    d.setVar('BSP_VERSION_LIMIT', version_limit)
}

PACKAGE_FEED_URIS ?= "http://114.55.254.174/xuantie/deb/release/1.x/${XUANTIE_KERNEL_VERSION}/${TUNE_ARCH}"
PACKAGE_FEED_ARCHS = "common"

INHERIT += "own-mirrors"
SOURCE_MIRROR_URL ?= "http://114.55.254.174/xuantie/downloads-mirror/1.x/release"
SSTATE_MIRRORS ?= "file://.* http://114.55.254.174/xuantie/sstate-cache/1.x/release/${XUANTIE_LINUX_TAG}/${XUANTIE_KERNEL_VERSION}/PATH;downloadfilename=PATH"

PYTHON_PIP_URL ?= "http://114.55.254.174/xuantie/python3/release/simple/"

PREFERRED_VERSION_opencv ?= "4.5.4"

IMAGE_FSTYPES += "cpio.gz ext4"

XUANTIE_PACKAGE_ARCH_SET ?= "NO"

XUANTIE_TUNE_CCARGS ?= "-march=${XUANTIE_MARCH} -mabi=${XUANTIE_MABI}"
TUNE_CCARGS:append = "${@bb.utils.contains('XUANTIE_PACKAGE_ARCH_SET', 'NO', " ${XUANTIE_TUNE_CCARGS}", ' ', d)}"

EXTERNAL_TOOLCHAIN_ABI_DIR_NAME ?= "${XUANTIE_MABI}"
