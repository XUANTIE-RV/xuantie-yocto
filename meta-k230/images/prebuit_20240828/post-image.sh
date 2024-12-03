#!/bin/bash
set -e
DTB="k230-canmv.dtb"

if [ $# -ne 2 ]; then
    exit 1
fi
image_type="$1"
build_image="$2"

BINARIES_DIR=$(pwd)
GENIMAGE_CFG_SD=./genimage.cfg


gen_image()
{
	#set -x;

	local genimage="./genimage"
	local cfg="genimage.cfg";
	local image_name="sysimage-sdcard.img";
	cd  "${BINARIES_DIR}/";
	cp "../../../xuantie-build/xuantie/tmp-glibc/deploy/images/riscv64-k6.6ry-kc${image_type}-tcxt/${build_image}-riscv64-k6.6ry-kc${image_type}-tcxt.ext4" ./rootfs.ext4

	GENIMAGE_TMP="genimage.tmp" ;	rm -rf "${GENIMAGE_TMP}";
	${genimage}   	--rootpath "${TARGET_DIR}"  --tmppath "${GENIMAGE_TMP}"    \
					--inputpath "$(pwd)"  	--outputpath "$(pwd)"	--config "${cfg}"

	rm -rf "${GENIMAGE_TMP}"
}

gen_boot_ext4()
{
	cp "../../../xuantie-build/xuantie/tmp-glibc/deploy/images/riscv64-k6.6ry-kc${image_type}-tcxt/Image" boot
	cp "../../../xuantie-build/xuantie/tmp-glibc/deploy/images/riscv64-k6.6ry-kc${image_type}-tcxt/k230-canmv.dtb" boot
	cd boot
	rm -rf k.dtb; ln -s ${DTB} k.dtb; cd -;
	rm -rf boot.ext4 ;fakeroot mkfs.ext4 -d boot  -r 1 -N 0 -m 1 -L "boot" -O ^64bit boot.ext4 50M
}

gen_boot_ext4
gen_image ${GENIMAGE_CFG_SD}   sysimage-sdcard.img

mv sysimage-sdcard.img ${build_image}-${image_type}-sdcard.img

