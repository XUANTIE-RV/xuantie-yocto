#!/bin/bash
set -e

SCRIPT_DIR="$(cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
cd $SCRIPT_DIR

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
default_isa[c908]="rv64imafdc_zicbom_zicbop_zicboz_zihintpause_zfh_zba_zbb_zbc_zbs_sscofpmf_svinval_svnapot_svpbmt_xtheadc"
default_isa[c908i]="rv64imac_zicbom_zicbop_zicboz_zihintpause_zba_zbb_zbc_zbs_sscofpmf_svinval_svnapot_svpbmt_xtheadc"
default_isa[c908v]="rv64imafdcv_zicbom_zicbop_zicboz_zihintpause_zfh_zba_zbb_zbc_zbs_sscofpmf_svinval_svnapot_svpbmt_xtheadc_xtheadvdot"

# C908v2
default_isa[c908_v2]="rv64imafdc_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_sstc_sscofpmf_svinval_svnapot_svpbmt_xtheadc"
default_isa[c908-cp_v2]="rv64imafdc_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_sstc_sscofpmf_svinval_svnapot_svpbmt_xtheadcmo_xtheadsync_xtheadcf_xtheadcei"
default_isa[c908v_v2]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_zvfh_sstc_sscofpmf_svinval_svnapot_svpbmt_xtheadc_xtheadvdot"
default_isa[c908v-cp_v2]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_zvfh_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadcbi_xtheadcei_xtheadcf_xtheadcv"
default_isa[c908i_v2]="rv64imac_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zca_zcb_zba_zbb_zbc_zbs_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadc"
default_isa[c908i-cp_v2]="rv64imac_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zca_zcb_zba_zbb_zbc_zbs_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadcmo_xtheadsync_xtheadcei"
default_isa[c908v-cp-xt_v2]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_zvfh_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadc_xtheadvdot_xtheadcbi_xtheadcei_xtheadcf_xtheadcv"
default_isa[c908vk-cp_v2]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_zkt_zvbb_zvbc_zvkb_zvkg_zvkned_zvknha_zvknhb_zvksed_zvksh_zvfbfmin_zvfbfwma_zvfh_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadcbi_xtheadcei_xtheadcf_xtheadcv"
default_isa[c908vk-cp-xt_v2]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_zkt_zvbb_zvbc_zvkb_zvkg_zvkned_zvknha_zvknhb_zvksed_zvksh_zvfbfmin_zvfbfwma_zvfh_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadc_xtheadvdot_xtheadcbi_xtheadcei_xtheadcf_xtheadcv"

# C910/C920 v2
default_isa[c910v2]="rv64imafdc_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_sscofpmf_svinval_svnapot_svpbmt_xtheadc"
default_isa[c920v2]="rv64imafdcv_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_sscofpmf_svinval_svpbmt_xtheadc_xtheadvdot"

# C910/C920 v3
default_isa[c910v3]="rv64imafdc_zicbom_zicbop_zicboz_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zawrs_zfa_zfbfmin_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadba_xtheadbb_xtheadbs_xtheadc_xtheadcmo_xtheadcondmov_xtheadfmemidx_xtheadfmv_xtheadmac_xtheadmemidx_xtheadmempair_xtheadsync"
default_isa[c920v3]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zawrs_zfa_zfbfmin_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_zve32f_zve32x_zve64d_zve64f_zve64x_zvfbfmin_zvfbfwma_zvfh_zvl128b_zvl32b_zvl64b_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadba_xtheadbb_xtheadbs_xtheadc_xtheadcmo_xtheadcondmov_xtheadfmemidx_xtheadfmv_xtheadmac_xtheadmemidx_xtheadmempair_xtheadsync_xtheadvdot"
default_isa[c910v3-cp]="rv64imafdc_zicbom_zicbop_zicboz_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zawrs_zfa_zfbfmin_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadcmo_xtheadsync_xtheadcf_xtheadcei"
default_isa[c920v3-cp]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zawrs_zfa_zfbfmin_zfh_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_zvfh_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadcmo_xtheadsync_xtheadcbi_xtheadcei_xtheadcf_xtheadcv"
default_isa[c920v3-cp-xt]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zawrs_zfa_zfbfmin_zfh_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_zvfh_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadc_xtheadvdot_xtheadcbi_xtheadcei_xtheadcf_xtheadcv"

