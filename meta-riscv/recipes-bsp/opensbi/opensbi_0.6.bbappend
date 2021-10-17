SUMMARY = "RISC-V Open Source Supervisor Binary Interface (OpenSBI)"
DESCRIPTION = "OpenSBI aims to provide an open-source and extensible implementation of the RISC-V SBI specification for a platform specific firmware (M-mode) and a general purpose OS, hypervisor or bootloader (S-mode or HS-mode). OpenSBI implementation can be easily extended by RISC-V platform or System-on-Chip vendors to fit a particular hadware configuration."

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING.BSD;md5=42dd9555eb177f35150cf9aa240b61e5"

SRCREV="${AUTOREV}"
SRC_URI = "git://git@gitlab.alibaba-inc.com/thead-linux/d1_opensbi.git;protocol=ssh \
           file://0001-Makefile-Fix-the-compile-error-for-yocto.patch \
           "

COMPATIBLE_MACHINE = "(^d1*)"

EXTRA_OEMAKE += "PLATFORM=thead/c910 SUNXI_CHIP=sun20iw1p1 PLATFORM_RISCV_ISA=rv64gcxthead"
