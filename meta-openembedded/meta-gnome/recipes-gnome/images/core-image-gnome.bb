require core-image-minimal-gnome.bb

SUMMARY = "Basic desktop image for ice boards"

IMAGE_FEATURES += "splash package-management x11 ssh-server-openssh \
                   tools-debug dev-pkgs debug-tweaks"

IMAGE_INSTALL += "gpgme gnupg"


# boot to desktop
SYSTEMD_DEFAULT_TARGET = "graphical.target"

# Gstreamer Packages
IMAGE_INSTALL += " ffmpeg \
                   gstreamer1.0 \
                   gstreamer1.0-plugins-base \
                   gstreamer1.0-plugins-bad \
                   gstreamer1.0-plugins-good \
                   gstreamer1.0-plugins-ugly \
                   gstreamer1.0-libav \
                   gstreamer1.0-rtsp-server \
                   json-glib \
                   libdaemon \
                   jansson \
                   "
