
SRC_URI = "git://git@gitee.com/t-head-linux/procps.git;protocol=https;branch=master \
           file://sysctl.conf \
           file://0001-w.c-correct-musl-builds.patch \
           file://0002-proc-escape.c-add-missing-include.patch \
           "
do_install:append() {
    sed -i '/^net.ipv4.tcp_syncookies=1/s/^/#/' ${D}${sysconfdir}/sysctl.conf
}
