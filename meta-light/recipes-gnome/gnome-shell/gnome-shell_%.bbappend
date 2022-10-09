FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
RDEPENDS_${PN} = " libgles1-mesa"

SRC_URI_append = " \
                  file://0001-Only-launch-wayland-for-gnome-shell.patch \
                  file://0001-Remove-hot-corner-application-due-to-crash-issue.patch \
"