# C907
default_isa[c907]="rv64imac_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zba_zbb_zbc_zbs_sscofpmf_svinval_svnapot_svpbmt_xtheadc"
default_isa[c907fd]="rv64imafdc_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zba_zbb_zbc_zbs_sscofpmf_svinval_svnapot_svpbmt_xtheadc"
default_isa[c907fdv]="rv64imafdcv_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpaue_zawrs_zfa_zfbfmin_zfh_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_sscofpmf_svinval_svnapot_svpbmt_xtheadc_xtheadvdot"
default_isa[c907fdvm]="rv64imafdcv_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_sscofpmf_svinval_svnapot_svpbmt_xtheadc_xtheadmatrix_xtheadvdot"
default_isa[c907-rv32]="rv32imac_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zba_zbb_zbc_zbs_sscofpmf_svinval_svnapot_svpbmt_xtheadc"
default_isa[c907fd-rv32]="rv32imafdcv_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_sscofpmf_svinval_svnapot_svpbmt_xtheadc_xtheadvdot"
default_isa[c907fdv-rv32]="rv32imafdcv_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_sscofpmf_svinval_svnapot_svpbmt_xtheadc_xtheadvdot"
default_isa[c907fdvm-rv32]="rv32imafdcv_zicbom_zicbop_zicboz_zicond_zihintntl_zihintpause_zawrs_zfa_zfbfmin_zfh_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_sscofpmf_svinval_svnapot_svpbmt_xtheadc_xtheadmatrix_xtheadvdot"

# R908
default_isa[r908]="rv64imac_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zca_zcb_zcmop_zba_zbb_zbc_zbs_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadba_xtheadbb_xtheadbs_xtheadc_xtheadcmo_xtheadcondmov_xtheadfmemidx_xtheadfmv_xtheadfpp_xtheadmac_xtheadmemidx_xtheadmempair_xtheadsync"
default_isa[r908fd]="rv64imafdc_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadba_xtheadbb_xtheadbs_xtheadc_xtheadcmo_xtheadcondmov_xtheadfmemidx_xtheadfmv_xtheadfpp_xtheadmac_xtheadmemidx_xtheadmempair_xtheadsync"
default_isa[r908fdv]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_zve32f_zve32x_zve64d_zve64f_zve64x_zvfh_zvl128b_zvl32b_zvl64b_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadba_xtheadbb_xtheadbs_xtheadc_xtheadcmo_xtheadcondmov_xtheadfmemidx_xtheadfmv_xtheadfpp_xtheadmac_xtheadmemidx_xtheadmempair_xtheadsync_xtheadvdot"
default_isa[r908-cp]="rv64imac_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zca_zcb_zcmop_zba_zbb_zbc_zbs_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadcmo_xtheadfpp_xtheadsync_xtheadcei"
default_isa[r908fd-cp]="rv64imafdc_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadcmo_xtheadfpp_xtheadsync_xtheadcf_xtheadcei"
default_isa[r908fd-cp-xt]="rv64imafdc_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zca_zcb_zcd_zcmop_zfh_zba_zbb_zbc_zbs_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadc_xtheadfpp_xtheadcbi_xtheadcei_xtheadcf"
default_isa[r908fdv-cp]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zca_zcb_zcd_zcmop_zfh_zba_zbb_zbc_zbs_zvfh_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadcmo_xtheadfpp_xtheadsync_xtheadcbi_xtheadcei_xtheadcf_xtheadcv"
default_isa[r908fdv-cp-xt]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zimop_zca_zcb_zcd_zcmop_zfh_zba_zbb_zbc_zbs_zvfh_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadc_xtheadfpp_xtheadvdot_xtheadcbi_xtheadcei_xtheadcf_xtheadcv"


# C908X
default_isa[c908x]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_zvfh_zvl1024b_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadc_xtheadvdot_xtheadvsfa_xtheadvfcvt_xtheadvreduction_xtheadlpw"
default_isa[c908x-cp]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_zvfh_zvl1024b_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadvsfa_xtheadvfcvt_xtheadvreduction_xtheadlpw_xtheadcbi_xtheadcei_xtheadcf_xtheadcv"
default_isa[c908x-cp-xt]="rv64imafdcv_zicbom_zicbop_zicboz_zicntr_zicsr_zifencei_zihintpause_zihpm_zfbfmin_zfh_zca_zcb_zcd_zba_zbb_zbc_zbs_zvfbfmin_zvfbfwma_zvfh_zvl1024b_sscofpmf_sstc_svinval_svnapot_svpbmt_xtheadc_xtheadvdot_xtheadvsfa_xtheadvfcvt_xtheadvreduction_xtheadlpw_xtheadcbi_xtheadcei_xtheadcf_xtheadcv"

