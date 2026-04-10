require opensbi.inc

SRC_URI = " \
    ${@bb.utils.contains('MACHINECONFIG', 'public', \
    '${XUANTIE_GIT}/opensbi.git;branch=opensbi-v1.7-dev;protocol=https', \
    'git://github.com/XUANTIE-RV/opensbi.git;branch=opensbi-v1.7-dev;protocol=https', d)} \
    file://0001-xuantie-pmu-riscv32-Support-cycles-instructions-over.patch \
"

# lib: sbi: Enable Ssqosid Ext using mstateen0
SRCREV = "ec52ac7d13cb477d3adf70b46a306c817a5b5603"
