#!/bin/bash
set -e
declare -A default_isa

# C906
default_isa[c906]="rv64imacxtheadc"
default_isa[c906fd]="rv64imafdc_zfh_xtheadc"
default_isa[c906fdv]="rv64imafdcv0p7_zfh_xtheadc"

# C910/C920
default_isa[c910]="rv64imafdc_zfh_xtheadc"
default_isa[c920]="rv64imafdcv0p7_zfh_xtheadc"
default_isa[r910]="rv64imafdc_zfh_xtheadc"
default_isa[r920]="rv64imafdcv0p7_zfh_xtheadc"

# C908
default_isa[c908]="rv64imafdc_zicbom_zicbop_zicboz_zihintpause_zfh_zba_zbb_zbc_zbs_svinval_svnapot_svpbmt_xtheadc"
default_isa[c908i]="rv64imac_zicbom_zicbop_zicboz_zihintpause_zba_zbb_zbc_zbs_svinval_svnapot_svpbmt_xtheadc"
default_isa[c908v]="rv64imafdcv_zicbom_zicbop_zicboz_zihintpause_zfh_zba_zbb_zbc_zbs_svinval_svnapot_svpbmt_xtheadc_xtheadvdot"


# C910/C920 v2
default_isa[c910v2]="rv64imafdc_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_svinval_svnapot_svpbmt_xtheadc"
default_isa[c920v2]="rv64imafdcv_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_svinval_svpbmt_xtheadc_xtheadvdot"

# C910/C920 v3
default_isa[c910v3]="rv64imafdc_zicbom_zicbop_zicboz_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zawrs_zfa_zfbfmin_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadba_xtheadbb_xtheadbs_xtheadc_xtheadcmo_xtheadcondmov_xtheadfmemidx_xtheadfmv_xtheadmac_xtheadmemidx_xtheadmempair_xtheadsync"
default_isa[c920v3]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zawrs_zfa_zfbfmin_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_zve32f_zve32x_zve64d_zve64f_zve64x_zvfbfmin_zvfbfwma_zvfh_zvl128b_zvl32b_zvl64b_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadba_xtheadbb_xtheadbs_xtheadc_xtheadcmo_xtheadcondmov_xtheadfmemidx_xtheadfmv_xtheadmac_xtheadmemidx_xtheadmempair_xtheadsync_xtheadvdot"
default_isa[c910v3-cp]="rv64imafdc_zicbom_zicbop_zicboz_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zawrs_zfa_zfbfmin_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadcmo_xtheadsync_xxtccef_xxtccei"
default_isa[c920v3-cp]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zawrs_zfa_zfbfmin_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_zve32f_zve32x_zve64d_zve64f_zve64x_zvfbfmin_zvfbfwma_zvfh_zvl128b_zvl32b_zvl64b_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadcmo_xtheadsync_xxtccef_xxtccei_xxtccev"

# C907
default_isa[c907]="rv64imac_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zba_zbb_zbc_zbs_svinval_svnapot_svpbmt_xtheadc"
default_isa[c907fd]="rv64imafdc_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zba_zbb_zbc_zbs_svinval_svnapot_svpbmt_xtheadc"
default_isa[c907fdv]="rv64imafdcv_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpaue_zawrs_zfa_zfbfmin_zfh_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_svinval_svnapot_svpbmt_xtheadc_xtheadvdot"
default_isa[c907fdvm]="rv64imafdcv_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_svinval_svnapot_svpbmt_xtheadc_xtheadmatrix_xtheadvdot"
default_isa[c907-rv32]="rv32imac_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zba_zbb_zbc_zbs_svinval_svnapot_svpbmt_xtheadc"
default_isa[c907fd-rv32]="rv32imafdcv_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_svinval_svnapot_svpbmt_xtheadc_xtheadvdot"
default_isa[c907fdv-rv32]="rv32imafdcv_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_svinval_svnapot_svpbmt_xtheadc_xtheadvdot"
default_isa[c907fdvm-rv32]="rv32imafdcv_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_svinval_svnapot_svpbmt_xtheadc_xtheadmatrix_xtheadvdot"

# R908
default_isa[r908]="rv64imac_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zca_zcb_zcmop_zba_zbb_zbc_zbs_sstc_svinval_svnapot_svpbmt_xtheadba_xtheadbb_xtheadbs_xtheadc_xtheadcmo_xtheadcondmov_xtheadfmemidx_xtheadfmv_xtheadfpp_xtheadmac_xtheadmemidx_xtheadmempair_xtheadsync"
default_isa[r908fd]="rv64imafdc_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_sstc_svinval_svnapot_svpbmt_xtheadba_xtheadbb_xtheadbs_xtheadc_xtheadcmo_xtheadcondmov_xtheadfmemidx_xtheadfmv_xtheadfpp_xtheadmac_xtheadmemidx_xtheadmempair_xtheadsync"
default_isa[r908fdv]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_zve32f_zve32x_zve64d_zve64f_zve64x_zvfh_zvl128b_zvl32b_zvl64b_sstc_svinval_svnapot_svpbmt_xtheadba_xtheadbb_xtheadbs_xtheadc_xtheadcmo_xtheadcondmov_xtheadfmemidx_xtheadfmv_xtheadfpp_xtheadmac_xtheadmemidx_xtheadmempair_xtheadsync_xtheadvdot"
default_isa[r908-cp]="rv64imac_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zca_zcb_zcmop_zba_zbb_zbc_zbs_sstc_svinval_svnapot_svpbmt_xtheadcmo_xtheadfpp_xtheadsync_xxtccei"
default_isa[r908fd-cp]="rv64imafdc_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_sstc_svinval_svnapot_svpbmt_xtheadcmo_xtheadfpp_xtheadsync_xxtccef_xxtccei"
default_isa[r908fdv-cp]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_zve32f_zve32x_zve64d_zve64f_zve64x_zvfh_zvl128b_zvl32b_zvl64b_sstc_svinval_svnapot_svpbmt_xtheadcmo_xtheadfpp_xtheadsync_xxtccef_xxtccei_xxtccev"