default_isa[xt-c930]="rv64imafdch_zic64b_zicbom_zicbop_zicboz_ziccamoa_ziccamoc_ziccif_zicclsm_ziccrse_zicfilp_zicfiss_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zmmul_za64rs_zabha_zacas_zalasr_zama16b_zawrs_zfa_zfbfmin_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_zkr_zkt_ssdtso_zvbb_zvbc_zvbc32e_zve32f_zve32x_zve64d_zve64f_zve64x_zvfbfmin_zvfbfwma_zvfh_zvfhmin_zvkb_zvkg_zvkgs_zvkn_zvknc_zvkned_zvkng_zvknha_zvknhb_zvks_zvksc_zvksed_zvksg_zvksh_zvkt_zvl128b_zvl32b_zvl64b_zvqdotq_shcounterenw_shgatpa_shtvala_shvsatpa_shvstvala_shvstvecd_smaia_smcdeleg_smcntrpmf_smcsrind_smctr_smepmp_smmpm_smmtt_smnpm_smrnmi_smstateen_ssaia_ssccfg_ssccptr_sscofpmf_sscounterenw_sscsrind_ssctr_ssdbltrp_ssnpm_sspm_ssqosid_ssstateen_ssstrict_sstc_sstvala_sstvecd_ssu64xl_supm_svadu_svbare_svinval_svnapot_svpbmt_svvptc_xtheadcbop_xtheadser"
default_isa[xt-c930-cp]="rv64imafdch_zic64b_zicbom_zicbop_zicboz_ziccamoa_ziccamoc_ziccif_zicclsm_ziccrse_zicfilp_zicfiss_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zmmul_za64rs_zabha_zacas_zalasr_zama16b_zawrs_zfa_zfbfmin_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_zkr_zkt_ssdtso_zvbb_zvbc_zvbc32e_zve32f_zve32x_zve64d_zve64f_zve64x_zvfbfmin_zvfbfwma_zvfh_zvfhmin_zvkb_zvkg_zvkgs_zvkn_zvknc_zvkned_zvkng_zvknha_zvknhb_zvks_zvksc_zvksed_zvksg_zvksh_zvkt_zvl128b_zvl32b_zvl64b_zvqdotq_shcounterenw_shgatpa_shtvala_shvsatpa_shvstvala_shvstvecd_smaia_smcdeleg_smcntrpmf_smcsrind_smctr_smepmp_smmpm_smmtt_smnpm_smrnmi_smstateen_ssaia_ssccfg_ssccptr_sscofpmf_sscounterenw_sscsrind_ssctr_ssdbltrp_ssnpm_sspm_ssqosid_ssstateen_ssstrict_sstc_sstvala_sstvecd_ssu64xl_supm_svadu_svbare_svinval_svnapot_svpbmt_svvptc_xtheadcbop_xtheadcf_xtheadcei_xtheadser"
default_isa[xt-c930v]="rv64imafdcvh_zic64b_zicbom_zicbop_zicboz_ziccamoa_ziccamoc_ziccif_zicclsm_ziccrse_zicfilp_zicfiss_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zmmul_za64rs_zabha_zacas_zalasr_zama16b_zawrs_zfa_zfbfmin_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_zkr_zkt_ssdtso_zvbb_zvbc_zvbc32e_zve32f_zve32x_zve64d_zve64f_zve64x_zvfbfmin_zvfbfwma_zvfh_zvfhmin_zvkb_zvkg_zvkgs_zvkn_zvknc_zvkned_zvkng_zvknha_zvknhb_zvks_zvksc_zvksed_zvksg_zvksh_zvkt_zvl128b_zvl32b_zvl64b_zvqdotq_shcounterenw_shgatpa_shtvala_shvsatpa_shvstvala_shvstvecd_smaia_smcdeleg_smcntrpmf_smcsrind_smctr_smepmp_smmpm_smmtt_smnpm_smrnmi_smstateen_ssaia_ssccfg_ssccptr_sscofpmf_sscounterenw_sscsrind_ssctr_ssdbltrp_ssnpm_sspm_ssqosid_ssstateen_ssstrict_sstc_sstvala_sstvecd_ssu64xl_supm_svadu_svbare_svinval_svnapot_svpbmt_svvptc_xtheadcbop_xtheadvarith_xtheadvcoder_xtheadvcrypto_xtheadvdot_xtheadser"
default_isa[xt-c930v-cp]="rv64imafdcvh_zic64b_zicbom_zicbop_zicboz_ziccamoa_ziccamoc_ziccif_zicclsm_ziccrse_zicfilp_zicfiss_zicntr_zicond_zicsr_zifencei_zihintntl_zihintpause_zihpm_zimop_zmmul_za64rs_zabha_zacas_zalasr_zama16b_zawrs_zfa_zfbfmin_zfh_zfhmin_zca_zcb_zcd_zcmop_zba_zbb_zbc_zbs_zkr_zkt_ssdtso_zvbb_zvbc_zvbc32e_zve32f_zve32x_zve64d_zve64f_zve64x_zvfbfmin_zvfbfwma_zvfh_zvfhmin_zvkb_zvkg_zvkgs_zvkn_zvknc_zvkned_zvkng_zvknha_zvknhb_zvks_zvksc_zvksed_zvksg_zvksh_zvkt_zvl128b_zvl32b_zvl64b_zvqdotq_shcounterenw_shgatpa_shtvala_shvsatpa_shvstvala_shvstvecd_smaia_smcdeleg_smcntrpmf_smcsrind_smctr_smepmp_smmpm_smmtt_smnpm_smrnmi_smstateen_ssaia_ssccfg_ssccptr_sscofpmf_sscounterenw_sscsrind_ssctr_ssdbltrp_ssnpm_sspm_ssqosid_ssstateen_ssstrict_sstc_sstvala_sstvecd_ssu64xl_supm_svadu_svbare_svinval_svnapot_svpbmt_svvptc_xtheadcbop_xtheadvarith_xtheadvcoder_xtheadvcrypto_xtheadvdot_xtheadcf_xtheadcei_xtheadcv_xtheadser"

