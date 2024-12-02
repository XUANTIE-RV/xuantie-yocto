LICENSE = "CLOSED"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PERF_SRC_URI_5_10_y = " \
            file://0002-perf-annotate-Add-riscv64-support.patch \
"

SRC_URI:append = "${@bb.utils.contains('PREFERRED_VERSION_linux-xuantie', '5.10.y', '${PERF_SRC_URI_5_10_y}', '', d)}"

python do_fetch_perf () {
    bb.build.exec_func('base_do_fetch', d)
}

python do_patch_perf () {
    bb.build.exec_func('patch_do_patch', d)
}

addtask do_fetch_perf after do_configure before do_patch_perf
addtask do_patch_perf after do_fetch_perf before do_compile
do_patch_perf[dirs] = "${WORKDIR}"
do_patch_perf[depends] = "${PATCHDEPENDENCY}"
