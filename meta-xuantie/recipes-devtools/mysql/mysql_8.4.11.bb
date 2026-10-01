DESCRIPTION = "MySQL 8.4.11 LTS"
HOMEPAGE = "https://dev.mysql.com/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a40d502082d163654dd67d736a0c4b1f"

SRC_URI = "https://cdn.mysql.com/Downloads/MySQL-8.4/mysql-${PV}.tar.gz \
           file://0001-mysys-run-build_id_test-via-CMAKE_CROSSCOMPILING_EMULATOR.patch \
           file://0002-utilities-build-comp_err-when-cross-compiling-with-emulator.patch \
           file://0003-plugin-x-run-protoc-gen-yplg-through-emulator-wrapper.patch \
           file://0004-cmake-run-mysqld-help-for-info_bin-via-CMAKE_CROSSCOMPILING_EMULATOR.patch \
           "
SRC_URI[sha256sum] = "eb3051164d625dd346a8203f76e0d5d5d9aec51dbe9d51788e39ec6b3f1394c2"

S = "${WORKDIR}/mysql-${PV}"

# qemu-native: 交叉构建时 host 工具（protoc / comp_err / comp_sql / gen_lex_*）
# 经 CMAKE_CROSSCOMPILING_EMULATOR 在 qemu usermode 下运行
DEPENDS = "qemu-native openssl ncurses libtirpc gcc-runtime"

RDEPENDS:${PN} += "libssl libcrypto libtirpc perl ncurses"

inherit cmake pkgconfig binconfig xuantie-set-package-arch-with-mcpu

# 不注入 Yocto 默认优化参数（-O2 等），mysql cmake 原生 build type
# （默认 RelWithDebInfo）自带的优化级别生效
FULL_OPTIMIZATION = ""

# mcpu 体系：bbclass 置 XUANTIE_PACKAGE_ARCH_SET=YES 后全局 TUNE_CCARGS 不再注入
# -mcpu（xuantie-base.inc），per-mcpu 包需在 recipe 级补回（同 openssl-xuantie 先例）
def mysql_get_mcpu(d):
    if d.getVar('XUANTIE_TUNE_CCARGS_USE_MCPU') != 'NO' and d.getVar('XUANTIE_MCPU') and d.getVar('XUANTIE_MCPU') != d.getVar('XUANTIE_MCPU_GENERIC'):
        return " -mcpu=" + d.getVar('XUANTIE_MCPU');
    else:
        return d.getVar('XUANTIE_TUNE_CCARGS_GENERIC') or '';

TUNE_CCARGS:append:riscv64 = " ${@mysql_get_mcpu(d)}"
TUNE_CCARGS:append:riscv32 = " ${@mysql_get_mcpu(d)}"

# 交叉编译：构建期生成的目标架构 host 工具经 qemu usermode 运行；
# -L 指向目标 sysroot 以解析 glibc/openssl 等运行时库；
# -cpu ${QEMU_RISCV_CPU}：qemu 默认 rv64 CPU 不含 xtheadc 扩展，
# rv64gc_zfh_xtheadc 体系产物会 SIGILL（同 xuantie-base.inc 的 QEMU_OPTIONS 约定）；
# -E LD_LIBRARY_PATH：riscv64 glibc 加载器默认搜索 /lib64/lp64d、/usr/lib64/lp64d，
# 与 OE sysroot 布局（/lib、/usr/lib）不符，需显式指定（同 qemuwrapper-cross 约定）
EXTRA_OECMAKE = " \
    '-DCMAKE_CROSSCOMPILING_EMULATOR=${STAGING_BINDIR_NATIVE}/qemu-${TARGET_ARCH};-cpu;${QEMU_RISCV_CPU};-L;${STAGING_DIR_TARGET};-E;LD_LIBRARY_PATH=${STAGING_DIR_TARGET}${libdir}:${STAGING_DIR_TARGET}${base_libdir}' \
    -DWITH_PACKAGE_FLAGS=0 \
    -DWITH_UNIT_TESTS=OFF \
    -DWITH_ROUTER=OFF \
    -DINSTALL_MYSQLTESTDIR= \
    -DINSTALL_MYSQLDATADIR=${localstatedir}/lib/mysql \
    -DSYSCONFDIR=${sysconfdir} \
"

FILES:${PN} += " \
    ${bindir} \
    ${libdir} \
    ${libdir}/plugin \
    ${libdir}/private \
    ${includedir} \
    ${datadir} \
    ${prefix}/docs \
    ${prefix}/man \
    ${prefix}/support-files \
    ${prefix}/README \
    ${prefix}/LICENSE \
    ${prefix}/run \
    ${prefix}/var \
"

# mysql 自带 perl/shell 脚本引用运行时模块，QA 无法静态解析
INSANE_SKIP:${PN} += "file-rdeps"