function print_help() {
	echo "Usage:  update_dts_cpu.sh <soc_platform>  <-cpu>  [-layout 1]  [-isa default]"
	echo
	echo "<soc_platform>:    Choose one from [ xiaohui | zsb_rv64 | zsb_rv32 ] etc."
	echo "<-cpu your_cpu>:   Choose one from [ c910 | c920v2 | c908 | c907 ] etc."
	echo "[-layout 1]:       Choose one from [ 11 | 110011| 11111111 ] etc."
	echo "[-isa default]:    Choose one from [ default | your_customized_isa ] etc."
	echo "[-timer 25000000]: Input time frequency, default value is 25000000."
	echo "[-pa PA40]:        Choose one from [ PA48 | PA40 | others(do nothing) ] etc."
	echo "[-bus ncore]:      Choose one from [ none | stlk | stlk2 | ncore | others(do nothing) ] etc."
	echo "[-intc plic]:      Choose one from [ plic | aplic | others(do nothing) ] etc."
	echo "[-sv 48]:          Choose one from [ 39 | 48 | others(do nothing) ] etc."
	echo "[-gmac h100]:      Choose one from [ h100 | pro | others(do nothing) ] etc."
	echo "[-ntrace false]:   Choose one from [ false | true ] etc."
	echo "[-memsize 1G]      Input memory size [ 1G, 2G, 3G, 4G ] etc."
	echo "[-optee false]:    Choose one from [ false | true ] etc."
	echo "[-pmc false]:      Choose one from [ false | true ] etc."
	echo "leave the optional args blank, as skip the updates"
	exit 1
}

LINK_MODE=${LINK_MODE:-ncore}

# ntrace configs, get value from env
TRACE_CONTEXT=${TRACE_CONTEXT:-"true"}
TRACE_STALL=${TRACE_STALL:-"true"}
TRACE_INHIBIT_SRC=${TRACE_INHIBIT_SRC:-"false"}
TRACE_TRIGGER_ENABLE=${TRACE_TRIGGER_ENABLE:-"true"}
TRACE_TIMESTAMP_ENABLE=${TRACE_TIMESTAMP_ENABLE:-"true"}
TRACE_RUN_IN_DEBUGMODE=${TRACE_RUN_IN_DEBUGMODE:-"false"}
TRACE_SINK_WORKING_MODE=${TRACE_SINK_WORKING_MODE:-"circulating buffer"}

PA48=
PLATFORM=zsb_rv64
CPU=${2,}
CORE_LAYOUT=1
ISA_STRING=default
TIME_FREQ=25000000
INTC=plic
GMAC=h100
NTRACE=false
SV=
tmp=1
MEMORY_SIZE=
OPTEE=false
PMC=false


function update_c930
{
	if [[ $CPU =~ .*c930.* ]]; then
		echo ">> updating c930"
		GMAC=c930
		sed -i 's/ctl_enable_2core == 0/ctl_enable_2core == 2/g' gdbinit/zsb.gdbinit
	fi
}

function parse_old_args(){
	if [ $tmp -gt 5 ] ; then
		print_help
	fi
	case $tmp in
	1)
		PLATFORM=$1
	;;
	2)
		CPU=$1
		update_c930
	;;
	3)
		CORE_LAYOUT=$1
	;;
	4)
		ISA_STRING=$1
	;;
	5)
		TIME_FREQ=$1
	;;
	esac
	tmp=$((tmp + 1))
}

