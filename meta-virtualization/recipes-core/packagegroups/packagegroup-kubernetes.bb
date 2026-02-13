SUMMARY = "packagegroups for various kubernets (and variants) roles"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

# k3s has this restriction
inherit features_check
REQUIRED_DISTRO_FEATURES ?= "seccomp"

PACKAGES = "\
    packagegroup-kubernetes-base \
    packagegroup-k8s-host \
    packagegroup-k8s-node \
    packagegroup-k3s-host \
    packagegroup-k3s-node \
    "

KUBERNETES_CRI ?= "containerd"

RDEPENDS:packagegroup-kubernetes-base = " \
    packagegroup-${KUBERNETES_CRI} \
    packagegroup-oci \
"

RDEPENDS:packagegroup-k8s-host = " \
    packagegroup-kubernetes-base \
    kubernetes \
"

RDEPENDS:packagegroup-k8s-node = " \
    packagegroup-kubernetes-base \
    kubeadm \
    kubernetes-misc \
"

RDEPENDS:packagegroup-k3s-host = " \
    packagegroup-kubernetes-base \
    k3s-server \
    k3s-cni \
"

RDEPENDS:packagegroup-k3s-node = " \
    packagegroup-kubernetes-base \
    k3s-agent \
    k3s-cni \
"
