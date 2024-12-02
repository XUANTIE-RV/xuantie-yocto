#!/bin/bash

# this scripts will do following to launch Linux
#   1. analyse input params, select restore images
#   2. update dts with correct image size
#   3. run gdbinit gdb scripts
#      a. run socinit gdb script
#      b. load all all images and start

source script/system_alias_config.sh

if [ "$#" -eq 0 ] || \
	{ [ "$#" -eq 1 ] && { [ "$1" = "--help" ] || [ "$1" = "-h" ]; }; }; then
	echo "Usage:  $0 <--target_remote=target_ip:port> [OPTION...]"
	echo ""
	echo "Examples:"
	echo "  run_fpga.sh --target_remote=192.168.0.1:1025"
	echo "  run_fpga.sh --target_remote=192.168.0.2:1025 \\"
	echo "              --abi_mode=rv64_std  \\"
	echo "              --enable_uboot=true \\"
#	echo "              --enable_optee=false \\"
	echo "              --opensbi_ver=1.3  \\"
	echo "              --kernel_ver=6.6 \\"
	echo "              --kernel_cfg=debug \\"
	echo "              --rootfs_type=lite \\"
#	echo "              --restore_base_addr=0x60000000"
	echo "  run_fpga.sh --target_remote=192.168.0.1:1025 --system=xuantie_rv64_std_kernel6_6_kcdefault"
	echo ""
	echo "[OPTION] format is: [--KEY=VALUE] defines below, the 1st value is DEFAULT."
	echo "     --target_remote      value: 192.168.0.1:1234"
	echo "     --abi_mode           value: rv64_std | rv32_std | rv32_comptible | rv32_new"
	echo "     --enable_uboot       value: false | true"
#	echo "     --enable_optee     value: false | true"
	echo "     --opensbi_ver        value: 1.3 | 0.9"
	echo "     --kernel_ver         value: 6.6 | 5.10 | 5.10@c906fdv | 5.10@c908v"
	echo "     --kernel_cfg         value: debug | default"
	echo "     --rootfs_type        value: lite"
#	echo "     --restore_base_addr  value: 0x60000000"
	echo "     --system             value: see below list"
	for item in "${system_alias[@]}"
	do
		echo "                                 $item"
	done
	echo "     --simulate_run       value: false | true"
	echo ""
	exit 0
fi

# declare input params_array
debuginfo=1
declare -A params_array
declare -a params_order

# set default values
params_array["target_remote"]="192.168.0.1:1025"; params_order+=("target_remote")
params_array["abi_mode"]="rv64_std";              params_order+=("abi_mode")
params_array["enable_uboot"]="false";             params_order+=("enable_uboot")
#params_array["enable_optee"]="false";           params_order+=("enable_optee")
params_array["opensbi_ver"]="1.3";                params_order+=("opensbi_ver")
params_array["kernel_ver"]="6.6";                 params_order+=("kernel_ver")
params_array["kernel_cfg"]="debug";               params_order+=("kernel_cfg")
params_array["rootfs_type"]="lite";               params_order+=("rootfs_type")
params_array["restore_base_addr"]="0x60000000";   params_order+=("restore_base_addr")
params_array["system"]="";                        params_order+=("system")
params_array["simulate_run"]="false";             params_order+=("simulate_run")

function is_valid_param() {
	local element="$1"
	for item in "${params_order[@]}"; do
		if [ "$item" == "$element" ]; then
			return 0
		fi
	done

	echo "Error[$LINENO]: param name '$element' not found"
	return 1
}

function is_valid_system() {
	local system_name="$1"
	for item in "${system_alias[@]}"
	do
		if [ "$item" == "$system_name" ]; then
			return 0
		fi
	done

	echo "Error[$LINENO]: system name '$system_name' not found"
	return 1
}

###############################################################################
# Iterate all cmd-line args, store into to params_array
# 1st, search "system" in input params and handle it
for arg in "$@"; do
	# check args match format: --key=value
	if [[ "$arg" =~ ^--([a-zA-Z0-9_]+)=(.*)$ ]]; then
		key="${BASH_REMATCH[1]}"
		value="${BASH_REMATCH[2]}"
		if [ $key == "system" ]; then
			echo "key="$key",value="$value
			if is_valid_system "$value"; then
				$value # regard $value as function call
			else
				echo "Error[$LINENO]: Invalid system: $arg"
				exit 1
			fi
		fi
	else
		echo "Error[$LINENO]: invalid parameter format: '$arg', should be '--KEY=VALUE'"
		exit 1
	fi
done

# 2nd, search other params and handle it(them)
for arg in "$@"; do
	# check args match format: --key=value
	if [[ "$arg" =~ ^--([a-zA-Z0-9_]+)=(.*)$ ]]; then
		key="${BASH_REMATCH[1]}"
		value="${BASH_REMATCH[2]}"
		if [ $key != "system" ]; then
			#echo "key="$key",value="$value
			if is_valid_param "$key"; then
				if [[ -n "$value" ]]; then
					params_array["$key"]="$value"
				else
					echo "Error[$LINENO]: please set value of '$arg'"
					exit 1
				fi
			else
				echo "Error[$LINENO]: Invalid argument: $arg"
				exit 1
			fi
		fi
	else
		echo "Error[$LINENO]: invalid parameter format: '$arg', should be '--KEY=VALUE'"
		exit 1
	fi