# getopts "hm:p:" opt
while [ "$#" -gt 0 ] ; do
	case $1 in
	-h)
		print_help
	;;
	-bus)
		LINK_MODE=$2
		echo "LINK_MODE: $LINK_MODE"
		shift
	;;
	-pa)
		PA48=$2
		echo "PA48: $PA48"
		shift
	;;
	-cpu)
		CPU=$2
		echo "CPU: $CPU"
		update_c930
		shift
	;;
	-layout)
		CORE_LAYOUT=$2
		echo "CORE_LAYOUT: $CORE_LAYOUT"
		shift
	;;
	-isa)
		ISA_STRING=$2
		echo "ISA_STRING: $ISA_STRING"
		shift
	;;
	-timer)
		TIME_FREQ=$2
		echo "TIME_FREQ: $TIME_FREQ"
		shift
	;;
	-intc)
		INTC=$2
		echo "INTC: $INTC"
		shift
	;;
	-sv)
		SV=$2
		echo "SV: $SV"
		shift
	;;
	-gmac)
		GMAC=$2
		echo "GMAC: $GMAC"
		shift
	;;
	-ntrace)
		NTRACE=$2
		echo "NTRACE: $NTRACE"
		shift
	;;
	-memsize)
		MEMORY_SIZE=$2
		echo "MEMORY_SIZE: $MEMORY_SIZE"
		shift
	;;
	-optee)
		OPTEE=$2
		echo "OPTEE: $OPTEE"
		shift
	;;
	-pmc)
		PMC=$2
		echo "PMC: $PMC"
		shift
	;;
	*)
		parse_old_args $1
	;;
	esac
	shift
done

DTS=dts/$PLATFORM.dts
TMP_DTS=.hw.dts
# check if DTS exist
if [ ! -f $DTS ]; then
	echo "Error: dts file '${DTS}' not exist !"
	echo
	exit 1
fi

# check if CPU supported
if [ -z ${CPU} ] || [[ -z ${default_isa[$CPU]+_} ]]; then
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

if [ "${OPTEE}" = "true" ] ; then
	ZSB_GDBINIT=gdbinit/zsb_rv64_optee.gdbinit
else
	ZSB_GDBINIT=gdbinit/zsb.gdbinit
fi

echo "==== update dts CPU settings ===="
echo "PLATFORM : ${PLATFORM}"
echo "DTS  : ${DTS}"
echo "CPU  : ${CPU}"
echo "CORE_LAYOUT : ${CORE_LAYOUT}"
echo "ISA_STRING  : ${ISA_STRING}"
echo "TIME-FREQUENCY  : ${TIME_FREQ}"
echo "LINK_MODE  : ${LINK_MODE}"
echo "OPTEE  : ${OPTEE}"
echo "==== ==== ==== ==== ==== ===="
echo

function update_dts_mmu
{
	if [ "${SV}" = "39" ] ; then
		echo ">> updating mmu to sv39"
		sed -i 's/ no4lvl//g' ${DTS}
		sed -i 's/ no5lvl//g' ${DTS}
		sed -i 's/norandmaps/norandmaps no4lvl/g' ${DTS}
	elif [ "${SV}" = "48" ] ; then
		echo ">> updating mmu to sv48"
		sed -i 's/ no4lvl//g' ${DTS}
		sed -i 's/ no5lvl//g' ${DTS}
		sed -i 's/norandmaps/norandmaps no5lvl/g' ${DTS}
	elif [ "${SV}" = "57" ] ; then
		echo ">> updating mmu to sv57"
		sed -i 's/ no4lvl//g' ${DTS}
		sed -i 's/ no5lvl//g' ${DTS}
	fi
}
function update_dts_intc
{
	rm -f dts/aplic.dtsi
	if [[ "$CPU" == *c930* ]]; then
		echo ">> updating c930 intc"
		ln -sr dts/aplic_c930.dtsi dts/aplic.dtsi
	else
		echo ">> updating rv64 intc"
		ln -sr dts/aplic_rv64.dtsi dts/aplic.dtsi
	fi

	rm -f dts/intc.dtsi
	if [ "${INTC}" = "plic" ] ; then
		echo ">> updating intc to plic"
		ln -sr dts/plic.dtsi dts/intc.dtsi
	elif [ "${INTC}" = "aplic" ] ; then
		echo ">> updating intc to aplic"
		ln -sr dts/aplic.dtsi dts/intc.dtsi
	elif [ "${INTC}" = "aplic2core" ] ; then
		echo ">> updating intc to aplic2core"
		ln -sr dts/aplic_2cores.dtsi dts/intc.dtsi
	fi
}
function update_dts_gmac
{
	if [ "${GMAC}" = "h100" ] ; then
		echo ">> updating gmac to h100"
		rm -f dts/gmac.dtsi
		ln -sr dts/gmac_haps.dtsi dts/gmac.dtsi
		sed -i "s/set \$net_mode = .*/set \$net_mode = 0/g" gdbinit/zsb_socinit.gdbinit
	elif [ "${GMAC}" = "c930" ] ; then
		echo ">> updating gmac to c930"
		rm -f dts/gmac.dtsi
		ln -sr dts/gmac_c930.dtsi dts/gmac.dtsi
		sed -i "s/set \$net_mode = .*/set \$net_mode = 1/g" gdbinit/zsb_socinit.gdbinit
	elif [ "${GMAC}" = "pro" ] ; then
		echo ">> updating gmac to pro"
		rm -f dts/gmac.dtsi
		ln -sr dts/gmac_pro.dtsi dts/gmac.dtsi
	fi
}

