require gmp.inc

LICENSE = "GPL-2.0-or-later | LGPL-3.0-or-later"

LIC_FILES_CHKSUM = "\
        file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
        file://COPYING.LESSERv3;md5=6a6a8e020838b23406c81b19c1d46df6 \
        file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
        file://COPYINGv3;md5=11cc2d3ee574f9d6b7ee797bdce4d423 \
"

REVISION = ""
SRC_URI = "https://gmplib.org/download/${BPN}/${BP}${REVISION}.tar.bz2 \
           file://use-includedir.patch \
           file://0001-Append-the-user-provided-flags-to-the-auto-detected-.patch \
           file://0001-confiure.ac-Believe-the-cflags-from-environment.patch \
           "
SRC_URI[sha256sum] = "ac28211a7cfb609bae2e2c8d6058d66c8fe96434f740cf6fe2e47b000d1c20cb"

acpaths = ""

EXTRA_OECONF += " --enable-cxx=detect"
EXTRA_OECONF:append:mipsarchr6 = " --disable-assembly"

# GCC 15 拒绝 K&R 风格函数声明（configure 测试代码使用了该风格），
# 通过 -std=gnu17 回退到旧行为，保证 configure 与编译均可通过。
# 注意：gmp configure 自行调用 CC 测试编译器时不使用 CFLAGS，
# 必须将 -std=gnu17 注入 TARGET_CC_ARCH 使其成为 CC 命令的一部分。
TARGET_CC_ARCH:append = "${@' -std=gnu17' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
CFLAGS:append = "${@' -std=gnu17' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
BUILD_CFLAGS:append = "${@' -std=gnu17' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"

PACKAGES =+ "libgmpxx"
FILES:libgmpxx = "${libdir}/libgmpxx${SOLIBS}"

do_install:append() {
	oe_multilib_header gmp.h
}

fix_absolute_paths () {
        sed -i \
        -e "s|--sysroot=${STAGING_DIR_HOST}||g" \
        -e "s|${DEBUG_PREFIX_MAP}||g" \
         ${B}/gmp.h
}

do_install:prepend:class-target() {
    fix_absolute_paths
}

do_install:prepend:class-nativesdk() {
    fix_absolute_paths
}

SSTATE_SCAN_FILES += "gmp.h"

# Doesn't compile in MIPS16e mode due to use of hand-written
# assembly
MIPS_INSTRUCTION_SET = "mips"

BBCLASSEXTEND = "native nativesdk"
