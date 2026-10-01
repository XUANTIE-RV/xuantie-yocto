include dpdk.inc

SRC_URI += " file://0001-config-meson-get-cpu_instruction_set-from-meson-opti.patch "

SRCREV = "b3485f4293997d35b6daecc3437bb0c183a51fb3"
S = "${WORKDIR}/git"

COMPATIBLE_HOST:libc-musl:class-target = "null"

do_write_config:append:class-target() {
    sed -i "/\[properties\]/a \
platform = 'generic'\n\
vendor_id = 'generic'\n\
arch_id = 'generic'" ${WORKDIR}/meson.cross
    # For clang builds, replace -mcpu=<cpu> or -march=<full_arch> with
    # -march=<base_isa> in meson.cross. Clang with full arch strings
    # (e.g. rv64imafdcv_za64rs_..._zcmop_...) fails with "experimental
    # extension requires explicit version number". Using the base ISA
    # (e.g. rv64imafdcv) avoids this.
    if echo "${CC}" | grep -q clang; then
        base_isa=$(echo "${EXTRA_OEMESON}" | sed -n 's/.*-Dcpu_instruction_set=\([^ ]*\).*/\1/p')
        if [ -n "$base_isa" ] && [ "$base_isa" != "generic" ]; then
            sed -i "s/-m\(cpu\|arch\)=[^' ]*/-march=${base_isa}/g" ${WORKDIR}/meson.cross
        fi
    fi
}

def get_cpu_instruction_set(bb, d):
    import re, os, subprocess
    tune = d.getVar('XUANTIE_TUNE_CCARGS_GENERIC') or ''
    mcpu = re.search(r'-mcpu=([^\s]*)', tune)
    if mcpu:
        # For mcpu mode, query clang to derive the implied base ISA.
        # The CPU name (e.g. xt-c930v-cp) cannot be passed as -march=
        # to meson/DPDK, so we reconstruct the base ISA string from
        # clang's predefined macros.
        tc = d.getVar('EXTERNAL_TOOLCHAIN_CLANG') or ''
        clang = os.path.join(tc, 'bin', 'clang') if tc else ''
        mabi = d.getVar('XUANTIE_MABI') or 'lp64d'
        xlen = '64' if mabi.startswith('lp64') else '32'
        target = 'riscv' + xlen
        if clang and os.path.isfile(clang):
            try:
                cmd = [clang, '-mcpu=' + mcpu.group(1),
                       '-menable-experimental-extensions',
                       '--target=' + target, '-E', '-dM', '-']
                output = subprocess.check_output(
                    cmd, stderr=subprocess.DEVNULL,
                    stdin=subprocess.DEVNULL, text=True, timeout=10)
                exts = sorted(set(re.findall(
                    r'^#define __riscv_([mafdcv])\s', output, re.MULTILINE)))
                order = 'mafdcv'
                isa = 'rv' + xlen + 'i'
                for e in order:
                    if e in exts:
                        isa += e
                return isa
            except Exception:
                pass
        return 'generic'
    march = re.search(r'-march=([^\s]*)', tune)
    if march:
        # Strip experimental _z* extension segments that meson can't validate,
        # keep the base ISA including 'v' (e.g. rv64imafdcv)
        base = march.group(1).split('_')[0]
        return base if base.startswith('rv') else 'generic'
    return "generic"

EXTRA_OEMESON = " -Dexamples=all -Dcpu_instruction_set=${@get_cpu_instruction_set(bb, d)} "

COMPATIBLE_HOST:libc-musl:class-target = "null"
COMPATIBLE_HOST:linux-gnux32 = "null"

PACKAGECONFIG ??= " "
PACKAGECONFIG[afxdp] = ",,libbpf xdp-tools"
PACKAGECONFIG[libvirt] = ",,libvirt"

RDEPENDS:${PN} += "pciutils python3-core"
RDEPENDS:${PN}-examples += "bash"
DEPENDS = "numactl python3-pyelftools-native openssl"

inherit meson pkgconfig xuantie-set-package-arch-with-mcpu

# Per-mcpu 打包（同 mysql/ceph/openssl-xuantie 先例）：bbclass 置
# XUANTIE_PACKAGE_ARCH_SET=YES 后全局 TUNE_CCARGS 不再注入 -mcpu
# （xuantie-base.inc），recipe 级补回，否则 meson.cross 无 per-CPU 参数。
def dpdk_get_mcpu(d):
    if d.getVar('XUANTIE_TUNE_CCARGS_USE_MCPU') != 'NO' and d.getVar('XUANTIE_MCPU') and d.getVar('XUANTIE_MCPU') != d.getVar('XUANTIE_MCPU_GENERIC'):
        return " -mcpu=" + d.getVar('XUANTIE_MCPU');
    else:
        return d.getVar('XUANTIE_TUNE_CCARGS_GENERIC') or '';

TUNE_CCARGS:append:riscv64 = " ${@dpdk_get_mcpu(d)}"
TUNE_CCARGS:append:riscv32 = " ${@dpdk_get_mcpu(d)}"

INSTALL_PATH = "${prefix}/share/dpdk"

do_install:append(){
    # remove  source files
    rm -rf ${D}/${INSTALL_PATH}/examples/*

    # Install examples
    install -m 0755 -d ${D}/${INSTALL_PATH}/examples/
    for dirname in ${B}/examples/dpdk-*
    do
        if [ ! -d ${dirname} ] && [ -x ${dirname} ]; then
            install -m 0755 ${dirname} ${D}/${INSTALL_PATH}/examples/
        fi
    done

}

PACKAGES =+ "${PN}-examples ${PN}-tools"

FILES:${PN} += " ${bindir}/dpdk-testpmd \
         ${bindir}/dpdk-proc-info \
         ${libdir}/*.so* \
         ${libdir}/dpdk/pmds-24.0/*.so* \
         "
FILES:${PN}-examples = " \
    ${prefix}/share/dpdk/examples/* \
    "

FILES:${PN}-tools = " \
    ${bindir}/dpdk-pdump \
    ${bindir}/dpdk-test \
    ${bindir}/dpdk-test-* \
    ${bindir}/dpdk-*.py \
    "

CVE_PRODUCT = "data_plane_development_kit"

INSANE_SKIP:${PN} = "dev-so"
