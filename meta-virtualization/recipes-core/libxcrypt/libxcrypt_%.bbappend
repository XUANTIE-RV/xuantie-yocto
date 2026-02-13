# if vmsep is in distro features, we need a static libxycryp, so we can build
# a static busybox. The end result of this is:
#    EXTRA_OECONF:remove = "--disable-static"
EXTRA_OECONF:remove = "${@bb.utils.contains('DISTRO_FEATURES', 'vmsep', '--disable-static', '', d)}"
