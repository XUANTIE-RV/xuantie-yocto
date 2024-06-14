
SRC_URI = "git://git@gitee.com/t-head-linux/systemd-stable.git;protocol=git;branch=v246-stable" 
SRC_URI += "file://touchscreen.rules \
           file://00-create-volatile.conf \
           file://init \
           file://99-default.preset \
           file://0001-binfmt-Don-t-install-dependency-links-at-install-tim.patch \
           file://0003-implment-systemd-sysv-install-for-OE.patch \
           file://0001-systemd.pc.in-use-ROOTPREFIX-without-suffixed-slash.patch \
           file://selinux-hook-handling-to-enumerate-nexthop.patch \
           "

