SUMMARY = "Detect if we are running in a virtual machine"
HOMEPAGE = "https://people.redhat.com/~rjones/virt-what/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://people.redhat.com/~rjones/virt-what/files/${BP}.tar.gz"
SRC_URI[sha256sum] = "12cb455334aa4cfd53ab78f27e2252e94d1f676dd093f48327ed94d8080d1f7b"

DEPENDS = "perl-native"

inherit autotools
