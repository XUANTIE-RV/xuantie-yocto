FILESEXTRAPATHS:prepend := "${THISDIR}/linux-yocto:"

SRC_URI += "file://xt-checksum.scc \
            file://ebtables.scc \
	    file://vswitch.scc \
	    file://lxc.scc \
            file://docker.scc \
	    file://cgroup-hugetlb.scc \
	    "
KERNEL_FEATURES:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'kvm', 'features/kvm/qemu-kvm-enable.scc', '', d)}"

KERNEL_MODULE_AUTOLOAD += "nf_conntrack_ipv6 openvswitch"
KERNEL_MODULE_AUTOLOAD += "${@bb.utils.contains('DISTRO_FEATURES', 'kvm', 'kvm', '', d)}"

# aufs kernel support required for xen-image-minimal
KERNEL_FEATURES:append = "${@bb.utils.contains('DISTRO_FEATURES', 'aufs', ' features/aufs/aufs-enable.scc', '', d)}"

# if the kernel-yocto meta-data routine automatically starts to add the
# recipe-sysroot-native, we can do away with this conditional, since all
# features will be found at the same relative offset from a search
# directory
def kernel_cache_cond_feature(src_uri,feature):
    import re
    kernel_cache = re.search("kernel-cache", src_uri )
    if kernel_cache:
       return feature

    return "../recipe-sysroot-native/kcfg/" + feature

#KERNEL_CACHE_FEATURES ?= "${@kernel_cache_cond_feature(d.getVar('SRC_URI'),'cfg/virtio.scc')}"
#KERNEL_FEATURES:append = " ${KERNEL_CACHE_FEATURES}"

# if kernel-yocto has been inherited (how we can check for configuration
# fragment merging suport at the moment, then add a dependency on the
# configuration fragment repository. This allows us to be sure that our
# features can be enabled via the fragments
do_kernel_metadata[depends] += "${@['', 'yocto-cfg-fragments-native:do_populate_sysroot'][(bb.data.inherits_class('kernel-yocto', d))]}"

# xen kernel support
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'xen', ' file://xen.scc', '', d)}"

# k8s and k3s kernel support
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'k8s', ' file://kubernetes.scc', '', d)}"
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'k3s', ' file://kubernetes.scc', '', d)}"
