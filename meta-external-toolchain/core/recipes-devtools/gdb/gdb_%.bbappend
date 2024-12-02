PROVIDES:append:tcmode-external = " ${@'gdbserver' if '${PREFERRED_PROVIDER_${MLPREFIX}gdbserver}' == '${PN}' else ''}"

# Disable build of gdbserver if is provided by external-sourcery-toolchain
PACKAGES:remove:tcmode-external = "${@'gdbserver' if '${PREFERRED_PROVIDER_${MLPREFIX}gdbserver}' != '${PN}' else ''}"
DISABLE_GDBSERVER = "${@'--disable-gdbserver' if '${PREFERRED_PROVIDER_${MLPREFIX}gdbserver}' != '${PN}' else ''}"
EXTRA_OECONF:append:tcmode-external = " ${DISABLE_GDBSERVER}"