function update_dts_ntrace
{
	rm -rf dts/ntrace-memory.dtsi dts/ntrace.dtsi
	touch  dts/ntrace-memory.dtsi dts/ntrace.dtsi
	if [ "${NTRACE}" = "true" ] ; then
		ln -srf dts/ntrace-memory-xiaohui-common.dtsi dts/ntrace-memory.dtsi
		if [ "$CORE_LAYOUT" = "1" ];then
			cp -f dts/ntrace-1cluster1core-xiaohui-common.dtsi dts/ntrace.dtsi
		elif [ "$CORE_LAYOUT" = "11" ] ; then
			cp -f dts/ntrace-1cluster2core-xiaohui-common.dtsi dts/ntrace.dtsi
		elif [ "$CORE_LAYOUT" = "1111" ] ; then
			cp -f dts/ntrace-1cluster4core-xiaohui-common.dtsi dts/ntrace.dtsi
		elif [ "$CORE_LAYOUT" = "11111111" ] ; then
			cp -f dts/ntrace-1cluster8core-xiaohui-common.dtsi dts/ntrace.dtsi
		fi

		ntrace_file=dts/ntrace.dtsi
		sed -i "s|trace_encoder_context = .*|trace_encoder_context = \"${TRACE_CONTEXT}\";|g" "${ntrace_file}" && \
		sed -i "s|trace_encoder_inst_stall_enable = .*|trace_encoder_inst_stall_enable = \"${TRACE_STALL}\";|g" "${ntrace_file}" && \
		sed -i "s|trace_encoder_inhibit_src = .*|trace_encoder_inhibit_src = \"${TRACE_INHIBIT_SRC}\";|g" "${ntrace_file}" && \
		sed -i "s|trace_encoder_inst_trigger_enable = .*|trace_encoder_inst_trigger_enable = \"${TRACE_TRIGGER_ENABLE}\";|g" "${ntrace_file}" && \
		sed -i "s|trace_timestamp_enable = .*|trace_timestamp_enable = \"${TRACE_TIMESTAMP_ENABLE}\";|g" "${ntrace_file}" && \
		sed -i "s|trace_timestamp_runindebugmode = .*|trace_timestamp_runindebugmode = \"${TRACE_RUN_IN_DEBUGMODE}\";|g" "${ntrace_file}" && \
		sed -i "s|working_mode = .*|working_mode = \"${TRACE_SINK_WORKING_MODE}\";|g" "${ntrace_file}"
	fi
}

