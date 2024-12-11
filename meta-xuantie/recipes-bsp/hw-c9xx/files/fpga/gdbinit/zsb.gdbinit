# Set gdb environment
set confirm off
set height  0
set remotetimeout 20

define import_string_env
	shell echo set \$$arg0=\"$(echo $$arg0)\" > .gdb.tmp
	source .gdb.tmp
end

define import_integer_env
	shell echo set \$$arg0=$(echo $$arg0) > .gdb.tmp
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

# import input params
import_string_env target_remote
import_string_env abi_mode
import_string_env enable_uboot
import_string_env enable_optee
import_string_env opensbi_ver
import_string_env kernel_ver
import_string_env kernel_cfg
import_string_env rootfs_type
import_string_env restore_base_addr
import_string_env simulate_run

# import image names
import_string_env image_zsb
import_string_env image_dtb
import_string_env image_uboot
import_string_env image_opensbi
import_string_env image_optee
import_string_env image_kernel
import_string_env image_rootfs

# import control flags
# riscv64-elf-gdb not support $_streq, just support integer compare
import_integer_env rv64_std
import_integer_env rv32_std
import_integer_env simulate_run
import_integer_env ctl_enable_uboot
import_integer_env opensbi_0_9_with_kernel_5_10

printf "simulate_run=%d\n", $simulate_run
printf "rv64_std=%d\n", $rv64_std
printf "rv32_std=%d\n", $rv32_std
printf "ctl_enable_uboot=%d\n", $ctl_enable_uboot
printf "opensbi_0_9_with_kernel_5_10=%d\n", $opensbi_0_9_with_kernel_5_10
printf "restore_base_addr=%s\n", $restore_base_addr

# This is only for opensbi 0.9 with kernel 5.10
# plic delegate
if ($opensbi_0_9_with_kernel_5_10 == 1)
	if ($simulate_run == 0)
		set *0x081ffffc=1
	else
		printf "set *0x081ffffc=1\n"
	end
end

if ($simulate_run == 0)
	source gdbinit/zsb_socinit.gdbinit
end

##################################################

# memory layout
set $restore_base_addr=0x0
shell echo set \$restore_base_addr=$restore_base_addr > .gdb_base_addr.tmp
source .gdb_base_addr.tmp

#set $opensbi_addr = 0x60000000
set $opensbi_addr = $restore_base_addr
set $opensbi_next_img_addr = $opensbi_addr + 0x00400000
if ($ctl_enable_uboot == 1)
	set $uboot_addr   = $opensbi_next_img_addr
	set $vmlinux_addr = $opensbi_next_img_addr + 0x00400000
else
	set $vmlinux_addr = $opensbi_next_img_addr
end
set $rootfs_addr  = $opensbi_addr + 0x04000000
set $dtb_addr     = $rootfs_addr  - 0x00100000
set $zsb_addr     = $rootfs_addr  - 0x00008000
set $dyninfo_addr = $rootfs_addr  - 0x40
set $flag_addr    = $rootfs_addr  - 0x100

if ($simulate_run == 0)
	restore_xt $image_zsb      binary $zsb_addr
	restore_xt $image_dtb      binary $dtb_addr
	restore_xt $image_opensbi  binary $opensbi_addr
	if ($ctl_enable_uboot == 1)
		restore_xt $image_uboot  binary $uboot_addr
	end
	restore_xt $image_kernel   binary $vmlinux_addr
	restore_xt $image_rootfs   binary $rootfs_addr
else
	printf "restore %-60s binary 0x%x\n", $image_zsb, $zsb_addr
	printf "restore %-60s binary 0x%x\n", $image_dtb, $dtb_addr
	printf "restore %-60s binary 0x%x\n", $image_opensbi, $opensbi_addr
	if ($ctl_enable_uboot == 1)
		printf "restore %-60s binary 0x%x\n", $image_uboot, $uboot_addr
	end
	printf "restore %-60s binary 0x%x\n", $image_kernel, $vmlinux_addr
	printf "restore %-60s binary 0x%x\n", $image_rootfs, $rootfs_addr
end

# Set opensbi dynamic info param
if ($simulate_run == 0)
	if ($rv64_std == 1)
		set *(unsigned long *)($dyninfo_addr)      = 0x4942534f
		set *(unsigned long *)($dyninfo_addr + 8)  = 2
		set *(unsigned long *)($dyninfo_addr + 16) = $opensbi_next_img_addr
		set *(unsigned long *)($dyninfo_addr + 24) = 1
		set *(unsigned long *)($dyninfo_addr + 32) = 0
		set *(unsigned long *)($dyninfo_addr + 40) = 0
	else
		set *(unsigned long *)($dyninfo_addr)      = 0x4942534f
		set *(unsigned long *)($dyninfo_addr + 4)  = 2
		set *(unsigned long *)($dyninfo_addr + 8)  = $opensbi_next_img_addr
		set *(unsigned long *)($dyninfo_addr + 12) = 1
		set *(unsigned long *)($dyninfo_addr + 16) = 0
		set *(unsigned long *)($dyninfo_addr + 20) = 0
	end

	# Set 64lip32 flag off
	set *(unsigned int *)$flag_addr = 0x0

	# Set all harts reset address
	set *0x18030010 = $zsb_addr
	set *0x18030018 = $zsb_addr
	set *0x18030020 = $zsb_addr
	set *0x18030028 = $zsb_addr
	set *0x18030030 = $zsb_addr
	set *0x18030038 = $zsb_addr
	set *0x18030040 = $zsb_addr
	set *0x18030048 = $zsb_addr

	set $pc         = $zsb_addr

	# Release all harts from reset
	set *0x18030000 = 0x7f

	echo Finished GDBINIT\n\n
end
