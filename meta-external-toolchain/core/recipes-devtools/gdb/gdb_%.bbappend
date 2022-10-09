PROVIDES_append_tcmode-external = " ${@'gdbserver' if '${PREFERRED_PROVIDER_${MLPREFIX}gdbserver}' == '${PN}' else ''}"

# Disable build of gdbserver if is provided by external-sourcery-toolchain
PACKAGES_remove_tcmode-external = "${@'gdbserver' if '${PREFERRED_PROVIDER_${MLPREFIX}gdbserver}' != '${PN}' else ''}"
DISABLE_GDBSERVER = "${@'--disable-gdbserver' if '${PREFERRED_PROVIDER_${MLPREFIX}gdbserver}' != '${PN}' else ''}"
EXTRA_OECONF_append_tcmode-external = " ${DISABLE_GDBSERVER}"
