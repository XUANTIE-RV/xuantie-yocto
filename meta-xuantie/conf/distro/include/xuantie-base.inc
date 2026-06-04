require conf/machine/include/riscv/tune-riscv.inc
require xuantie-clang.inc

DISTRO_NAME = "xuantie C9xx Release Distro"
DISTRO_VERSION = "1.3.2"

XUANTIE_LINUX_TAG = "XuanTie_V1.3.2"

EXTERNAL_TOOLCHAIN ?= "${TOPDIR}/../../riscv-toolchain"
TCMODE = "external"
TARGET_OS = "linux"
EXTERNAL_TOOLCHAIN_SYSROOT ?= "${EXTERNAL_TOOLCHAIN}/sysroot"

_EXTERNAL_TOOLCHAIN = "${EXTERNAL_TOOLCHAIN_GCC}"
_EXTERNAL_TOOLCHAIN:toolchain-clang = "${EXTERNAL_TOOLCHAIN_CLANG}"
export EXTERNAL_TOOLCHAIN_EXPORT ?= "${_EXTERNAL_TOOLCHAIN}"
BB_BASEHASH_IGNORE_VARS:append = " EXTERNAL_TOOLCHAIN_BIN EXTERNAL_TOOLCHAIN_LIBROOT \
    EXTERNAL_TOOLCHAIN_SYSROOT EXTERNAL_HEADERS_MULTILIB_SUFFIX \
    TOOLCHAIN_DIR EXTERNAL_TOOLCHAIN_EXPORT BUILD_ROOT RV_TOOLCHAIN "
INSANE_SKIP:${PN} += "glibc-so gobject-introspection-so glibc-external"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-xuantie"
KBUILD_DEFCONFIG ?= "defconfig"
SERIAL_CONSOLES ?= "115200;ttyS0"

PREFERRED_VERSION_op-tee ?= "0.1"
PREFERRED_VERSION_opensbi ?= "1.7"
PREFERRED_VERSION_libjpeg-turbo-native ?= "2.1.4"
PREFERRED_VERSION_qemu-native ?= "8.2.94"
PREFERRED_VERSION_kexec-tools ?= "2.0.22"
PREFERRED_VERSION_openssl ?= "3.4.0"

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

XUANTIE_PACKAGE_FEED_ARCH ??= "${TUNE_ARCH}"
PACKAGE_FEED_URIS ?= "http://114.55.254.174/xuantie/deb/release/${DISTRO_VERSION}/${XUANTIE_KERNEL_VERSION}/${XUANTIE_PACKAGE_FEED_ARCH}"
PACKAGE_FEED_ARCHS = "common"

INHERIT += "own-mirrors"
SOURCE_MIRROR_URL ?= "http://114.55.254.174/xuantie/downloads-mirror/1.x/release"
SSTATE_MIRRORS ?= "file://.* http://114.55.254.174/xuantie/sstate-cache/1.1/dev/${XUANTIE_LINUX_TAG}/${XUANTIE_KERNEL_VERSION}/PATH;downloadfilename=PATH"

PYTHON_PIP_URL ?= "http://114.55.254.174/th1520/python3/release/simple/"

IMAGE_FSTYPES += "cpio.gz ext4"

XUANTIE_MCPU_PACKAGE_ARCH ??= "${XUANTIE_MCPU}"
XUANTIE_PACKAGE_ARCH_SET ?= "NO"
XUANTIE_TUNE_CCARGS_USE_MCPU ??= "NO"

XUANTIE_TUNE_CCARGS ?= "-march=${XUANTIE_MARCH} -mabi=${XUANTIE_MABI}"
TUNE_CCARGS:append = "${@bb.utils.contains('XUANTIE_PACKAGE_ARCH_SET', 'NO', " ${XUANTIE_TUNE_CCARGS}", ' ', d)}"

