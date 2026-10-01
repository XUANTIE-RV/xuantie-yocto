# enable for public release
# enable by xuantie-build/xuantie/linux-image-tools/scripts/yocto_process.py
# if enable XUANTIE_LINUX_TAG need set
MACHINECONFIG = "public"

# qemu-native_9.0.94 (internal dev branch) is dropped from the public
# release; pin the public fork version so qemu-native does not fall back
# to upstream 9.1.1, which has no c920v3/xt-* cpu models.
PREFERRED_VERSION_qemu-native = "8.2.94"
