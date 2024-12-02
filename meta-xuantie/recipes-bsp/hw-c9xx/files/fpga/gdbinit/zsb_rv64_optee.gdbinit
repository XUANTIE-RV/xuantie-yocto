# Set gdb environment
set confirm off
set height  0

define import_string_env
        shell echo set \$$arg0=\"$(echo $$arg0)\" > .gdb.tmp
        source .gdb.tmp
end

define restore_xt
        set $img = $arg0
        set $addr = $arg2

        shell rm -f .gdb.log
        set logging file .gdb.log
        set logging enabled on
        printf "restore %s binary 0x%x\n", $img, $addr
        set logging enabled off

        source .gdb.log
end

import_string_env image_zsb
import_string_env bl31_path
import_string_env teeos_path
import_string_env image_dtb
import_string_env image_kernel
import_string_env image_rootfs 

# This is only for opensbi 0.9 with 5.10 kernel
# plic delegate
#set *0x081ffffc=1
# memory layout
set $vmlinux_addr = 0x80000000 + 0x00200000
set $rootfs_addr  = 0x80000000 + 0x04000000
set $zsb_addr     = 0x60000000 + 0x04000000 - 0x00008000
set $dtb_addr     = 0x60000000 + 0x04000000 - 0x00100000
set $bl31_addr    = 0x60000000
set $teeos_addr   = 0xef000000

# Load zsb for tee
restore_xt $image_zsb                                                   binary $zsb_addr
# Load bl31
restore_xt $bl31_path                                                   binary $bl31_addr
# load tee
restore_xt $teeos_path                                                  binary $teeos_addr
# Load dtb
restore_xt $image_dtb                                                   binary $dtb_addr
# Load rootfs & kernel
restore_xt $image_kernel                                                binary $vmlinux_addr
restore_xt $image_rootfs                                                binary $rootfs_addr
# Set all harts reset address
set *(unsigned long *)0x18030010 = $zsb_addr
set *(unsigned long *)0x18030018 = $zsb_addr
set *(unsigned long *)0x18030020 = $zsb_addr
set *(unsigned long *)0x18030028 = $zsb_addr
set *(unsigned long *)0x18030030 = $zsb_addr
set *(unsigned long *)0x18030038 = $zsb_addr
set *(unsigned long *)0x18030040 = $zsb_addr
set *(unsigned long *)0x18030048 = $zsb_addr
set $pc         = $zsb_addr
# Release all harts from reset
set *0x18030000 = 0x7f
echo Finished GDBINIT\n\n

