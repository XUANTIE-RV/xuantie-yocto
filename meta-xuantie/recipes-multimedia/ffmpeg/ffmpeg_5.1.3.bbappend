FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-Update-FFMPEG-5.1.3-and-Adapt-to-OMX.patch \
            file://0002-Fixed-OMX_UseBuffer-api-unsupported-bug.patch \
            file://0003-Increase-FPS-when-Encode-Low-Resolution-Video.patch \
            file://0004-fix-flush-time-sequence-disordered.patch \
            file://0005-Avoid-Releasing-Empty-Resource.patch \
            file://0006-Add-drm_prime-mode-into-OMXDecoders.patch \
            file://0007-Add-h263-omx-decoder.patch \
            file://0008-Enable-mpeg4-omx-decoder.patch \
            file://0009-mpeg4-fix-decoding-stuck-issue-when-width-is-odd.patch \
            file://0010-h263-mpeg4-set-dec-max-resolution.patch \
            file://0011-Optimize-memory-management-of-dmabufs.patch \
            file://0012-Add-vc1-omx-decoder.patch \
            file://0013-Add-vp8-omx-decoder.patch \
            file://0014-Add-mjpeg-omx-decoder.patch \
            file://0015-Change-decoder-close-method.patch \
            file://0016-Fix-decoding-small-resolution-stuck-issue.patch \
            file://0017-Fix-the-issue-of-ineffective-extension-parameters.patch \
            file://0018-Fix-the-Stride-Problem-for-both-Two-Modes.patch \
            file://0019-Fix-the-loop-mode-for-mpv-player.patch \
            file://0020-Fix-zero-copy-playback-of-uncommon-stride-videos.patch \
            file://0021-Make-decoder-out-frame-queue-always-be-adequate.patch \
            file://0022-Ignore-PortChanged-when-data2-is-not-zero.patch \
            file://0023-Avoid-outport-buffers-for-vp9-more-than-16.patch"
CFLAGS += "-DDRM_PRIME"