EXTERNAL_TOOLCHAIN_ABI_DIR_NAME ?= "${XUANTIE_MABI}"
EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT ?= "lib64xthead"
# EXTERNAL_TOOLCHAIN_LIB_DIR_NAME_DEFAULT ?= "lib64"
EXTERNAL_TOOLCHAIN_ABI_DIR_NAME_DEFAULT ?= "lp64d"

# mirrors
XUANTIE_GIT ?= "git://git@gitee.com/xuantie-yocto"
MIRRORS += "\
${XUANTIE_GIT} git://git@github.com/XUANTIE-RV \
${XUANTIE_GIT} git://git@github.com/ruyisdk/ \
"

XUANTIE_GITSM ?= "gitsm://git@gitee.com/xuantie-yocto"
MIRRORS += "\
${XUANTIE_GITSM} gitsm://git@github.com/XUANTIE-RV \
${XUANTIE_GITSM} gitsm://git@github.com/ruyisdk/ \
"
PREFERRED_VERSION_opencv ?= "4.12.0"
PREFERRED_VERSION_libvirt ?= "v11.1.0+git"
PREFERRED_VERSION_openmpbench ?= "3.1"
PREFERRED_VERSION_openblas ?= "0.3.30"
# PREFERRED_VERSION_sysbench ?= "0.5"
PREFERRED_VERSION_sysbench ?= "1.0.20"
PREFERRED_VERSION_bcc ?= "0.35.0"
# PREFERRED_VERSION_ffmpeg ?= "7.0"
PREFERRED_VERSION_ffmpeg ?= "7.1"

## according to arch to select version
## 1.1 for prebuild; 1.2 for source code
PREFERRED_VERSION_spec2006 ?= "1.2"
PREFERRED_VERSION_spec2017 ?= "1.0.6"
PREFERRED_VERSION_lmbench ?= "3.0-a9"

# build static librarie
DISABLE_STATIC:pn-libarchive = ""
DISABLE_STATIC:pn-xz = ""
DISABLE_STATIC:pn-elfutils = ""
DISABLE_STATIC:pn-libtirpc = ""
DISABLE_STATIC:pn-libunwind = ""
DISABLE_STATIC:pn-cunit = ""
DISABLE_STATIC:pn-flex = ""

LD_LIBXTSTRING ?= ""

ROOT_HOME ?= "/root"

# LIBC bridge: environment variable -> internal XUANTIE_TCLIBC
# Usage: LIBC=muslc DISTRO=c907fdv bitbake ...
LIBC ?= "glibc"
XUANTIE_TCLIBC ?= "${LIBC}"
require xuantie-libc-${XUANTIE_TCLIBC}.inc

# kernel bridge: environment variable -> kernel config includes
# Usage: kernel=6.6-default DISTRO=c907fdv bitbake ...
# Valid values: 6.6-default, 6.6-debug, 5.10-default, 5.10-debug
# Existing kernel-combined configs (rv64-k6.6-default.conf etc.) include
# kernel configs explicitly and are unaffected by this default.
kernel ?= "6.6-default"
XUANTIE_KERNEL ?= "${kernel}"
include conf/distro/include/xuantie-kernel-${XUANTIE_KERNEL}.inc

python () {
    libc = d.getVar('LIBC')
    if libc and libc not in ('glibc', 'muslc'):
        bb.fatal("Invalid LIBC='%s'. Valid values: glibc, muslc" % libc)

    kernel_val = d.getVar('XUANTIE_KERNEL')
    if kernel_val:
        valid = ('6.6-default', '6.6-debug', '5.10-default', '5.10-debug')
        if kernel_val not in valid:
            bb.fatal("Invalid kernel='%s'. Valid values: %s" % (kernel_val, ', '.join(valid)))

    tc = d.getVar('TOOLCHAIN')
    if tc and tc not in ('gcc', 'clang'):
        bb.fatal("Invalid TOOLCHAIN='%s'. Valid values: gcc, clang" % tc)
    if libc == 'muslc' and tc == 'clang':
        raise bb.parse.SkipRecipe("LIBC=muslc + TOOLCHAIN=clang is not supported")
}

require xuantie-proprietary.inc
