# Enable booting Xen with qemuboot / runqemu: generate device tree
#
# Copyright (c) 2021 Star Lab Corp. All rights reserved.
#
# Author: Christopher Clark <christopher.clark@starlab.io>

# Interface variables:
#
# QB_DTB : defined in qemuboot.bbclass.
# If set, this class will generate the specified device tree file.
#
# QB_XEN_CMDLINE_EXTRA :
# A string to be appended to the default Xen hypervisor boot command line,
# for supplying Xen boot options.
# The device tree that this bbclass generates will contain Xen command
# line options to connect the Xen console to the Qemu serial port.
#
# QB_XEN_DOMAIN_MODULES :
# A space-separated list of colon-separated entries:
#   "<file for the module>:<load memory address>:<module compatibility string>"
#
# QB_XEN_DOM0_BOOTARGS :
# A string for specifying Dom0 boot options for the Xen section of the device
# tree.
#
# QB_XEN_DTB_TASK_DEPENDS :
# The task dependencies for the DTB generation. A default is provided.
#
# See also: Other QB_ variables as defined by the qemuboot.bbclass.

write_lops_xen_section() {
    DOM0_BOOTARGS="$2"
    cat <<EOF >"$1"
/dts-v1/;
/ {
    compatible = "system-device-tree-v1";
    lops {
        /* Connect the Xen console to Qemu dtuart */
        lop_1 {
            compatible = "system-device-tree-v1,lop,code-v1";
            code = "
                chosen = node.tree['/chosen']
                stdout_path = str(chosen['stdout-path'].value[0])
                chosen['xen,xen-bootargs'] = \
                        'console=dtuart dtuart=%s' % stdout_path
                return True
            ";
        };
        lop_2 {
            compatible = "system-device-tree-v1,lop,modify";
            modify = "/chosen:xen,dom0-bootargs:${DOM0_BOOTARGS}";
        };
        lop_3 {
            compatible = "system-device-tree-v1,lop,modify";
            modify = "/chosen:#address-cells:<1>";
        };
        lop_4 {
            compatible = "system-device-tree-v1,lop,modify";
            modify = "/chosen:#size-cells:<1>";
        };
     };
};
EOF
}

write_lop_add_to_xen_cmdline() {
    EXTRA_XEN_BOOTARGS="$2"
    cat <<EOF >"$1"
/dts-v1/;
/ {
    compatible = "system-device-tree-v1";
    lops {
        lop_1 {
            compatible = "system-device-tree-v1,lop,code-v1";
            options = "extra_xen_bootargs:${EXTRA_XEN_BOOTARGS}";
            code = "
                chosen = node.tree['/chosen']
                xen_bootargs = str(chosen['xen,xen-bootargs'].value)
                chosen['xen,xen-bootargs'] = '%s %s' % \
                     (xen_bootargs, extra_xen_bootargs)
                return True
            ";
        };
     };
};
EOF
}

write_lop_add_chosen_module() {
    ADDR="$2"
    SIZE="$3"
    MODULE_TYPE="$4"
    cat <<EOF >"$1"
/dts-v1/;
/ {
    compatible = "system-device-tree-v1";
    lops {
        lop_1 {
            compatible = "system-device-tree-v1,lop,add";
            node_src = "module@${ADDR}";
            node_dest = "/chosen/module@${ADDR}";

            module@${ADDR} {
                compatible = "multiboot,module", "${MODULE_TYPE}";
                reg = <${ADDR} ${SIZE}>;
            };
        };
     };
};
EOF
}

