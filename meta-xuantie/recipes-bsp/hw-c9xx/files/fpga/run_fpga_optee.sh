#!/bin/bash

# this scripts will do following to launch Linux
#   1. copy your dts to .hw.dts
#   2. update dts with correct image size
#   3. run socinit gdb scripts (which is configure the SOC platfrom)
#   4. run gdbinit gdb scripts (which load linux images and start linux)

source script/system_alias_config.sh

if [ "$#" -eq 0 ] || \
	{ [ "$#" -eq 1 ] && { [ "$1" = "--help" ] || [ "$1" = "-h" ]; }; }; then
	echo "Usage:  $0 <--target_remote=target_ip:port> [OPTION...]"
	echo ""
	echo "Examples:"
	echo "  run_fpga.sh --target_remote=192.168.0.1:1025"
	echo "  run_fpga.sh --target_remote=192.168.0.2:1025 \\"
	echo "              --abi_mode=rv64_std  \\"
	echo "              --kernel_ver=6.6 \\"
	echo "              --kernel_cfg=debug \\"
	echo ""
	echo "[OPTION] format is: [--KEY=VALUE] defines below, the 1st value is DEFAULT."
	echo "     --target_remote      value: 192.168.0.1:1234"
	echo "     --abi_mode           value: rv64_std "
	echo "     --kernel_ver         value: 6.6 | 5.10 | 5.10@c908v"
	echo "     --kernel_cfg         value: debug | default"
	for item in "${system_alias[@]}"
	do
		echo "                                 $item"
	done
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
params_array["kernel_ver"]="6.6";                 params_order+=("kernel_ver")
params_array["kernel_cfg"]="debug";               params_order+=("kernel_cfg")

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
		echo "Error[$LINENO]: please set value of'$arg'"
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
		echo "Error[$LINENO]: please set value('$value') of '$arg'"
		exit 1
	fi
done

###############################################################################
# declare images_array and get value via params_array in get_images_optee.sh
declare -A images_array
declare -a images_order
images_array["image_zsb"]="";       images_order+=("image_zsb")
images_array["bl31_path"]="";       images_order+=("bl31_path")
images_array["teeos_path"]="";      images_order+=("teeos_path")
images_array["image_dtb"]="hw.dtb"; images_order+=("image_dtb")
images_array["image_kernel"]="";    images_order+=("image_kernel")
images_array["image_rootfs"]="";    images_order+=("image_rootfs")
source script/get_images_optee.sh

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

if [ ${params_array["abi_mode"]} == "rv64_std" ]; then
	gdb_control_array["rv64_std"]=1
elif [ ${params_array["abi_mode"]} == "rv32_std" ]; then
	gdb_control_array["rv32_std"]=1
fi

echo "<< gdb_control_array:"
for key in "${!gdb_control_array[@]}"; do
	value=${gdb_control_array[$key]}
	export $key=$value
	echo "  $key = $value"
done

###############################################################################

set -e
if [ ${params_array["abi_mode"]} == "rv64_std" ]; then
       BOARD="zsb_rv64"
fi
KERNEL=${params_array["kernel_ver"]}
TARGET_REMOTE=${params_array["target_remote"]}
SOCINIT=gdbinit/zsb_socinit.gdbinit
GDBINIT=gdbinit/${BOARD}_optee.gdbinit
if [[ $KERNEL == "5.10" || $KERNEL == "5.10@c908v" ]]; then
	DTS=dts/${BOARD}_optee_kernel-5.10.dts
else
	DTS=dts/${BOARD}_optee.dts
fi

if [[ "$KERNEL" == "5.10" || "$KERNEL" == "5.10@c908v" ]]; then
	ROOTFS=images/rootfs/rootfs.xuantie-image-rv64-5.10-lite.cpio.gz
elif [ $KERNEL == "6.6" ]; then
	ROOTFS=images/rootfs/rootfs.xuantie-image-rv64-6.6-lite.cpio.gz
else
	ROOTFS=0
fi

echo "======== run.sh launch info ========"
echo "remote  :" $TARGET_REMOTE
echo "platform:" $BOARD
echo "socinit :" $SOCINIT
echo "gdbinit :" $GDBINIT
echo "dts     :" $DTS
echo "rootfs  :" $ROOTFS
echo "==== ==== ==== ==== ==== ===="
if [ ! -f $GDBINIT -o ! -f $SOCINIT -o ! -f $DTS ]; then
	echo "Error: init file ${SOCINIT} or ${GDBINIT} or dts ${DTS} not exist !"
	exit 1
fi

echo ">> generate '.hw.dts'"
cp $DTS .hw.dts



# modify .hw.dts with correct rootfs size
ROOTFS_BASE=`cat .hw.dts | grep initrd-start | awk -F " " '{print $4}' | awk -F ">" '{print $1}'`
ROOTFS_SIZE=`ls -lt $(realpath ../$ROOTFS) | awk '{print $5}'`
((ROOTFS_END= $ROOTFS_BASE + $ROOTFS_SIZE))
ROOTFS_END=`printf "0x%x" $ROOTFS_END`
sed -i "s/linux,initrd-end = <0x0 .*/linux,initrd-end = <0x0 $ROOTFS_END>;/g" .hw.dts


set -x

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


# SoC related initialization and  boot linux with gdbinit
if [ $BOARD == "zsb_rv32" ]; then
	./riscv64-linux-gdb -q -ex "set architecture riscv:rv32" -ex "tar remote $TARGET_REMOTE" -x $SOCINIT -x $GDBINIT -ex "c"
else
	./riscv64-linux-gdb -q -ex "tar remote $TARGET_REMOTE" -x $SOCINIT -x $GDBINIT -ex "c"
fi
