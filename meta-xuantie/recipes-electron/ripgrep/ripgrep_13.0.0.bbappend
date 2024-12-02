FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:riscv32 = " file://0001-Add-riscv32gc-oe-linux-gnu-into-NO_ATOMIC_64-list.patch;patchdir=../cargo_home/bitbake/crossbeam-utils-0.8.5/ "
