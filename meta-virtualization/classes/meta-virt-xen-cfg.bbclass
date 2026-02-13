# We need to load the Xen meta-virt config components, only if "xen"
# is in the distro features.  Since we don't know the distro flags during
# layer.conf load time, we delay using a special bbclass that simply includes
# the META_VIRT_XEN_CONFIG_PATH file.

include ${@bb.utils.contains('DISTRO_FEATURES', 'xen', '${META_VIRT_XEN_CONFIG_PATH}', '', d)}
