DEPENDS += "elfutils-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:riscv64 = " file://canaan-k230.cfg"

KERNEL_DEVICETREE:append:riscv64 = " canaan/k230-canmv.dtb canaan/k230-evb.dtb"

IMAGES = "${THISDIR}/images"

k230_do_deploy() {
    if [ ! -d ${DEPLOYDIR}/.boot ]; then
        mkdir -p ${DEPLOY_DIR_IMAGE}/.boot
    fi

    if [ -f ${B}/arch/riscv/boot/Image ]; then
        cp -f ${B}/arch/riscv/boot/Image ${DEPLOY_DIR_IMAGE}/.boot
    fi

    dtbfiles=`ls -lt ${B}/arch/riscv/boot/dts/canaan/k230-*.dtb | awk '{print $9}'`
    for i in $dtbfiles;
    do
        if [ -f $i ]; then
            cp ${i} ${DEPLOY_DIR_IMAGE}/.boot
        fi
    done

    head=$(git --git-dir=${S}/.git rev-parse --verify HEAD 2>/dev/null)
    echo "commit-id:"${head} > ${DEPLOY_DIR_IMAGE}/.boot/kernel-release
}

do_deploy:append:riscv64 () {
    k230_do_deploy
}
