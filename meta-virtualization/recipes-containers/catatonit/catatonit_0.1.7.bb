# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: MIT

SUMMARY = "A container init that is so simple it's effectively brain-dead."
HOMEPAGE = "https://github.com/openSUSE/catatonit"
DESCRIPTION = "${SUMMARY}"
SECTION = "base"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "git://github.com/openSUSE/${BPN};protocol=https;branch=main"
SRCREV = "d8d72fea155c144ed3bf298a35a1aba5625a5656"
S = "${WORKDIR}/git"

inherit autotools
