OPENCV_BB_INC = "opencv.inc"
OPENCV_BB_INC:append:xuantie-buildmode-source = " opencv-4.12.0.inc"
inherit xuantie-prebuilt-src
require ${OPENCV_BB_INC}
