#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#
# DESCRIPTION
# This implements the 'bootimg-biosxen' source plugin class for 'wic'
#
# Bootloader arguments: Xen args are separated from Linux ones at '---':
# eg.
#   bootloader --append="console=com1,vga com1=115200,8n1 --- console=hvc0"
#
# Optional source param: initrd
# accepts multiple ramdisk files to be supplied to multiboot.
#  eg.
#   part /boot --source bootimg-biosxen --sourceparams="initrd=foo.initrd;bar.initrd"
#
# AUTHORS
# Christopher Clark <christopher.w.clark [at] gmail.com>
# Elements derived from bootimg-biosplusefi.py by:
#   William Bourque <wbourque [at] gmail.com>

import logging
import os
import types

from wic import WicError
import wic.pluginbase
from importlib.machinery import SourceFileLoader
from wic.misc import (exec_cmd, get_bitbake_var)

logger = logging.getLogger('wic')

class BootimgBiosXenPlugin(wic.pluginbase.SourcePlugin):
    """
    Create MBR boot partition including files for Xen

    """

    name = 'bootimg-biosxen'
    __PCBIOS_MODULE_NAME = "bootimg-pcbios"
    __imgBiosObj = None

    @classmethod
    def __init__(cls):
        """
        Constructor (init)
        """
        # original comment from bootimg-biosplusefi.py :
        # "XXX For some reasons, __init__ constructor is never called.
        #  Something to do with how pluginbase works?"
        cls.__instanciateBIOSClass()

    @classmethod
    def __instanciateBIOSClass(cls):
        """

        """
        # Import bootimg-pcbios (class name "BootimgPcbiosPlugin")
        modulePath = os.path.join(os.path.dirname(wic.pluginbase.__file__),
                                  "plugins", "source",
                                  cls.__PCBIOS_MODULE_NAME + ".py")
        loader = SourceFileLoader(cls.__PCBIOS_MODULE_NAME, modulePath)
        mod = types.ModuleType(loader.name)
        loader.exec_module(mod)
        cls.__imgBiosObj = mod.BootimgPcbiosPlugin()

    @classmethod
    def do_install_disk(cls, disk, disk_name, creator, workdir, oe_builddir,
                        bootimg_dir, kernel_dir, native_sysroot):
        """
        Called after all partitions have been prepared and assembled into a
        disk image.
        """
        if not cls.__imgBiosObj:
            cls.__instanciateBIOSClass()

        cls.__imgBiosObj.do_install_disk(disk, disk_name, creator, workdir,
                                         oe_builddir, bootimg_dir, kernel_dir,
                                         native_sysroot)

    @classmethod
    def do_configure_partition(cls, part, source_params, creator, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        """
        Called before do_prepare_partition(), creates syslinux config
        """
        if not cls.__imgBiosObj:
            cls.__instanciateBIOSClass()

        bootloader = creator.ks.bootloader

        if not bootloader.configfile:
            splash = os.path.join(cr_workdir, "/hdd/boot/splash.jpg")
            if os.path.exists(splash):
                splashline = "menu background splash.jpg"
            else:
                splashline = ""

            syslinux_conf = ""
            syslinux_conf += "PROMPT 0\n"
            syslinux_conf += "TIMEOUT " + str(bootloader.timeout) + "\n"
            syslinux_conf += "\n"
            syslinux_conf += "ALLOWOPTIONS 1\n"
            syslinux_conf += "\n"
            if splashline:
                syslinux_conf += "%s\n" % splashline

            syslinux_conf += "DEFAULT boot\n"
            syslinux_conf += "LABEL boot\n"
            syslinux_conf += "  KERNEL mboot.c32\n"

            # Split the bootloader args at '---' to separate the Xen args
            # from the Linux kernel args.
            # The Xen args here are defaults; overridden by bootloader append.
            xen_args = "console=com1,vga com1=115200,8n1"
            kernel_append = ""
            if bootloader.append:
                separator_pos = bootloader.append.find('---')
                if separator_pos != -1:
                    xen_args = bootloader.append[:separator_pos]
                    kernel_append = bootloader.append[separator_pos+3:]
                else:
                    kernel_append = bootloader.append

            kernel_args = "label=boot root=%s %s" % \
                          (creator.rootdev, kernel_append)

            syslinux_conf += "  APPEND /xen.gz %s --- /vmlinuz %s" % \
                             (xen_args, kernel_args)

            initrd = source_params.get('initrd')
            if initrd:
                initrds = initrd.split(';')
                for initrd_file in initrds:
                    syslinux_conf += " --- /%s" % os.path.basename(initrd_file)
            syslinux_conf += "\n"

            logger.debug("Writing syslinux config %s/hdd/boot/syslinux.cfg",
                         cr_workdir)

            hdddir = "%s/hdd/boot" % cr_workdir
            install_cmd = "install -d %s" % hdddir
            exec_cmd(install_cmd)

            cfg = open("%s/hdd/boot/syslinux.cfg" % cr_workdir, "w")
            cfg.write(syslinux_conf)
            cfg.close()

        else:
            cls.__imgBiosObj.do_configure_partition(part, source_params,
                                                    creator, cr_workdir,
                                                    oe_builddir, bootimg_dir,
                                                    kernel_dir, native_sysroot)

    @classmethod
    def do_prepare_partition(cls, part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        """
        if not cls.__imgBiosObj:
            cls.__instanciateBIOSClass()

        bootimg_dir = cls.__imgBiosObj._get_bootimg_dir(bootimg_dir, 'syslinux')
        hdddir = "%s/hdd/boot" % cr_workdir

        # machine-deduction logic originally from isoimage-isohybrid.py
        initrd_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
        if not initrd_dir:
            raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting.")
        machine = os.path.basename(initrd_dir)

        xen = "xen-" + machine + ".gz"

        cmds = ["install -m 0644 %s/%s %s/xen.gz" %
                (kernel_dir, xen, hdddir),
                "install -m 0644 %s/syslinux/mboot.c32 %s/mboot.c32" %
                (bootimg_dir, hdddir)]

        initrd = source_params.get('initrd')

        # Allow multiple 'initrds', as per the bootimg-efi class.
        # This can be used to install additional binaries for multiboot.
        # eg. TXT ACMs, XSM/Flask policy file, microcode binary
        if initrd:
            initrds = initrd.split(';')
            for initrd_file in initrds:
                cmds.append("install -m 0644 %s/%s %s/%s" %
                            (kernel_dir, initrd_file, hdddir,
                             os.path.basename(initrd_file)))

        for install_cmd in cmds:
            exec_cmd(install_cmd)

        cls.__imgBiosObj.do_prepare_partition(part, source_params,
                                              creator, cr_workdir,
                                              oe_builddir, bootimg_dir,
                                              kernel_dir, rootfs_dir,
                                              native_sysroot)
