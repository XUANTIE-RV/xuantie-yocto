DESCRIPTION = "Weston Image for Light FM"
LICENSE = "MIT"

IMAGE_INSTALL += "packagegroup-core-boot \
    kernel-modules \
    weston \
    gtk+3 \
    gpgme \
    gnupg \
    gpu-bxm-4-64-gpl \
    haveged \
    util-linux \
    gfx-examples \
    tiff \
    skia \
    initscripts-readonly-rootfs-overlay \
"

inherit core-image features_check

python do_rootfs_append_riscv64 () {
    import subprocess

    srcdir=d.getVar('IMAGE_ROOTFS')

    cmd = 'rm -rf %s/boot/* ' % (srcdir)
    subprocess.check_call(cmd, shell=True)
}

REQUIRED_DISTRO_FEATURES = "wayland opengl vulkan opencl"
IMAGE_FEATURES += "dev-pkgs ssh-server-openssh"
DISTRO_FEATURES_remove = " x11"
PREFERERRED_VERSION_mesa = "21.0.1"
PREFERERRED_VERSION_llvm = "10.0.2"

#IMAGE_ROOTFS_EXTRA_SPACE = "10240"
SYSTEMD_DEFAULT_TARGET = "graphical.target"

export IMAGE_BASENAME = "light-fm-image-weston"
