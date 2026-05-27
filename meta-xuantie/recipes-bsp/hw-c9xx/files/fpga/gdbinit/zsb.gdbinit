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

# import image names
import_string_env image_zsb
import_string_env image_dtb
import_string_env image_uboot
import_string_env image_opensbi
import_string_env image_kernel
import_string_env image_rootfs

# import control flags
# riscv64-elf-gdb not support $_streq, just support integer compare
import_integer_env skip_load_image
import_integer_env rva23_profile
import_integer_env rva20_profile
import_integer_env rv64_std
import_integer_env rv32_std
import_integer_env simulate_run
import_integer_env ctl_enable_uboot
import_integer_env opensbi_0_9_with_kernel_5_10
import_integer_env ctl_enable_2core

printf "skip_load_image=%d\n", $skip_load_image
printf "simulate_run=%d\n", $simulate_run
printf "rva23_profile=%d\n", $rva23_profile
printf "rva20_profile=%d\n", $rva20_profile
printf "rv64_std=%d\n", $rv64_std
printf "ctl_enable_uboot=%d\n", $ctl_enable_uboot
printf "ctl_enable_2core=%d\n", $ctl_enable_2core
printf "opensbi_0_9_with_kernel_5_10=%d\n", $opensbi_0_9_with_kernel_5_10

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
	monitor set resume-bkpt-exception on
	source gdbinit/zsb_socinit.gdbinit
end

##################################################
# memory layout according to zsb design (after Commit 1fb8f58)
set $opensbi_addr = 0x60000000
if ($ctl_enable_uboot == 1)
	set $uboot_addr   = 0x60400000
	set $vmlinux_addr = $uboot_addr + 0x00400000
else
	set $uboot_addr   = 0x0
	set $vmlinux_addr = 0x60400000
end

if ($opensbi_0_9_with_kernel_5_10 == 1)
	set $dtb_addr     = 0x63f00000
	set $zsb_addr     = 0x63ff8000
else
	set $dtb_addr     = 0x60300000
	set $zsb_addr     = 0x603F8000
end

set $flag_addr    = 0x603FFF00
set $rootfs_addr  = 0x64000000

set $dyninfo_addr = $rootfs_addr  - 0x40


printf "====================================\n"
printf "opensbi_addr=0x%x\n", $opensbi_addr
printf "uboot_addr  =0x%x\n", $uboot_addr
printf "vmlinux_addr=0x%x\n", $vmlinux_addr
printf "rootfs_addr =0x%x\n", $rootfs_addr
printf "zsb_addr    =0x%x\n", $zsb_addr
printf "dtb_addr    =0x%x\n", $dtb_addr
printf "flag_addr   =0x%x\n", $flag_addr
printf "dyninfo_addr   =0x%x\n", $dyninfo_addr
printf "====================================\n"

if ($simulate_run == 0)
	if ($skip_load_image == 0)
		printf "start load images by gdb\n"
		restore_xt $image_zsb      binary $zsb_addr
		restore_xt $image_dtb      binary $dtb_addr
		restore_xt $image_opensbi  binary $opensbi_addr
		if ($ctl_enable_uboot == 1)
			restore_xt $image_uboot  binary $uboot_addr
		end
		restore_xt $image_kernel   binary $vmlinux_addr
		restore_xt $image_rootfs   binary $rootfs_addr
	else
		printf "skip load images by gdb\n"
	end
else
	if ($skip_load_image == 0)
		printf "start load images by gdb\n"
		printf "restore %-60s binary 0x%x\n", $image_zsb, $zsb_addr
		printf "restore %-60s binary 0x%x\n", $image_dtb, $dtb_addr
		printf "restore %-60s binary 0x%x\n", $image_opensbi, $opensbi_addr
		if ($ctl_enable_uboot == 1)
			printf "restore %-60s binary 0x%x\n", $image_uboot, $uboot_addr
		end
		printf "restore %-60s binary 0x%x\n", $image_kernel, $vmlinux_addr
		printf "restore %-60s binary 0x%x\n", $image_rootfs, $rootfs_addr
	else
		printf "skip load images by gdb\n"
	end
end

# Set opensbi dynamic info param
if ($simulate_run == 0)
	if ($rv64_std == 1)
		set *(unsigned long *)($dyninfo_addr)      = 0x4942534f
		set *(unsigned long *)($dyninfo_addr + 8)  = 2
		set *(unsigned long *)($dyninfo_addr + 16) = 0x60400000
		set *(unsigned long *)($dyninfo_addr + 24) = 1
		set *(unsigned long *)($dyninfo_addr + 32) = 0
		set *(unsigned long *)($dyninfo_addr + 40) = 0
	else
		set *(unsigned long *)($dyninfo_addr)      = 0x4942534f
		set *(unsigned long *)($dyninfo_addr + 4)  = 2
		set *(unsigned long *)($dyninfo_addr + 8)  = 0x60400000
		set *(unsigned long *)($dyninfo_addr + 12) = 1
		set *(unsigned long *)($dyninfo_addr + 16) = 0
		set *(unsigned long *)($dyninfo_addr + 20) = 0
	end

	# Set 64lip32 flag off
	set *(unsigned int *)$flag_addr = 0x0

	if ($ctl_enable_2core == 0)
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
		set *0x18030000 = 0xff
	else
		# Set all harts reset address
		set *0x26808020 = $zsb_addr
		set *0x26828020 = $zsb_addr
		set *0x26888020 = $zsb_addr
		set *0x268a8020 = $zsb_addr
		set *0x26908020 = $zsb_addr
		set *0x26928020 = $zsb_addr
		set *0x26988020 = $zsb_addr
		set *0x269a8020 = $zsb_addr
		set *0x26a08020 = $zsb_addr
		set *0x26a28020 = $zsb_addr
		set *0x26a88020 = $zsb_addr
		set *0x26aa8020 = $zsb_addr
		set *0x26b08020 = $zsb_addr
		set *0x26b28020 = $zsb_addr
		set *0x26b88020 = $zsb_addr
		set *0x26ba8020 = $zsb_addr

		set *(unsigned long *)0x26808100 = 0x80000008
		set *(unsigned long *)0x26828100 = 0x80000008
		set *(unsigned long *)0x26888100 = 0x80000008
		set *(unsigned long *)0x268a8100 = 0x80000008
		set *(unsigned long *)0x26908100 = 0x80000008
		set *(unsigned long *)0x26928100 = 0x80000008
		set *(unsigned long *)0x26988100 = 0x80000008
		set *(unsigned long *)0x269a8100 = 0x80000008
		set *(unsigned long *)0x26a08100 = 0x80000008
		set *(unsigned long *)0x26a28100 = 0x80000008
		set *(unsigned long *)0x26a88100 = 0x80000008
		set *(unsigned long *)0x26aa8100 = 0x80000008
		set *(unsigned long *)0x26b08100 = 0x80000008
		set *(unsigned long *)0x26b28100 = 0x80000008
		set *(unsigned long *)0x26b88100 = 0x80000008
		set *(unsigned long *)0x26ba8100 = 0x80000008

		set $pc         = $zsb_addr

		# Release all harts from reset
		set *0x18030000 = 0xffff
	end
	echo Finished GDBINIT\n\n
end
