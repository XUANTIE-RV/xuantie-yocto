image_path="../images"

# get zsb image
if [[ ${params_array["abi_mode"]} == "rv64_std" ]]; then
	images_array["image_zsb"]=$image_path/zsb/zero_stage_boot-rv64.bin
elif [[ ${params_array["abi_mode"]} == "rv32_std" ]]; then
	images_array["image_zsb"]=$image_path/zsb/zero_stage_boot-rv32.bin
fi

# get uboot image
if [[ ${params_array["enable_uboot"]} == "true" ]]; then
	if [[ ${params_array["abi_mode"]} == "rv64_std" ]]; then
		images_array["image_uboot"]=$image_path/u-boot/u-boot-fpga-rv64.bin
	elif [[ ${params_array["abi_mode"]} == "rv32_std" ]]; then
		images_array["image_uboot"]=$image_path/u-boot/u-boot-fpga-rv32.bin
	else
		echo "Error[$LINENO]: not supported abi_mode:'${params_array["abi_mode"]}'"
		exit 1
	fi
fi

# get opensbi image
if [[ ${params_array["abi_mode"]} == "rv64_std" ]]; then
	if [[ ${params_array["opensbi_ver"]} == "1.3" ]]; then
		images_array["image_opensbi"]=$image_path/opensbi/fw_dynamic-v1.3-rv64.bin
	elif [[ ${params_array["opensbi_ver"]} == "0.9" ]]; then
		images_array["image_opensbi"]=$image_path/opensbi/fw_dynamic-v0.9-rv64.bin
	else
		echo "Error: not supported abi_mode:${params_array["abi_mode"]}"
		exit 1
	fi
elif [[ ${params_array["abi_mode"]} == "rv32_std" ]]; then
	if [[ ${params_array["opensbi_ver"]} == "1.3" ]]; then
		images_array["image_opensbi"]=$image_path/opensbi/fw_dynamic-v1.3-rv32.bin
	elif [[ ${params_array["opensbi_ver"]} == "0.9" ]]; then
		images_array["image_opensbi"]=$image_path/opensbi/fw_dynamic-v0.9-rv32.bin
	else
		echo "Error: not supported abi_mode:'${params_array["abi_mode"]}'"
		exit 1
	fi
fi

# get optee image, fixme!!!

# get kernel image
if [[ ${params_array["abi_mode"]} == "rv64_std" ]]; then
	if [[ ${params_array["kernel_ver"]} == "6.6" ]]; then
		if [[ ${params_array["kernel_cfg"]} == "default" ]]; then
			images_array["image_kernel"]=$image_path/kernel/Image-6.6-rv64-kcdefault
		elif [[ ${params_array["kernel_cfg"]} == "debug" ]]; then
			images_array["image_kernel"]=$image_path/kernel/Image-6.6-rv64-kcdebug
		else
			echo "Error[$LINENO]: not supported kernel with:" \
			     "abi_mode=${params_array["abi_mode"]}," \
				 "kernel_ver=${params_array["kernel_ver"]}," \
				 "kernel_cfg=${params_array["kernel_cfg"]}"
			exit 1
		fi
	elif [[ ${params_array["kernel_ver"]} == "5.10" ]]; then
		if [[ ${params_array["kernel_cfg"]} == "default" ]]; then
			images_array["image_kernel"]=$image_path/kernel/Image-5.10-rv64-kcdefault
		elif [[ ${params_array["kernel_cfg"]} == "debug" ]]; then
			images_array["image_kernel"]=$image_path/kernel/Image-5.10-rv64-kcdebug
		else
			echo "Error[$LINENO]: not supported kernel with:" \
			     "abi_mode=${params_array["abi_mode"]}," \
				 "kernel_ver=${params_array["kernel_ver"]}," \
				 "kernel_cfg=${params_array["kernel_cfg"]}"
			exit 1
		fi
	elif [[ ${params_array["kernel_ver"]} == "5.10@c906fdv" ]]; then
		if [[ ${params_array["kernel_cfg"]} == "default" ]]; then
			images_array["image_kernel"]=$image_path/kernel/Image-5.10-rv64-kcdefault@c906fdv
		elif [[ ${params_array["kernel_cfg"]} == "debug" ]]; then
			images_array["image_kernel"]=$image_path/kernel/Image-5.10-rv64-kcdebug@c906fdv
		else
			echo "Error[$LINENO]: not supported kernel with:" \
			     "abi_mode=${params_array["abi_mode"]}," \
				 "kernel_ver=${params_array["kernel_ver"]}," \
				 "kernel_cfg=${params_array["kernel_cfg"]}"
			exit 1
		fi
	elif [[ ${params_array["kernel_ver"]} == "5.10@c908v" ]]; then
		if [[ ${params_array["kernel_cfg"]} == "default" ]]; then
			images_array["image_kernel"]=$image_path/kernel/Image-5.10-rv64-kcdefault@c908v
		elif [[ ${params_array["kernel_cfg"]} == "debug" ]]; then
			images_array["image_kernel"]=$image_path/kernel/Image-5.10-rv64-kcdebug@c908v
		else
			echo "Error[$LINENO]: not supported kernel with:" \
			     "abi_mode=${params_array["abi_mode"]}," \
				 "kernel_ver=${params_array["kernel_ver"]}," \
				 "kernel_cfg=${params_array["kernel_cfg"]}"
			exit 1
		fi
	else
		echo "Error[$LINENO]: not supported kernel with:" \
			 "abi_mode=${params_array["abi_mode"]}," \
			 "kernel_ver=${params_array["kernel_ver"]}," \
			 "kernel_cfg=${params_array["kernel_cfg"]}"
		exit 1
	fi
