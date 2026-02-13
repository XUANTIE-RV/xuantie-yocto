
SRC_URI = " \
    git://git@gitee.com/t-head-linux/glog.git;nobranch=1;protocol=https \
    file://libexecinfo.patch \
"

# build static for CIPU requirment
PACKAGECONFIG:remove:riscv64 = "shared"