if [ $# -lt 2 -o $# -gt 5 ] ; then
	echo "Usage:  update_dts_cpu_optee.sh <soc_platform>  <cpu>  [core_layout]  [isa_string]"
	echo
	echo "<soc_platform>:  Choose one from [ xiaohui | zsb_rv64 | zsb_rv32 ] etc."
	echo "<cpu>:           Choose one from [ c910 | c920v2 | c908 | c907 ] etc."
	echo "[core_layout]:   Choose one from [ 11 | 110011| 11111111 ] etc."
	echo "[isa_string]:    Choose one from [ default | your_customized_isa ] etc."
        echo "[kernel]:        Choose one from [ 5.10 | 6.6 ] etc."
	echo "[timebase-frequency]:    Input time frequency, default value is 25000000."
	echo "leave the optional args blank, as skip the updates"
	echo
	echo "default isa strings:"
	for key in "${!default_isa[@]}"; do
		echo -e "$key:\t${default_isa[$key]}"
	done
	exit 1
fi


PLATFORM=$1
CPU=${2,}
CORE_LAYOUT=$3
ISA_STRING=$4
KERNEL=${5:-"6.6"}
TIME_FREQ=${6:-25000000}
if [ $KERNEL == "5.10" ]; then
        DTS=dts/${PLATFORM}_optee_kernel-5.10.dts
else
	DTS=dts/${PLATFORM}_optee.dts
fi

# check if DTS exist
if [ ! -f $DTS ]; then
	echo "Error: dts file '${DTS}' not exist !"
	echo
	exit 1
fi

# check if CPU supported
if [[ -z ${default_isa[$CPU]+_} ]]; then
	echo "Error: The key '$CPU' does not exist in the list"
	echo "suported CPU:"
	for key in "${!default_isa[@]}"; do
		echo $key
	done
	exit 1
fi

# use default ISA_STRING
if [[ $ISA_STRING == "default" ]]; then
	ISA_STRING=${default_isa[$CPU]}
fi

echo "==== update dts CPU settings ===="
echo "PLATFORM : ${PLATFORM}"
echo "DTS  : ${DTS}"
echo "CPU  : ${CPU}"
echo "CORE_LAYOUT : ${CORE_LAYOUT}"
echo "ISA_STRING  : ${ISA_STRING}"
echo "TIME-FREQUENCY  : ${TIME_FREQ}"
echo "==== ==== ==== ==== ==== ===="
echo

function update_cpu_socinit
{
	if [[ $CPU =~ c906.* ]]; then
		echo ">> updating socinit ncore to noncore"
		sed -i "s/set \$is_use_ncore = .*/set \$is_use_ncore = 0/g" gdbinit/zsb_socinit.gdbinit
	else
		echo ">> updating socinit to ncore"
		sed -i "s/set \$is_use_ncore = .*/set \$is_use_ncore = 1/g" gdbinit/zsb_socinit.gdbinit
	fi
}

function update_dts_cpu
{
	# if $CPU is defined then update it
	if [ ! -z $CPU ]; then
		echo ">> updating cpu string to $CPU"
		sed -i "s/model = .*/model = \"Xuantie $CPU\";/g" ${DTS}
	else
		echo "WARNING: CPU not set, skip update CPU"
	fi
}

function update_dts_time_freq
{
	# if $TIME_FREQ is defined then update it
	if [ ! -z $TIME_FREQ ]; then
	echo ">> updating timebase-frequency to $TIME_FREQ"
	sed -i "s/timebase-frequency = .*/timebase-frequency = <$TIME_FREQ>;/g" ${DTS}
	else
	echo "WARNING: TIME-FREQUENCY not set, skip update timebase-frequency"
	fi
}

function update_dts_core_layout
{
	# if $CORE_LAYOUT is defined then update it
	if [ ! -z $CORE_LAYOUT ]; then
		if [ $CORE_LAYOUT -eq 1 ];then
			sed -i '/^\s*set\s*\*0x18030000 = 0x7f/s/^\(\s*\)\(set\)/\1# \2/' gdbinit/zsb.gdbinit
		else
			sed -i '/#\s*set\s*\*0x18030000 = 0x7f/s/#\s*//' gdbinit/zsb.gdbinit
		fi
		echo ">> updating core layout to $CORE_LAYOUT"
		decimal=$((2#$CORE_LAYOUT))
		length=$(grep -o "cpu@" ${DTS} | wc -l)

		for (( i=0; i<${length}; i++ )); do
			y=`awk '/cpu@'$i' \{/{getline a;print NR}' ${DTS}`
			let y=$y+2
			if ((decimal & 1 == 1)); then
				sed -i ''$y','$y's/skip/okay/g' ${DTS}
			else
				sed -i ''$y','$y's/okay/skip/g' ${DTS}
			fi
		decimal=$((decimal >> 1))
		done
	else
		echo "WARNING: CORE_LAYOUT not set, skip update CORE_LAYOUT"
	fi
}


function update_dts_isa
{
	# if $ISA_STRING is defined then update it
	if [ ! -z $ISA_STRING ]; then
		echo ">> updating isa string to $ISA_STRING"
		sed -i "s/riscv,isa = .*/riscv,isa = \"$ISA_STRING\";/g" ${DTS}
	else
		echo "WARNING: ISA_STRING not set, skip update ISA_STRING"
	fi
}

update_cpu_socinit
update_dts_cpu
update_dts_core_layout
update_dts_isa
update_dts_time_freq
