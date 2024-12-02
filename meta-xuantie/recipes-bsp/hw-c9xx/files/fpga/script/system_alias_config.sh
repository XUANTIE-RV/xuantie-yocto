system_alias=()

function xuantie_rv64_std_kernel6_6_kcdefault() {
	params_array["abi_mode"]="rv64_std";
	params_array["enable_uboot"]="false";
	params_array["enable_optee"]="false";
	params_array["opensbi_ver"]="1.3";
	params_array["kernel_ver"]="6.6";
	params_array["kernel_cfg"]="default";
	params_array["rootfs_type"]="lite";
	params_array["restore_base_addr"]="0x60000000";
	params_array["system"]="";
	params_array["simulate_run"]="false";
}
system_alias+=(xuantie_rv64_std_kernel6_6_kcdefault)

function xuantie_rv64_std_kernel5_10_kcdebug() {
	params_array["abi_mode"]="rv64_std";
	params_array["enable_uboot"]="false";
	params_array["enable_optee"]="false";
	params_array["opensbi_ver"]="0.9";
	params_array["kernel_ver"]="5.10";
	params_array["kernel_cfg"]="debug";
	params_array["rootfs_type"]="lite";
	params_array["restore_base_addr"]="0x60000000";
	params_array["system"]="";
	params_array["simulate_run"]="false";
}
system_alias+=(xuantie_rv64_std_kernel5_10_kcdebug)

