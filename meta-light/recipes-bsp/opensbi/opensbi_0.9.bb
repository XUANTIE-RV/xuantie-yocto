DESCRIPTION = "Light Opensbi Components"
LICENSE = "CLOSED"

inherit deploy
DEPENDS = "e2fsprogs-native"

COMPATIBLE_MACHINE = "light-*"

SRC_URI = " \
            git://git@gitee.com/thead-yocto/opensbi.git;branch=master;protocol=http \
            file://light_aon_fpga.bin;md5=eb0b2fc3765b2a8771b53915487d8a75 \
            file://light_aon_fpga.elf;md5=09dab875b6bbbbde6b2eeef126f11c8e \
            file://light_c906_audio.bin;md5=d11d1c42e6cbe432279286eac1eae940 \
            file://light_c906_audio.elf;md5=2663d4dab3fa10ec614467bf4a124fbe \
            file://lightA/light_aon_fpga.bin;md5=9fe5cc7adff55242d2b0a4889c71a168 \
            file://lightA/light_aon_fpga.elf;md5=1a4e09538174ecbd759085a6e8f04f05 \
            file://lightA/light_c906_audio.bin;md5=d11d1c42e6cbe432279286eac1eae940 \
            file://lightA/light_c906_audio.elf;md5=2663d4dab3fa10ec614467bf4a124fbe \
            file://lightB/light_aon_fpga.bin;md5=971a79b8d361a505a765086588a0e405 \
            file://lightB/light_aon_fpga.elf;md5=b4e433d389ceb52dea290df9b1ece8b6 \
            file://lightB/light_c906_audio.bin;md5=67141bb799ccad041dd34c49081d6905 \
            file://lightB/light_c906_audio.elf;md5=f07966e101cb8c047b7b93c183dde9ac \
            file://light-ant-evt/light_aon_fpga.bin;md5=82270517191cfd055107811a3b882b7a \
            file://light-ant-evt/light_aon_fpga.elf;md5=82d291f9d78f2d9cac85bd815f050f8a \
            file://light-ant-evt/light_c906_audio.bin;md5=9124f7306370eb0d9e3f6178da1fa4fd \
            file://light-ant-evt/light_c906_audio.elf;md5=b4206da84148852df208088c96e4b2b3 \
            file://light-ant-discrete/light_aon_fpga.bin;md5=785c06c0eab5ff414375d6ce08c8940e \
            file://light-ant-discrete/light_aon_fpga.elf;md5=2da373f3e1950c9a9464be8c490cb25e \
            file://light-ant-discrete/light_c906_audio.bin;md5=3c5d2ccb84c98fb1dbcacec72e49ee96 \
            file://light-ant-discrete/light_c906_audio.elf;md5=b4206da84148852df208088c96e4b2b3 \
          "

THEAD_BSP_TAG ?= "${AUTOREV}"
SRCREV = "${THEAD_BSP_TAG}"

S = "${WORKDIR}/git"

export ARCH?="riscv"
export CROSS_COMPILE="riscv64-linux-"
export PLATFORM="generic"
export TOOLCHAIN_DIR?="${EXTERNAL_TOOLCHAIN}"

PARALLEL_MAKEINST = "-j1"

do_compile() {
	oe_runmake
}

do_deploy () {
	install -m 0755 ${S}/build/platform/generic/firmware/fw_dynamic.bin ${DEPLOYDIR}/
	install -m 0755 ${S}/build/platform/generic/firmware/fw_dynamic.elf ${DEPLOYDIR}/

    if  echo "${MACHINE}" | grep -q "light-a-val"; then
		echo "Firmware INFO: light-evb opensbi and e902 c906 firmware build MACHINE = ${MACHINE}"
		install -m 0755 ${WORKDIR}/light_aon_fpga.bin ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/light_aon_fpga.elf ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/light_c906_audio.bin ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/light_c906_audio.elf ${DEPLOYDIR}/
	elif echo "${MACHINE}" | grep -q "light-ant-discrete"; then
		echo "Firmware INFO: light ant-evt  opensbi and e902 c906 firmware build MACHINE = ${MACHINE}"
		install -m 0755 ${WORKDIR}/light-ant-discrete/light_aon_fpga.bin ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/light-ant-discrete/light_aon_fpga.elf ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/light-ant-discrete/light_c906_audio.bin ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/light-ant-discrete/light_c906_audio.elf ${DEPLOYDIR}/
	elif echo "${MACHINE}" | grep -q "light-ant-evt"; then
		echo "Firmware INFO: light ant-evt  opensbi and e902 c906 firmware build MACHINE = ${MACHINE}"
		install -m 0755 ${WORKDIR}/light-ant-evt/light_aon_fpga.bin ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/light-ant-evt/light_aon_fpga.elf ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/light-ant-evt/light_c906_audio.bin ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/light-ant-evt/light_c906_audio.elf ${DEPLOYDIR}/
	elif echo "${MACHINE}" | grep -q "light-a-product"; then
		echo "Firmware INFO: light-a opensbi and e902 c906 firmware build MACHINE = ${MACHINE}"
		install -m 0755 ${WORKDIR}/lightA/light_aon_fpga.bin ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/lightA/light_aon_fpga.elf ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/lightA/light_c906_audio.bin ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/lightA/light_c906_audio.elf ${DEPLOYDIR}/
	elif echo "${MACHINE}" | grep -q "light-b-product"; then
		echo "Firmware INFO: light b opensbi and e902 c906 firmware build MACHINE = ${MACHINE}"
		install -m 0755 ${WORKDIR}/lightB/light_aon_fpga.bin ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/lightB/light_aon_fpga.elf ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/lightB/light_c906_audio.bin ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/lightB/light_c906_audio.elf ${DEPLOYDIR}/
	else
		echo "Firmware INFO: light general opensbi and e902 c906 firmware build MACHINE = ${MACHINE}"
		install -m 0755 ${WORKDIR}/light_aon_fpga.bin ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/light_aon_fpga.elf ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/light_c906_audio.bin ${DEPLOYDIR}/
		install -m 0755 ${WORKDIR}/light_c906_audio.elf ${DEPLOYDIR}/
	fi

    if [ ! -d ${DEPLOY_DIR_IMAGE}/.boot ]; then
       mkdir -p ${DEPLOY_DIR_IMAGE}/.boot
    fi

    if [ -f ${DEPLOYDIR}/fw_dynamic.bin ]; then
       cp -f ${DEPLOYDIR}/fw_dynamic.bin ${DEPLOY_DIR_IMAGE}/.boot
    fi

    if [ -f ${DEPLOYDIR}/light_aon_fpga.bin ]; then
       cp -f ${DEPLOYDIR}/light_aon_fpga.bin ${DEPLOY_DIR_IMAGE}/.boot
    fi

    if [ -f ${DEPLOYDIR}/light_c906_audio.bin ]; then
       cp -f ${DEPLOYDIR}/light_c906_audio.bin ${DEPLOY_DIR_IMAGE}/.boot
    fi

    dd if=/dev/zero of=${DEPLOY_DIR_IMAGE}/boot.ext4 count=10000 bs=4096
    ${COMPONENTS_DIR}/x86_64/e2fsprogs-native/sbin/mkfs.ext4 -F  ${DEPLOY_DIR_IMAGE}/boot.ext4 -d ${DEPLOY_DIR_IMAGE}/.boot
}

do_deploy[nostamp] = "1"
addtask deploy before do_build after do_install

FILES_${PN} += " ${datadir} "
PACKAGES = "${PN}"

INSANE_SKIP_${PN} += " debug-files "

