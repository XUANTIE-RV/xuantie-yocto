SUMMARY = "All packages for container host"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

PACKAGES = "\
    packagegroup-container \
    packagegroup-lxc \
    packagegroup-docker \
    packagegroup-oci \
    ${@bb.utils.contains('DISTRO_FEATURES', 'seccomp', \
                         'packagegroup-podman', '', d)} \
    packagegroup-containerd \
    "

# everything and the kitchen sink, used for building/installing
# many things at once.
RDEPENDS:${PN} = " \
    packagegroup-lxc \
    packagegroup-docker \
    packagegroup-oci \
"

RDEPENDS:packagegroup-lxc = " \
    lxc \
"

RDEPENDS:packagegroup-docker = " \
    docker \
"

RDEPENDS:packagegroup-podman = " \
    podman \
"

RDEPENDS:packagegroup-oci = " \
    virtual-runc \
    oci-systemd-hook \
    oci-runtime-tools \
    oci-image-tools \
"

RDEPENDS:packagegroup-containerd = " \
    virtual-containerd \
"