elif [[ ${params_array["abi_mode"]} == "rv32_std" ]]; then
	if [[ ${params_array["kernel_ver"]} == "6.6" ]]; then
		if [[ ${params_array["kernel_cfg"]} == "default" ]]; then
			images_array["image_kernel"]=$image_path/kernel/Image-6.6-rv32-kcdefault
		elif [[ ${params_array["kernel_cfg"]} == "debug" ]]; then
			images_array["image_kernel"]=$image_path/kernel/Image-6.6-rv32-kcdebug
		else
			echo "Error[$LINENO]: not supported kernel with:" \
			     "abi_mode=${params_array["abi_mode"]}," \
				 "kernel_ver=${params_array["kernel_ver"]}," \
				 "kernel_cfg=${params_array["kernel_cfg"]}"
			exit 1
		fi
	elif [[ ${params_array["kernel_ver"]} == "5.10" ]]; then
		if [[ ${params_array["kernel_cfg"]} == "default" ]]; then
			images_array["image_kernel"]=$image_path/kernel/Image-5.10-rv32-kcdefault
		elif [[ ${params_array["kernel_cfg"]} == "debug" ]]; then
			images_array["image_kernel"]=$image_path/kernel/Image-5.10-rv32-kcdebug
		else
			echo "Error[$LINENO]: not supported kernel with:" \
			     "abi_mode=${params_array["abi_mode"]}," \
				 "kernel_ver=${params_array["kernel_ver"]}," \
				 "kernel_cfg=${params_array["kernel_cfg"]}"
			exit 1
		fi
	else
		echo "Error[$LINENO]: not supported kernel with:" \
			 "abi_mode=${params_array["abi_mode"]}," \
			 "kernel_ver=${params_array["kernel_ver"]}," \
			 "kernel_cfg=${params_array["kernel_cfg"]}"
		exit 1
	fi
fi

# get rootfs image
if [[ ${params_array["kernel_ver"]} == "6.6" ]]; then
	if [[ ${params_array["abi_mode"]} == "rv64_std" ]]; then
		images_array["image_rootfs"]=$image_path/rootfs/rootfs.xuantie-image-rv64-6.6-lite.cpio.gz
	elif [[ ${params_array["abi_mode"]} == "rv32_std" ]]; then
		images_array["image_rootfs"]=$image_path/rootfs/rootfs.xuantie-image-rv32-6.6-lite.cpio.gz
	fi
elif [[ ${params_array["kernel_ver"]} == 5.10* ]]; then
	if [[ ${params_array["abi_mode"]} == "rv64_std" ]]; then
		images_array["image_rootfs"]=$image_path/rootfs/rootfs.xuantie-image-rv64-5.10-lite.cpio.gz
	elif [[ ${params_array["abi_mode"]} == "rv32_std" ]]; then
		images_array["image_rootfs"]=$image_path/rootfs/rootfs.xuantie-image-rv32-5.10-lite.cpio.gz
	fi
else
	echo "Error[$LINENO]: not supported kernel version"
	exit 1
fi