function update_mem_addr
{
	if [ "${OPTEE}" = "true" ] ; then
		sed -i "s/linux,initrd-start = <0x00 .*/linux,initrd-start = <0x00 0x84000000>;/g" ${DTS}
	else
		sed -i "s/linux,initrd-start = <0x00 .*/linux,initrd-start = <0x00 0x64000000>;/g" ${DTS}
	fi
	if [ "x${PA48}" = "xPA48" ] ; then
		echo ">> updating run.sh to adapt PA48"
		sed -i "s/<0x00/<0xfffe/g" run.sh
		echo ">> updating gdbinit to adapt PA48"
		sed -i "s/set \$opensbi_addr = .*/set \$opensbi_addr = 0xfffe00000000/g" ${ZSB_GDBINIT}
		echo ">> updating dts to adapt PA48"
		y=`awk '/memory@*/{getline a;print NR}' ${DTS}`
		let y=$y+1
		sed -i ''$y','$y's/reg = .*/reg = <0xfffe 0x00000000 0x1 0x00000000>;/g' ${DTS}
		sed -i "s/linux,initrd-start = <0x00 .*/linux,initrd-start = <0xfffe 0x04000000>;/g" ${DTS}
		sed -i "s/linux,initrd-end = <0x00 .*/linux,initrd-end = <0xfffe 0x0>;/g" ${DTS}
	elif [ "x${PA48}" = "xPA40" ] ; then
		echo ">> updating run.sh to adapt PA40"
		sed -i "s/<0xfffe/<0x00/g" run.sh
		echo ">> updating gdbinit to adapt PA40"
		sed -i "s/set \$opensbi_addr = .*/set \$opensbi_addr = 0x60000000/g" ${ZSB_GDBINIT}
		echo ">> updating dts to adapt PA40"
		y=`awk '/memory@*/{getline a;print NR}' ${DTS}`
		let y=$y+1
		sed -i ''$y','$y's/reg = .*/reg = <0x0 0x60000000 0x1 0x00000000>;/g' ${DTS}
		sed -i "s/linux,initrd-start = .*/linux,initrd-start = <0x00 0x64000000>;/g" ${DTS}
		sed -i "s/linux,initrd-end = .*/linux,initrd-end = <0x00 0x0>;/g" ${DTS}
	fi
	if [ "${PLATFORM}" = "zsb_ilp32" ] ; then
		y=`awk '/memory@*/{getline a;print NR}' ${DTS}`
		let y=$y+1
		sed -i ''$y','$y's/reg = .*/reg = <0x0 0x60000000 0x0 0x40000000>;/g' ${DTS}
	fi
}

function update_cpu_socinit
{
	if [[ $CPU =~ c906.* ]]; then
		echo ">> updating socinit ncore to noncore"
		sed -i "s/set \$link_mode = .*/set \$link_mode = 0/g" gdbinit/zsb_socinit.gdbinit
	elif [ "$LINK_MODE" = "ncore" ]; then
		echo ">> updating socinit to ncore"
		sed -i "s/set \$link_mode = .*/set \$link_mode = 1/g" gdbinit/zsb_socinit.gdbinit
	elif [ "$LINK_MODE" = "stlk" ]; then
		echo ">> updating socinit to stlk"
		sed -i "s/set \$link_mode = .*/set \$link_mode = 2/g" gdbinit/zsb_socinit.gdbinit
	elif [ "$LINK_MODE" = "stlk2" ]; then
		echo ">> updating socinit to stlk2"
		sed -i "s/set \$link_mode = .*/set \$link_mode = 2/g" gdbinit/zsb_socinit.gdbinit
		sed -i "s/set \$stl3_base = .*/set \$stl3_base = 0xfffffffff000/g" gdbinit/zsb_socinit.gdbinit
	elif [ "$LINK_MODE" = "none" ]; then
		echo ">> updating socinit to stlk"
		sed -i "s/set \$link_mode = .*/set \$link_mode = 0/g" gdbinit/zsb_socinit.gdbinit
	fi
}