done

###############################################################################
# declare images_array and get value via params_array in get_images.sh
declare -A images_array
declare -a images_order
images_array["image_zsb"]="";       images_order+=("image_zsb")
images_array["image_dtb"]="hw.dtb"; images_order+=("image_dtb")
images_array["image_uboot"]="";     images_order+=("image_uboot")
images_array["image_opensbi"]="";   images_order+=("image_opensbi")
images_array["image_optee"]="";   images_order+=("image_optee")
images_array["image_kernel"]="";    images_order+=("image_kernel")
images_array["image_rootfs"]="";    images_order+=("image_rootfs")
source script/get_images.sh

# print all params_array items and export to environment
echo ">> input params:"
for id in "${!params_order[@]}"; do
	key=${params_order[id]}
	value=${params_array[$key]}
	export $key=$value
	echo "  $key = \"$value\""
done

###############################################################################
# declare control_array for gdb
declare -A gdb_control_array
gdb_control_array["rv64_std"]=0
gdb_control_array["rv32_std"]=0
gdb_control_array["simulate_run"]=0
gdb_control_array["ctl_enable_uboot"]=0
gdb_control_array["opensbi_0_9_with_kernel_5_10"]=0

if [ ${params_array["abi_mode"]} == "rv64_std" ]; then
	gdb_control_array["rv64_std"]=1
elif [ ${params_array["abi_mode"]} == "rv32_std" ]; then
	gdb_control_array["rv32_std"]=1
fi

if [ ${params_array["simulate_run"]} == "true" ]; then
	gdb_control_array["simulate_run"]=1
fi

if [ ${params_array["enable_uboot"]} == "true" ]; then
	gdb_control_array["ctl_enable_uboot"]=1
elif [ ${params_array["enable_uboot"]} == "false" ]; then
	gdb_control_array["ctl_enable_uboot"]=0
else
	echo "ERROR[$LINENO]: not supported uboot mode:'${params_array["enable_uboot"]}'"
	exit 1
fi

if [ ${params_array["opensbi_ver"]} == "0.9" ] && \
     ( [ ${params_array["kernel_ver"]} == "5.10" ] || \
       [ ${params_array["kernel_ver"]} == "5.10@c906fdv" ] || \
       [ ${params_array["kernel_ver"]} == "5.10@c908v" ] \
     ); then
	gdb_control_array["opensbi_0_9_with_kernel_5_10"]=1
fi
echo "<< gdb_control_array:"
for key in "${!gdb_control_array[@]}"; do
	value=${gdb_control_array[$key]}
	export $key=$value
	echo "  $key = $value"
done

###############################################################################
GDBINIT=gdbinit/zsb.gdbinit
SOCINIT=gdbinit/zsb_socinit.gdbinit
if [ ${params_array["abi_mode"]} == "rv64_std" ]; then
	DTS=dts/zsb_rv64.dts
else
	DTS=dts/zsb_rv32.dts
fi

if [ ! -f $GDBINIT -o ! -f $SOCINIT -o ! -f $DTS ]; then
	echo "Error: init file ${SOCINIT} or ${GDBINIT} or dts ${DTS} not exist !"
	exit 1
fi

# modify .hw.dts with correct rootfs size
echo ">> generate '.hw.dts'"
cp $DTS .hw.dts
ROOTFS_BASE=`cat .hw.dts | grep initrd-start | awk -F " " '{print $4}' | awk -F ">" '{print $1}'`
ROOTFS_SIZE=`ls -lt $(realpath ${images_array["image_rootfs"]}) | awk '{print $5}'`
((ROOTFS_END= $ROOTFS_BASE + $ROOTFS_SIZE))
ROOTFS_END=`printf "0x%x" $ROOTFS_END`
sed -i "s/linux,initrd-end = <0x0 .*/linux,initrd-end = <0x0 $ROOTFS_END>;/g" .hw.dts

# compile device tree file
dtc -I dts -O dtb .hw.dts > hw.dtb

# check all images_array image file exist before run
echo "<< images name:"
for id in "${!images_order[@]}"; do
	key=${images_order[id]}
	value=${images_array[$key]}
	export $key=$value
	echo "  $key = \"$value\""
	if [ -n $value ]; then
		if [ ! -e $value ] || [ ! -s $value ]; then
			echo "ERROR[$LINENO]: image '$value' not exist or size is 0"
			exit 1
		fi
	fi
done

echo ""
echo "=============================="

if [ ${params_array["simulate_run"]} == "false" ]; then
	# SoC related initialization and  boot linux with gdbinit
	if [ ${params_array["abi_mode"]} == "rv32-std" ]; then
		./riscv64-linux-gdb -q -ex "set architecture riscv:rv32" -ex "tar remote ${params_array["target_remote"]}" -x gdbinit/zsb.gdbinit -ex "c"
	else
		./riscv64-linux-gdb -q -ex "tar remote ${params_array["target_remote"]}" -x gdbinit/zsb.gdbinit -ex "c"
	fi
else
	echo ""
	./riscv64-linux-gdb -x gdbinit/zsb.gdbinit -ex "q"
fi
