# Set defaults for booting Xen images with qemuboot

# Xen and Dom0 command line options
QB_XEN_CMDLINE_EXTRA ??= "dom0_mem=192M"
QB_XEN_DOM0_BOOTARGS ??= \
    "console=hvc0 earlyprintk clk_ignore_unused root=/dev/vda"

# Launch with one initial domain, dom0, with one boot module, the kernel
DOM0_KERNEL ??= "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}"
DOM0_KERNEL_LOAD_ADDR ??= "0x45000000"
QB_XEN_DOMAIN_MODULES ??= "${DOM0_KERNEL}:${DOM0_KERNEL_LOAD_ADDR}:multiboot,kernel"

# Qemuboot for Arm uses the QB_DEFAULT_KERNEL method to load Xen
# and the device loader option for the dom0 kernel:
QB_OPT_APPEND:append:aarch64 = " \
    -device loader,file=${DOM0_KERNEL},addr=${DOM0_KERNEL_LOAD_ADDR} \
    "
QB_OPT_APPEND:append:qemuarm = " \
    -device loader,file=${DOM0_KERNEL},addr=${DOM0_KERNEL_LOAD_ADDR} \
    "
QB_DEFAULT_KERNEL:qemuarm64 = "xen-${MACHINE}"
QB_DEFAULT_KERNEL:qemuarm = "xen-${MACHINE}"

# 64-bit Arm: gic version 3
QB_MACHINE:qemuarm64 = "-machine virt,gic-version=3 -machine virtualization=true"
# 32-bit Arm: highmem=off
# Disable highmem so that qemu does not use highmem IO regions that end up
# being placed at the 256GiB mark (e.g. ECAM space) and can cause issues in Xen.
QB_MACHINE:qemuarm = "-machine virt,highmem=off -machine virtualization=true"

# Increase the default qemu memory allocation to allow for the hypervisor.
# Use a weak assignment to allow for change of default and override elsewhere.
QB_MEM_VALUE ??= "512"
QB_MEM = "-m ${QB_MEM_VALUE}"

# 64-bit Arm: qemuboot with a device tree binary
QB_DTB:qemuarm64 = "${IMAGE_NAME}.qemuboot.dtb"
QB_DTB_LINK:qemuarm64 = "${IMAGE_LINK_NAME}.qemuboot.dtb"

# 32-bit Arm: qemuboot with a device tree binary
QB_DTB:qemuarm = "${IMAGE_NAME}.qemuboot.dtb"
QB_DTB_LINK:qemuarm = "${IMAGE_LINK_NAME}.qemuboot.dtb"
