SUMMARY = "Curl Application container image"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

IMAGE_FSTYPES = "container oci"
inherit image
inherit image-oci

IMAGE_FEATURES = ""
IMAGE_LINGUAS = ""
NO_RECOMMENDATIONS = "1"

IMAGE_INSTALL = " \
       base-files \
       base-passwd \
       netbase \
       ${CONTAINER_SHELL} \
"

# If the following is configured in local.conf (or the distro):
#      PACKAGE_EXTRA_ARCHS:append = " container-dummy-provides"
# 
# it has been explicitly # indicated that we don't want or need a shell, so we'll
# add the dummy provides.
# 
# This is required, since there are postinstall scripts in base-files and base-passwd
# that reference /bin/sh and we'll get a rootfs error if there's no shell or no dummy
# provider.
CONTAINER_SHELL ?= "${@bb.utils.contains('PACKAGE_EXTRA_ARCHS', 'container-dummy-provides', 'container-dummy-provides', 'busybox', d)}"

# Allow build with or without a specific kernel
IMAGE_CONTAINER_NO_DUMMY = "1"

# Workaround /var/volatile for now
ROOTFS_POSTPROCESS_COMMAND += "rootfs_fixup_var_volatile ; "
rootfs_fixup_var_volatile () {
    install -m 1777 -d ${IMAGE_ROOTFS}/${localstatedir}/volatile/tmp
    install -m 755 -d ${IMAGE_ROOTFS}/${localstatedir}/volatile/log
}

OCI_IMAGE_ENTRYPOINT = "curl"
OCI_IMAGE_TAG = "easy"
OCI_IMAGE_ENTRYPOINT_ARGS = "http://localhost:80"
CONTAINER_SHELL = "busybox"

IMAGE_INSTALL:append = "curl"
