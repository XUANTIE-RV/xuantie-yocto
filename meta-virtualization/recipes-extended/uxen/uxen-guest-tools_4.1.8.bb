SUMMARY = "uXen type-2 Open Source hypervisor Linux guest tools"
DESCRIPTION = "Linux guest virtual machine tools for the uXen hypervisor"
HOMEPAGE = "https://www.bromium.com/opensource"
LICENSE = "GPL-2.0-only"

COMPATIBLE_HOST = '(x86_64.*).*-linux'

SRC_URI = " \
    https://www.bromium.com/wp-content/uploads/2019/11/uxen-${PV}.zip;name=uxen \
    https://www.bromium.com/wp-content/uploads/2019/11/Bromium-4.1.8-Open-Source-Software.pdf;name=license \
    file://fix-Makefile-for-OE-kernel-build.patch \
    file://0001-vm-support-fix-build-for-kernel-s-5.4.patch \
    file://0002-vm-support-fix-build-for-kernel-s-5.15.patch \
    file://0003-vm-support-fix-build-for-kernel-s-5.14.patch \
    file://0004-uxenhc-fix-DMODULE-not-working-on-module-build-comma.patch \
"

SRC_URI[uxen.sha384sum] = "be2233bc6506a23350d76c03ac28ea7ea381e1dc6ed5ce996e8ac71e6a3316fcaa2ed070c622618bd226f43a4d6db5d4"
SRC_URI[license.sha384sum] = "92e48c614df3094cb52321d4c4e01f6df5526d46aee5c6fa36c43ee23d4c33f03baa1fc5f6f29efafff636b6d13bc92c"

# The software license is GPLv2: please see page 199 of the pdf document
LIC_FILES_CHKSUM = "file://${WORKDIR}/Bromium-4.1.8-Open-Source-Software.pdf;md5=cf120df6ffa417b36f870a9997650049"

S = "${WORKDIR}/uxen-${PV}-72a4af9/vm-support/linux"

inherit module dos2unix
