require opensbi.inc

SRC_URI = " \
    ${@bb.utils.contains('MACHINECONFIG', 'public', \
    '${XUANTIE_GIT}/opensbi.git;branch=linux-6.6;protocol=https', \
    'git://github.com/XUANTIE-RV/opensbi.git;branch=linux-6.6;protocol=https', d)} \
    file://0001-xuantie-pmu-riscv32-Support-cycles-instructions-over.patch \
"

# Commits on Aug 14, 2025
# commit info: platform: Add xuantie_pmc for PMC test
SRCREV = "f94bfb85500a00051b74d68d6a02c2ed77fa9f29"
