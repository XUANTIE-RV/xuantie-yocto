require opensbi.inc

SRC_URI = " \
            git://github.com/riscv-software-src/opensbi.git;branch=master;protocol=https \
            file://0001-platform-generic-defconfig-remove-CONFIG_SERIAL_SEMI.patch \
            file://0002-FW_TEXT_START-6.patch \
            file://0003-sbi_hart-enable-PBMT-for-rv32.patch \
          "

# tag v1.4
SRCREV = "a2b255b88918715173942f2c5e1f97ac9e90c877"
