#!/bin/csh

source /tools/common/haps/config_haps_env.csh
source /tools/admin/common/rv.cshrc

/tools/common/haps/xactor_mem_backdoor -sn $argv[1] -fn A -m write_from_file -f $argv[2] -startaddr 0x60400000
if ($status != 0) then
    echo "Load image $argv[2] failed"
    exit 1
endif

/tools/common/haps/xactor_mem_backdoor -sn $argv[1] -fn A -m write_from_file -f $argv[3] -startaddr 0x64000000
if ($status != 0) then
    echo "Load image $argv[3] failed"
    exit 1
endif

