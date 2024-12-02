#!/usr/bin/python3 -B

import os
import sys
import shutil

sys.path.append("build/linux/unbundle")

import replace_gn_files

# Note: libvpx unbundling is currently broken with no easy fix;
# see https://crbug.com/1307941

keepers = ('ffmpeg', 'harfbuzz-ng', 'icu', 'libvpx',
    'absl_algorithm',
    'absl_base',
    'absl_cleanup',
    'absl_container',
    'absl_debugging',
    'absl_flags',
    'absl_functional',
    'absl_hash',
    'absl_log',
    'absl_log_internal',
    'absl_memory',
    'absl_meta',
    'absl_numeric',
    'absl_random',
    'absl_status',
    'absl_strings',
    'absl_synchronization',
    'absl_time',
    'absl_types',
    'absl_utility',
    'crc32c',
    'dav1d',
    'libaom' ,
    'libavif' ,
    'libjxl' ,
    'libyuv' ,
    'swiftshader-SPIRV-Headers' ,
    'swiftshader-SPIRV-Tools' ,
    'vulkan-SPIRV-Headers' ,
    'vulkan-SPIRV-Tools' ,
    'brotli' ,
    'double-conversion' ,
    'fontconfig' ,
    'jsoncpp' ,
    'libdrm' ,
    'libevent' ,
    'libpng' ,
    'libwebp' ,
    'libxml' ,
    'libXNVCtrl' ,
    'libxslt' ,
    'openh264' ,
    'opus' ,
    're2' ,
    'snappy' ,
    'woff2' ,
    'zlib' ,
    'freetype' ,
    'libjpeg' ,
    )

for lib,rule in replace_gn_files.REPLACEMENTS.items():
    print("build/linux/unbundle/%s.gn %s"%(lib,rule))
    if lib not in keepers:
        # create a symlink to the unbundle gn file
        symlink = "ln -sf "
        path = os.path.split(rule)
        if not os.path.exists(path[0]):
            os.mkdir(path[0])
        while path[0] != '':
            path = os.path.split(path[0])
            symlink += '../'
        symlink += "build/linux/unbundle/%s.gn %s"%(lib,rule)
        print(symlink)
        if os.system(symlink):
            raise RuntimeError("error creating symlink",symlink)
