#
# This image class creates an oci image spec directory from a generated
# rootfs. The contents of the rootfs do not matter (i.e. they need not be
# container optimized), but by using the container image type and small
# footprint images, we can create directly executable container images.
#
# Once the tarball (or oci image directory) has been created of the OCI
# image, it can be manipulated by standard tools. For example, to create a
# runtime bundle from the oci image, the following can be done:
#
# Assuming the image name is "container-base":
#
#   If the oci image was a tarball, extract it (skip, if a directory is being directly used)
#     % tar xvf container-base-<arch>-<stamp>.rootfs-oci-latest-x86_64-linux.oci-image.tar
#
#   And then create the bundle:
#     % oci-image-tool create --ref name=latest container-base-<arch>-<stamp>.rootfs-oci container-base-oci-bundle
#
#   Alternatively, the bundle can be created with umoci (use --rootless if sudo is not available)
#     % sudo umoci unpack --image container-base-<arch>-<stamp>.rootfs-oci:latest container-base-oci-bundle
#
#   Or to copy (push) the oci image to a docker registry, skopeo can be used (vary the
#   tag based on the created oci image:
#
#     % skopeo copy --dest-creds <username>:<password> oci:container-base-<arch>-<stamp>:latest docker://zeddii/container-base
#
#   If your build host architecture matches the target, you can execute the unbundled
#   container with runc:
#
#     % sudo runc run -b container-base-oci-bundle ctr-build
# / % uname -a
# Linux mrsdalloway 4.18.0-25-generic #26-Ubuntu SMP Mon Jun 24 09:32:08 UTC 2019 x86_64 GNU/Linux
#

# We'd probably get this through the container image typdep, but just
# to be sure, we'll repeat it here.
ROOTFS_BOOTSTRAP_INSTALL = ""
# we want container and tar.bz2's to be created
IMAGE_TYPEDEP:oci = "container tar.bz2"

# sloci is the script/project that will create the oci image
# OCI_IMAGE_BACKEND ?= "sloci-image"
OCI_IMAGE_BACKEND ?= "umoci"
do_image_oci[depends] += "${OCI_IMAGE_BACKEND}-native:do_populate_sysroot"

#
# image type configuration block
#
OCI_IMAGE_AUTHOR ?= "${PATCH_GIT_USER_NAME}"
OCI_IMAGE_AUTHOR_EMAIL ?= "${PATCH_GIT_USER_EMAIL}"

OCI_IMAGE_TAG ?= "latest"
OCI_IMAGE_RUNTIME_UID ?= ""

OCI_IMAGE_ARCH ?= "${TARGET_ARCH}"
OCI_IMAGE_SUBARCH ?= "${@oci_map_subarch(d.getVar('TARGET_ARCH'), d.getVar('TUNE_FEATURES'), d)}"

OCI_IMAGE_ENTRYPOINT ?= "sh"
OCI_IMAGE_ENTRYPOINT_ARGS ?= ""
OCI_IMAGE_WORKINGDIR ?= ""
OCI_IMAGE_STOPSIGNAL ?= ""

# List of ports to expose from a container running this image:
#  PORT[/PROT]  
#     format: <port>/tcp, <port>/udp, or <port> (same as <port>/tcp).
OCI_IMAGE_PORTS ?= ""

# key=value list of labels
OCI_IMAGE_LABELS ?= ""
# key=value list of environment variables
OCI_IMAGE_ENV_VARS ?= ""

# whether the oci image dir should be left as a directory, or
# bundled into a tarball.
OCI_IMAGE_TAR_OUTPUT ?= "true"

# Generate a subarch that is appropriate to OCI image
# types. This is typically only ARM architectures at the
# moment.
def oci_map_subarch(a, f, d):
    import re
    if re.match('arm.*', a):
        if 'armv7' in f:
            return 'v7'
        elif 'armv6' in f:
            return 'v6'
        elif 'armv5' in f:
            return 'v5'
            return ''
    return ''

# the IMAGE_CMD:oci comes from the .inc
OCI_IMAGE_BACKEND_INC ?= "${@"image-oci-" + "${OCI_IMAGE_BACKEND}" + ".inc"}"
include ${OCI_IMAGE_BACKEND_INC}

