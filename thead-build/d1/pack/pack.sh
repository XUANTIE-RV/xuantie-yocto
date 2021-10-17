#!/bin/sh

IMAGE_PATH=../tmp-glibc/deploy/images/d1

cp -aLf ${IMAGE_PATH}/Image Image
cp -aLf ${IMAGE_PATH}/d1.dtb sunxi.fex
cp -af ${IMAGE_PATH}/fw_jump.bin opensbi.fex

if [ ! -f Image ]; then
    echo "Linux kernel \"Image\" not found!"
    exit -1
fi

if [ -d staging ]; then
    rm -rf staging
fi

if [ -f tina_d1-nezha_uart0.img ];then
    rm -rf tina_d1-nezha_uart0.img
fi

mkdir -p staging
[ -f prebuilts/boot.fex ] && {
    rm -rf prebuilts/boot.fex
}
cp -af prebuilts/* staging
mv Image staging/.
mv sunxi.fex staging/.
mv opensbi.fex staging/

cd staging
echo "make env.fex---------"
../tools/mkenvimage -r -p 0x00 -s 0x20000 -o env.fex env.cfg

echo "start to makimage---------"
../tools/mkbootimg --kernel Image --board  d1-nezha --base  0x40200000 --kernel_offset  0x0 --ramdisk_offset  0x01000000 -o boot.fex

echo "start to make boot_package.fex"
busybox unix2dos boot_package.cfg
../tools/dragonsecboot -pack boot_package.cfg

echo "do sys_partition----"
busybox unix2dos sys_partition.fex
../tools/script sys_partition.fex
echo "do update mbr----"
../tools/update_mbr sys_partition.bin
echo "do image.cfg ---"
../tools/dragon image.cfg sys_partition.fex
if [ -f tina_d1-nezha_uart0.img ]; then
    echo "move the final image"
    mv tina_d1-nezha_uart0.img ..
else
    echo "\033[0;31;1mpack failed!\033[0m"
    exit 1
fi


cd - > /dev/null 2>&1
[ -f tina_d1-nezha_uart0.img ] && {
    echo "pack finished."
    echo "\033[0;31;1mtina_d1-nezha_uart0.img\033[0m"
    md5sum tina_d1-nezha_uart0.img
}
