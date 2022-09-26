DESCRIPTION = "repo for light proprietary image"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "light-*"

DEPENDS += " linux-thead xtensa-dsp "

SRC_URI = " \
            git://git@gitee.com/thead-yocto/light-images-proprietary.git;branch=master;protocol=http \
          "

THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"

S = "${WORKDIR}/git"

export SYSROOT_DIR="${RECIPE_SYSROOT}"
export KERNEL_VERSION="$(cat ${BASE_WORKDIR}/kernel_version)"

do_install() {
# // NPU proprietary install
    install -d ${D}${libdir}
    install -d ${D}${datadir}/npu
    install -d ${D}${datadir}/npu/test
    install -d ${D}${datadir}/npu/test/bin
    install -d ${D}${datadir}/npu/test/resource/cnn_testbench
    install -d ${D}${datadir}/npu/test/resource/cnn_testbench/lenet
    install -d ${D}${datadir}/npu/test/resource/cnn_testbench/lenet/OCM_1M5
    install -d ${D}${datadir}/npu/test/resource/nnvm_testbench

    install -m 0644 ${S}/npu/lib/lib*.so*                          ${D}${libdir}
    install -m 0755 ${S}/npu/ko/*.sh                               ${D}${datadir}/npu
    install -m 0755 ${S}/npu/test/run_*_testbench.sh               ${D}${datadir}/npu/test
    install -m 0755 ${S}/npu/test/*_testbench                      ${D}${datadir}/npu/test
    install -m 0755 ${S}/npu/test/bin/*                            ${D}${datadir}/npu/test/bin
    install -m 0755 ${S}/npu/test/resource/cnn_testbench/lenet/*.txt     ${D}${datadir}/npu/test/resource/cnn_testbench/lenet
    install -m 0755 ${S}/npu/test/resource/cnn_testbench/lenet/*.bin   ${D}${datadir}/npu/test/resource/cnn_testbench/lenet
    install -m 0755 ${S}/npu/test/resource/cnn_testbench/lenet/OCM_1M5/*   ${D}${datadir}/npu/test/resource/cnn_testbench/lenet/OCM_1M5
    install -m 0755 ${S}/npu/test/resource/nnvm_testbench/*        ${D}${datadir}/npu/test/resource/nnvm_testbench

# // FCE proprietary install
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
    install -d ${D}${libdir}
    install -d ${D}${includedir}/fce
    install -d ${D}${datadir}/fce
    install -d ${D}${datadir}/fce/test
    install -d ${D}${datadir}/fce/test/resource/featurelib/anti-incre_softmax
    install -d ${D}${datadir}/fce/test/resource/input

    install -m 0755 ${S}/fce/ko/*.ko                               ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0755 ${S}/fce/lib/lib*.so*                          ${D}${libdir}
    install -m 0755 ${S}/fce/include/*.h                           ${D}${includedir}/fce
    install -m 0755 ${S}/fce/test/*.sh                             ${D}${datadir}/fce
    install -m 0755 ${S}/fce/test/fce_*                            ${D}${datadir}/fce/test
    install -m 0755 ${S}/fce/test/resource/featurelib/anti-incre_softmax/*          ${D}${datadir}/fce/test/resource/featurelib/anti-incre_softmax
    install -m 0755 ${S}/fce/test/resource/input/*                       ${D}${datadir}/fce/test/resource/input

# // ddr-pmu proprietary install
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0644 ${S}/ddr-pmu/ko/*.ko                               ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra

# // libgal-viv proprietary install
    install -d ${D}${libdir}
    install -d ${D}${includedir}/gal-viv

    install -m 0755 ${S}/libgal-viv-sdk/drivers/*.so ${D}${libdir}
    cp -r ${S}/libgal-viv-sdk/include/HAL/* ${D}${includedir}/gal-viv

# // libcsi-g2d proprietary install
    install -d ${D}${libdir}
    install -d ${D}${includedir}/csi-g2d

    install -m 0755 ${S}/libcsi-g2d-sdk/libs/*.so ${D}${libdir}
    cp -r ${S}/libcsi-g2d-sdk/include/* ${D}${includedir}/csi-g2d

# // omxil proprietary install
    install -d ${D}${libdir}/omxil
    install -d ${D}${includedir}/omxil
    install -d ${D}${datadir}/omxil/test/bin

    install -m 0755 ${S}/vpu-omxil/${libdir}/omxil/*                ${D}${libdir}/omxil
    install -m 0755 ${S}/vpu-omxil/${includedir}/omxil/*            ${D}${includedir}/omxil
    install -m 0755 ${S}/vpu-omxil/${datadir}/omxil/test/bin/*test  ${D}${datadir}/omxil/test/bin

# // GPU proprietary install
    install -d ${D}${libdir}
    install -d ${D}/usr/local/bin/
    install -d ${D}/lib/firmware/
    install -d ${D}/etc/vulkan/icd.d/
    install -d ${D}/etc/OpenCL/vendors/
    install -d ${D}/etc/init.d/
    install -d ${D}/etc/modules-load.d/

    install -m 0755 ${S}/gpu_bxm_4_64/etc/modules-load.d/pvrsrvkm.conf              ${D}/etc/modules-load.d/
    install -m 0755 ${S}/gpu_bxm_4_64/usr/local/bin/*                               ${D}/usr/local/bin/
    install -m 0755 ${S}/gpu_bxm_4_64/etc/vulkan/icd.d/icdconf.json                 ${D}/etc/vulkan/icd.d/
    install -m 0755 ${S}/gpu_bxm_4_64/etc/OpenCL/vendors/IMG.icd                    ${D}/etc/OpenCL/vendors/
    install -m 0755 ${S}/gpu_bxm_4_64/lib/firmware/rgx.*                            ${D}/lib/firmware/
    install -m 0755 ${S}/gpu_bxm_4_64/etc/init.d/rc.pvr                             ${D}/etc/init.d/
    install -m 0755 ${S}/gpu_bxm_4_64/usr/lib/*.so*                                 ${D}/usr/lib/

# //vi system	start
    install -d ${D}${libdir}
    install -d ${D}${datadir}/vi
    install -d ${D}${datadir}/vi/dw200
    install -d ${D}${datadir}/vi/dw200/test
    install -d ${D}${datadir}/vi/dw200/test/case
    install -d ${D}${datadir}/vi/dw200/test/case/config
    install -d ${D}${datadir}/vi/dw200/test/case/resource
    install -d ${D}${datadir}/vi/dw200/test/case/usermap
    install -d ${D}${datadir}/vi/isp
    install -d ${D}${datadir}/vi/isp/test
    install -d ${D}${datadir}/vi/isp/test/ISP8000L_V2008
    install -d ${D}${datadir}/vi/isp/test/ISP8000L_V2008/case
    install -d ${D}${datadir}/vi/isp/test/ISP8000L_V2008/case/engine
    install -d ${D}${datadir}/vi/isp/test/ISP8000L_V2008/case/engine/config
    install -d ${D}${datadir}/vi/isp/test/ISP8000L_V2008/case/engine/config/baseline
    install -d ${D}${datadir}/vi/isp/test/ISP8000L_V2008/case/engine/config/format
    install -d ${D}${datadir}/vi/isp/test/ISP8000L_V2008/case/engine/config/scaling
    install -d ${D}${datadir}/vi/isp/test/ISP8000L_V2008/case/engine/resource
    install -d ${D}${datadir}/vi/isp_ry
    install -d ${D}${datadir}/vi/isp_ry/test
    install -d ${D}${datadir}/vi/isp_ry/test/ISP8000_V2009
    install -d ${D}${datadir}/vi/isp_ry/test/ISP8000_V2009/case
    install -d ${D}${datadir}/vi/isp_ry/test/ISP8000_V2009/case/engine
    install -d ${D}${datadir}/vi/isp_ry/test/ISP8000_V2009/case/engine/config
    install -d ${D}${datadir}/vi/isp_ry/test/ISP8000_V2009/case/engine/config/baseline
    install -d ${D}${datadir}/vi/isp_ry/test/ISP8000_V2009/case/engine/config/baseline/format
    install -d ${D}${datadir}/vi/isp_ry/test/ISP8000_V2009/case/engine/config/baseline/scaling
    install -d ${D}${datadir}/vi/isp_ry/test/ISP8000_V2009/case/engine/resource
    install -d ${D}${datadir}/vi/tuningtool
    install -d ${D}${datadir}/vi/tuningtool/bin
    install -d ${D}${datadir}/vi/tuningtool/lib
    install -d ${D}/vi/isp/include/
    install -d ${D}${includedir}/csi_hal

#csi_hal
    install -m 0755 ${S}/isp-isp8000l/hal/*.h                           ${D}${includedir}/csi_hal
#DEC400
    install -m 0644 ${S}/isp-isp8000l/dec400/lib/lib*.so*                          ${D}${libdir}
    #DW200
    install -m 0644 ${S}/isp-isp8000l/dw200/lib/lib*.so*                          ${D}${libdir}

#TUNING TOOL
    cd ${S}/isp-isp8000l/build/riscv64-unknown-linux-gnu/debug/appshell/generated/debug/bin/
    cp -Rv  --no-dereference --preserve=mode,links -v * ${D}${datadir}/vi/tuningtool/bin
    cd -
    cd ${S}/isp-isp8000l/dist/riscv64-unknown-linux-gnu/debug/bin/
    cp -Rv  --no-dereference --preserve=mode,links -v * ${D}${datadir}/vi/tuningtool/bin
    cd -
    rm -f ${D}${datadir}/vi/tuningtool/bin/DAA3840*
    rm -f ${D}${datadir}/vi/tuningtool/bin/daA3840*
    cd  ${S}/isp-isp8000l/build/riscv64-unknown-linux-gnu/debug/appshell/generated/debug/lib/
    cp -Rv  --no-dereference --preserve=mode,links -v lib*.so* ${D}${libdir}
    cd -

    cd  ${S}/isp-isp8000l/dist/riscv64-unknown-linux-gnu/debug/lib/
    cp -Rv  --no-dereference --preserve=mode,links -v *.so.* ${D}${libdir}
    cp -Rv  --no-dereference --preserve=mode,links -v pkgconfig ${D}${libdir}
    rm -f ${D}${libdir}/pkgconfig/*
    rm -f ${D}${libdir}/libdaA3840*
    cp ${S}/isp-isp8000l/csi_cfg.sh ${D}${datadir}/vi/tuningtool/bin
    cp ${S}/isp-isp8000l/isp/test/video_property.yaml ${D}${datadir}/vi/tuningtool/bin
    cd -
    install -m 0644 ${S}/isp-isp8000l/isp/lib/lib*.so*                          ${D}${libdir}
    cd ${S}/isp-isp8000l/isp/test/
    cp -R  --no-dereference --preserve=mode,links -v  * ${D}${datadir}/vi/isp/test
    cd -
#ISP RY
    install -m 0644 ${S}/isp-isp8000l/isp_ry/lib/lib*.so*                          ${D}${libdir}
    cd ${S}/isp-isp8000l/isp_ry/test/
    cp -R  --no-dereference --preserve=mode,links -v * ${D}${datadir}/vi/isp_ry/test
    cd -

    chrpath -d ${D}${datadir}/vi/isp/test/isp_test
    chrpath -d ${D}${datadir}/vi/isp_ry/test/isp_test
    chrpath -d ${D}${datadir}/vi/tuningtool/bin/isp_test
    rm -f ${D}${libdir}/libboost*.a
# //vi system end
}


PRIVATE_LIBS += "\
    libov5693.so.1\
    libov5693.so\
    libov5693.so.1.0.0\
    libIMX290.so.1\
    libIMX290.so\
    libIMX290.so.1.0.0\
    libov2775.so.1\
    libov2775.so\
    libov2775.so.1.0.0\
    libIMX334.so.1\
    libIMX334.so\
    libIMX334.so.1.0.0\
    libgc5035.so.1\
    libgc5035.so\
    libgc5035.so.1.0.0\
    libsc2310.so.1\
    libsc2310.so\
    libsc2310.so.1.0.0\
    libsc132gs.so.1\
    libsc132gs.so\
    libsc132gs.so.1.0.0\
    libov12870.so.1\
    libov12870.so\
    libov12870.so.1.0.0"

FILES_${PN} += " ${base_libdir} "
FILES_${PN} += " ${libdir} "
FILES_${PN} += " ${includedir} "
FILES_${PN} += " ${datadir} "
FILES_${PN} += " /usr/local/bin/ "

do_package_qa[noexec] = "1"
EXCLUDE_FROM_SHLIBS = "1"
PACKAGES = "${PN}"
INSANE_SKIP_${PN} += " arch staticdev debug-files already-stripped dev-deps file-rdeps installed-vs-shipped "
