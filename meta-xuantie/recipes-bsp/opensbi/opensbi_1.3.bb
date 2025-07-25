require opensbi.inc

SRC_URI = " \
    ${@bb.utils.contains('MACHINECONFIG', 'public', \
    '${XUANTIE_GIT}/opensbi.git;branch=linux-6.6;protocol=https', \
    'git://github.com/XUANTIE-RV/opensbi.git;branch=linux-6.6;protocol=https', d)} \
    file://0001-xuantie-pmu-riscv32-Support-cycles-instructions-over.patch \
"

# tag v1.3
SRCREV = "a9994daf2474e51df3c8ec83065ca556180eb203"
