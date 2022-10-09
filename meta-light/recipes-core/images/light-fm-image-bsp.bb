DESCRIPTION = "A full image for light fm."

require light-fm-image-minimal.bb

# disable for public release
IMAGE_INSTALL += " gpu-bxm-4-64 "
IMAGE_INSTALL += " gpu-bxm-4-64-gpl "
# disable for public release
IMAGE_INSTALL += " npu-ax3386 "
IMAGE_INSTALL += " npu-ax3386-gpl "
# disable for public release
IMAGE_INSTALL += " thead-fce "
# disable for public release
IMAGE_INSTALL += " thead-ddr-pmu "
# disable for public release
IMAGE_INSTALL += " isp-isp8000l "
IMAGE_INSTALL += " vi-bt "
IMAGE_INSTALL += " vi-kernel "
IMAGE_INSTALL += " xtensa-dsp "
IMAGE_INSTALL += " csi-camera-hal "
IMAGE_INSTALL += " rambus-os-ik-150 "
IMAGE_INSTALL += " efuse-light "
# disable for public release
IMAGE_INSTALL += " libgal-viv "
# disable for public release
IMAGE_INSTALL += " libcsi-g2d "
IMAGE_INSTALL += " iso7816-card "
# ant test request
IMAGE_INSTALL += " evtest "

# disable for public release
IMAGE_INSTALL += " vpu-omxil "
IMAGE_INSTALL += " libomxil "
IMAGE_INSTALL += " process-linker "
IMAGE_INSTALL += " isp-venc-shake "
IMAGE_INSTALL += " vpu-vc8000d-kernel "
IMAGE_INSTALL += " vpu-vc8000e-kernel "
# disable for public release
IMAGE_INSTALL += " csi-hal-vcodec "

# // for public release only, disabled by default for internal release
# IMAGE_INSTALL += " image-proprietary "

export IMAGE_BASENAME = "light-fm-image-bsp"
