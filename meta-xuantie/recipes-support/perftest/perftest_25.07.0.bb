SUMMARY = "perftest"
DESCRIPTION = "This is a collection of tests written over uverbs \
intended for use as a performance micro-benchmark. The tests may \
be used for HW or SW tuning as well as for functional testing."

LICENSE = "CLOSED"

inherit autotools

DEPENDS += "rdma-core pciutils"

SRC_URI = "git://github.com/linux-rdma/perftest.git;branch=master;protocol=https"

SRCREV = "4bee61f80d9e268fc97eaf40be00409e91d3a19e"
S = "${WORKDIR}/git"
