SUMMARY = "iovisor/ply"
DESCRIPTION = "Dynamic Tracing in Linux"
HOMEPAGE = "https://github.com/iovisor/ply"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

#SRC_URI = "git://github.com/iovisor/ply.git;branch=master;protocol=http"
#SRCREV = "master"
SRC_URI = "git://github.com/xmzzz/ply.git;branch=Initial-support-for-riscv64;protocol=http"
SRCREV = "Initial-support-for-riscv64"

S = "${WORKDIR}/git"

inherit autotools

#COMPATIBLE_HOST:riscv32 = "null"
#COMPATIBLE_HOST:riscv64 = "null"