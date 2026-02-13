# The recipe for init-ifupdown in core has a special-case for all
# the Qemu MACHINES: it removes all external network interfaces
# by default. However, eth0 is needed for testimage, so enable it here.

# If QB_NETWORK_XEN_BRIDGE is set, configure bridging for the network.
QB_NETWORK_XEN_BRIDGE ??= ""

enable_runqemu_network() {
    : # no-op for non-qemu MACHINES
}

enable_runqemu_network:qemuall() {
    # Do not override a network configuration for eth0 if one is present
    if ! grep -q eth0 "${IMAGE_ROOTFS}${sysconfdir}/network/interfaces" ; then

        # Xen host networking: use bridging to support guest networks
        if [ -n "${QB_NETWORK_XEN_BRIDGE}" ] ; then
            # Configure a Xen host network bridge and put eth0 on it
            cat <<EOF >>${IMAGE_ROOTFS}${sysconfdir}/network/interfaces

# Bridged host network for Xen testimage
iface eth0 inet manual

auto xenbr0
iface xenbr0 inet dhcp
	bridge_ports eth0
EOF

# Add a script to create the bridge and add eth0 if necessary
            cat <<EOF >>${IMAGE_ROOTFS}${sysconfdir}/network/if-pre-up.d/xenbr0
#!/bin/sh

if [ "\$IFACE" = xenbr0 ]; then
    brctl addbr xenbr0 || /bin/true
    brctl addif xenbr0 eth0 || /bin/true
    ifconfig eth0 up
fi
EOF
            chmod 755 ${IMAGE_ROOTFS}${sysconfdir}/network/if-pre-up.d/xenbr0
        else
            # Just configure eth0
            cat <<EOF >>${IMAGE_ROOTFS}${sysconfdir}/network/interfaces

# Network for testimage
auto eth0
iface eth0 inet dhcp
EOF
        fi
    fi
}
ROOTFS_POSTPROCESS_COMMAND += 'enable_runqemu_network;'
