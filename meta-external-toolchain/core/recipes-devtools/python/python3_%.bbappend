# This build relies on this matching up with HOST_PREFIX, as it doesn't seem
# to use CROSS_COMPILE everywhere. Align it here to fix the build.
HOST_SYS_tcmode-external = "${@'${HOST_PREFIX}'[:-1]}"