generate_xen_qemuboot_dtb() {
    # First: invoke qemu to generate an initial device tree.
    # Parameters supplied here inspired by inspection of:
    #   runqemu "${IMAGE_BASENAME}" nographic slirp \
    #            qemuparams='-dtb "" -machine dumpdtb=${B}/qemu-dumped.dtb'
    ${QB_SYSTEM_NAME} \
        -device qemu-xhci \
        -device usb-tablet \
        -device usb-kbd \
        ${QB_MACHINE} \
        ${QB_CPU} \
        ${QB_SMP} \
        ${QB_MEM} \
        -nographic \
        -serial mon:stdio \
        -machine "dumpdtb=${B}/qemu-dumped.dtb"

    # Lopper generates temporary files in cwd, so run it within ${B}
    cd "${B}"
    write_lops_xen_section "${B}/lop-insert-xen-section.dts" \
        "${QB_XEN_DOM0_BOOTARGS}"

    write_lop_add_to_xen_cmdline "${B}/lop-xen-cmdline.dts" \
        "${QB_XEN_CMDLINE_EXTRA}"

    if [ -z "${QB_XEN_DOMAIN_MODULES}" ]; then
        bbwarn "No domain modules: please set QB_XEN_DOMAIN_MODULES"
    fi

    for DOMAIN_MODULE in ${QB_XEN_DOMAIN_MODULES}
    do
        MODULE_FILE="$(echo ${DOMAIN_MODULE} | cut -f1 -d:)"
        ADDR="$(echo ${DOMAIN_MODULE} | cut -f2 -d:)"
        MODULE_TYPE="$(echo ${DOMAIN_MODULE} | cut -f3 -d:)"
        RESOLVED_FILE="$(readlink -f ${MODULE_FILE})"
        SIZE=$(printf '0x%x\n' $(stat -c '%s' "${RESOLVED_FILE}"))
        [ "x${SIZE}" != "x0x0" ] || bbfatal No module: "${MODULE_FILE}"
        write_lop_add_chosen_module "${B}/lop-add-module-${ADDR}.dts" \
            "${ADDR}" "${SIZE}" "${MODULE_TYPE}"
        LOP_MODULE_ARGS="${LOP_MODULE_ARGS} -i ${B}/lop-add-module-${ADDR}.dts"
    done

    QEMUBOOT_DTB="${IMGDEPLOYDIR}/${QB_DTB}"
    QEMUBOOT_DTB_LINK="${IMGDEPLOYDIR}/${QB_DTB_LINK}"

    lopper --werror --verbose \
        -i "${B}/lop-insert-xen-section.dts" \
        -i "${B}/lop-xen-cmdline.dts" \
        ${LOP_MODULE_ARGS} \
        -f -o "${QEMUBOOT_DTB}" \
            "${B}/qemu-dumped.dtb"

    # To assist debugging:
    dtc -I dtb -O dts -o "${B}/output.dts" "${QEMUBOOT_DTB}"

    if [ "${QEMUBOOT_DTB_LINK}" != "${QEMUBOOT_DTB}" ] ; then
        if [ -e "${QEMUBOOT_DTB_LINK}" ] ; then
            rm "${QEMUBOOT_DTB_LINK}"
        fi
        ln -s "${QB_DTB}" "${QEMUBOOT_DTB_LINK}"
    fi
}

do_write_xen_qemuboot_dtb() {
    # Not all architectures qemuboot with a device tree binary, so check
    # to see if one is needed. This allows this bbclass file to be used
    # in the same image recipe for multiple architectures.
    if [ -n "${QB_DTB}" ] && [ -n "${QB_SYSTEM_NAME}" ] ; then
        generate_xen_qemuboot_dtb
    fi
}

addtask do_write_xen_qemuboot_dtb after do_write_qemuboot_conf before do_image
# Task dependency:
# An expected common case is that the kernel for at least one of the initial
# domains (eg. dom0) is deployed from the virtual/kernel recipe, so
# add virtual/kernel:do_deploy as a task dependency here since the kernel size
# needs to be known for generating the device tree.
# Dependencies are only introduced if a device tree will be generated.
QB_XEN_DTB_TASK_DEPENDS ?= " \
    ${@[ ' \
        qemu-helper-native:do_populate_sysroot \
        lopper-native:do_populate_sysroot \
        dtc-native:do_populate_sysroot \
        virtual/kernel:do_deploy \
    ', ''][d.getVar('QB_DTB') == '' or d.getVar('QB_DTB') is None]} \
    "
do_write_xen_qemuboot_dtb[depends] = "${QB_XEN_DTB_TASK_DEPENDS}"

def qemuboot_dtb_vars(d):
    build_vars = ['MACHINE', 'TUNE_ARCH', 'DEPLOY_DIR_IMAGE',
                'KERNEL_IMAGETYPE', 'IMAGE_NAME', 'IMAGE_LINK_NAME',
                'STAGING_DIR_NATIVE', 'STAGING_BINDIR_NATIVE',
                'STAGING_DIR_HOST', 'SERIAL_CONSOLES']
    return build_vars + [k for k in d.keys() if k.startswith('QB_')]

do_write_qemuboot_dtb[vardeps] += "${@' '.join(qemuboot_dtb_vars(d))}"
do_write_qemuboot_dtb[vardepsexclude] += "TOPDIR"
