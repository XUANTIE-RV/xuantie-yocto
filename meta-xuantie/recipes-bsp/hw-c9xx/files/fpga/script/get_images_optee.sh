image_path="../images"

# get zsb image
if [[ ${params_array["abi_mode"]} == "rv64_std" ]]; then
        images_array["image_zsb"]=$image_path/zsb/zero_stage_boot-rv64.bin
fi


# get kernel image
if [[ ${params_array["abi_mode"]} == "rv64_std" ]]; then
	if [[ ${params_array["kernel_ver"]} == "6.6" ]]; then
		images_array["bl31_path"]=$image_path/optee/kernel6.6-rv64-fpga/bringup/bl31.bin
		images_array["teeos_path"]=$image_path/optee/kernel6.6-rv64-fpga/bringup/tee-pager_v2.bin
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
		images_array["bl31_path"]=$image_path/optee/kernel5.10-rv64-fpga/bringup/bl31.bin
		images_array["teeos_path"]=$image_path/optee/kernel5.10-rv64-fpga/bringup/tee-pager_v2.bin
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
        elif [[ ${params_array["kernel_ver"]} == "5.10@c908v" ]]; then
                images_array["bl31_path"]=$image_path/optee/kernel5.10@c908v-rv64-fpga/bringup/bl31.bin
                images_array["teeos_path"]=$image_path/optee/kernel5.10@c908v-rv64-fpga/bringup/tee-pager_v2.bin
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
        fi
fi

# get rootfs image
if [[ ${params_array["abi_mode"]} == "rv64_std" ]]; then
	if [[ ${params_array["kernel_ver"]} == "6.6" ]]; then
		images_array["image_rootfs"]=$image_path/rootfs/rootfs.xuantie-image-rv64-6.6-lite.cpio.gz
	elif [[ ${params_array["kernel_ver"]} == "5.10" || ${params_array["kernel_ver"]} == "5.10@c908v" ]]; then
		images_array["image_rootfs"]=$image_path/rootfs/rootfs.xuantie-image-rv64-5.10-lite.cpio.gz
	fi
fi
