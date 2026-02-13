LICENSE = "GPL-3.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=62e1e33aebac5b1bc9fc48a866e2f61b \
                    file://oathtool/COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://liboath/COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://pam_oath/COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://download.savannah.nongnu.org/releases/oath-toolkit/oath-toolkit-${PV}.tar.gz \
           file://0001-oath-fix-macro-definition-error.patch \
"

S = "${WORKDIR}/${BPN}-toolkit-${PV}"
SRC_URI[md5sum] = "4a05cd4768764843bd5493609a6bdb17"
SRC_URI[sha256sum] = "b03446fa4b549af5ebe4d35d7aba51163442d255660558cd861ebce536824aa0"

inherit autotools pkgconfig

# Specify any options you want to pass to the configure script using EXTRA_OECONF:
EXTRA_OECONF = ""
DEPENDS = "gtk-doc-native"