function update_gdbinit_cp
{
	echo ">> clear gdbinit cp xt flag"
	sed -i "s/set \*(unsigned int \*)\$flag_addr = 0x0 .*/set \*(unsigned int \*)\$flag_addr = 0x0/g" ${ZSB_GDBINIT}
	sed -i "s/set \*(unsigned int \*)\$flag_addr = 0x1 .*/set \*(unsigned int \*)\$flag_addr = 0x1/g" ${ZSB_GDBINIT}

	if [[ $CPU =~ -cp-xt ]]; then
		echo ">> updating gdbinit flag & 0x2 | 0x4"
		sed -i "s/set \*(unsigned int \*)\$flag_addr = 0x0/set \*(unsigned int \*)\$flag_addr = 0x0 \| 0x2 \| 0x4/g" ${ZSB_GDBINIT}
		sed -i "s/set \*(unsigned int \*)\$flag_addr = 0x1/set \*(unsigned int \*)\$flag_addr = 0x1 \| 0x2 \| 0x4/g" ${ZSB_GDBINIT}
	elif [[ $CPU =~ -cp ]]; then
		echo ">> updating gdbinit flag & 0x2"
		sed -i "s/set \*(unsigned int \*)\$flag_addr = 0x0/set \*(unsigned int \*)\$flag_addr = 0x0 \| 0x2/g" ${ZSB_GDBINIT}
		sed -i "s/set \*(unsigned int \*)\$flag_addr = 0x1/set \*(unsigned int \*)\$flag_addr = 0x1 \| 0x2/g" ${ZSB_GDBINIT}
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

function update_dts_support_32cores
{
	if [ ${#CORE_LAYOUT} -gt 8 ];then
		sed -i 's|// /include/ "32cores.dtsi"|/include/ "32cores.dtsi"|g' ${DTS}
	else
		sed -i 's|^/include/ "32cores.dtsi"|// /include/ "32cores.dtsi"|g' ${DTS}
	fi
}

function update_dts_core_layout
{
	# if $CORE_LAYOUT is defined then update it
	if [ ! -z $CORE_LAYOUT ]; then
		if [ $CORE_LAYOUT -eq 1 ];then
			sed -i '/^\s*set\s*\*0x18030000 = .*/s/^\(\s*\)\(set\)/\1# \2/' ${ZSB_GDBINIT}
		elif [ ${#CORE_LAYOUT} -gt 8 ];then
			sed -i "s/.*set \*0x18030000 = .*/	set \*0x18030000 = 0xffffffff/g" ${ZSB_GDBINIT}
		else
			sed -i "s/.*set \*0x18030000 = .*/	set \*0x18030000 = 0x7f/g" ${ZSB_GDBINIT}
		fi
		echo ">> updating core layout to $CORE_LAYOUT"
		decimal=$((2#$CORE_LAYOUT))
		length=$(grep -o "cpu@" ${TMP_DTS} | wc -l)

		for (( i=0; i<${length}; i++ )); do
			y=`awk '/cpu@'$i' \{/{getline a;print NR}' ${TMP_DTS}`
			let y=$y+2
			if ((decimal & 1 == 1)); then
				sed -i ''$y','$y's/skip/okay/g' ${TMP_DTS}
			else
				sed -i ''$y','$y's/okay/skip/g' ${TMP_DTS}
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
		sed -i "s/riscv,isa = .*/riscv,isa = \"$ISA_STRING\";/g" ${TMP_DTS}
	else
		echo "WARNING: ISA_STRING not set, skip update ISA_STRING"
	fi
}

function update_dts_memory_size() {
	if [[ -z "$MEMORY_SIZE" ]]; then
		return
	fi

	# Extract numerical value and unit
	NUM=$(echo "$MEMORY_SIZE" | sed 's/G//')

	# Check if input is a valid number followed by G
	if ! [[ "$NUM" =~ ^[0-9]+$ ]]; then
		echo "Invalid input: $MEMORY_SIZE must be a number followed by G"
		exit 1
	fi

	# Calculate memory size in bytes (1G = 1024^3 bytes)
	BYTES=$((NUM * 1024 * 1024 * 1024))

	# Convert to high and low 32-bit values
	HIGH=$((BYTES >> 32))
	LOW=$((BYTES & 0xFFFFFFFF))

	# Format as hexadecimal with leading zeros
	HEX_HIGH=$(printf "0x%X" $HIGH)
	HEX_LOW=$(printf "0x%X" $LOW)

	# Use sed to update the 'reg' field in the DTS file
	sed -i "s/\(reg = <0x[[:xdigit:]]\+[[:space:]]\+0x60000000[[:space:]]\+\).*/\1${HEX_HIGH} ${HEX_LOW}>;/g" ${TMP_DTS}

	echo ">> updating memory size to ${MEMORY_SIZE}"
}

function update_dts_pmc() {
	if [ "${PMC}" = "true" ] ; then
		sed -i 's/status = "[^"]*"/status = "okay"/g' dts/pmc.dtsi
		sed -i "s/set \$pmc_init = .*/set \$pmc_init = 1/g" gdbinit/zsb_socinit.gdbinit
	else
		sed -i 's/status = "[^"]*"/status = "skip"/g' dts/pmc.dtsi
		sed -i "s/set \$pmc_init = .*/set \$pmc_init = 0/g" gdbinit/zsb_socinit.gdbinit
	fi
}
function update_dts_pmu
{
	rm -f dts/pmu.dtsi
	if [[ "$CPU" == *c930* ]]; then
		echo ">> updating c930 pmu"
		ln -sr dts/pmu_c930.dtsi dts/pmu.dtsi
	else
		echo ">> updating rv64 pmu"
		ln -sr dts/pmu_rv64.dtsi dts/pmu.dtsi
	fi
}

# update origin dts file
update_cpu_socinit
update_gdbinit_cp
update_dts_time_freq
update_mem_addr
update_dts_cpu
update_dts_intc
update_dts_gmac
update_dts_ntrace
update_dts_mmu
update_dts_support_32cores
update_dts_pmc
update_dts_pmu

# update $TMP_DTS file
./dtc -I dts -O dts $DTS > $TMP_DTS
update_dts_core_layout
update_dts_isa
update_dts_memory_size
