DESCRIPTION = "Linux lite debug image for Xuantie"
LICENSE = "MIT"

require xuantie-image-lite.bb

IMAGE_FEATURES += "dbg-pkgs"

IMAGE_INSTALL += " dtc ethtool  libaio libmnl   stress-ng"

#  是 RVV 1.0 手写汇编，ISA 里没有 v/zve* 时汇编器直接报
# unrecognized opcode（'vsetvli' / 'vlb.v'）。按 ISA 能力摘除而不是按 DISTRO
# 名字面量，判据与取值见 xuantie-base.inc 的 XUANTIE_HAS_RVV10。
IMAGE_INSTALL:remove = "${@bb.utils.contains('XUANTIE_HAS_RVV10', 'no', '', '', d)}"

export IMAGE_BASENAME = "xuantie-image-lite-debug"
