DESCRIPTION = "Linux lite debug image for Xuantie"
LICENSE = "MIT"

require xuantie-image-lite.bb

IMAGE_FEATURES += "dbg-pkgs"

export IMAGE_BASENAME = "xuantie-image-lite-debug"
