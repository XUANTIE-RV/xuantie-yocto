# Build errors with the pie options enabled
SECURITY_CFLAGS:pn-libvirt = "${SECURITY_NO_PIE_CFLAGS}"

# This has text relco errors with the pie options enabled
SECURITY_CFLAGS:pn-lxc = "${SECURITY_NO_PIE_CFLAGS}"
