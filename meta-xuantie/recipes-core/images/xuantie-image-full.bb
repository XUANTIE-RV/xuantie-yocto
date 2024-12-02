DESCRIPTION = "Linux full image for Xuantie"
LICENSE = "MIT"

require xuantie-image-lite.bb
require ../packagegroups/xuantie-domain-libs.bb
require ../packagegroups/xuantie-domain-components.bb
require ../packagegroups/xuantie-buildroot-lite.bb

IMAGE_INSTALL += " xuantie-debug-tools xuantie-benchmark"
#64 bit testing build tools
IMAGE_INSTALL += " file coreutils sed xz zlib zlib-dev wget tar ripgrep perl m4 lz4 lz4-dev grep "
IMAGE_INSTALL += " autoconf automake bc bcc gcc binutils flex ncurses ncurses-dev bison nasm yasm python3 mpfr ninja cmake dragonwell "

python do_rootfs:append:riscv64 () {
    import subprocess

    srcdir=d.getVar('IMAGE_ROOTFS')

    cmd = 'sed -i "s|# session=/usr/bin/startlxde|session=/usr/bin/startxfce4|g" %s/etc/lxdm/lxdm.conf || true' % (srcdir)
    subprocess.check_call(cmd, shell=True)
}

inherit features_check
REQUIRED_DISTRO_FEATURES = "x11"

inherit core-image

SYSTEMD_DEFAULT_TARGET = "graphical.target"

export IMAGE_BASENAME = "xuantie-image-full"

