FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-Fix-compile-cpu.c-not-find-sysctl.h-file-error.patch \
            file://0001-Add-OMX-CODECS.patch \
            file://0002-OMX-CODECS-Performance-Enhanced.patch \
            file://0003-OMX-CODECS-Stability-Enhanced.patch \
            file://0004-Fix-OMX-Decoders-For-Dynamic-Resolution-Videos.patch \
            file://0005-Improve-the-OMX-encoders-for-lower-CPU-usage.patch"
