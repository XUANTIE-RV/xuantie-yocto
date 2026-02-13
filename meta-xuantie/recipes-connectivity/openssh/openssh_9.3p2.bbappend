PACKAGECONFIG:remove = "rng-tools"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://ssh-host"

do_install:append() {
    install -m 0600 ${WORKDIR}/ssh-host/ssh_host_ecdsa_key         ${D}${sysconfdir}/ssh/
    install -m 0644 ${WORKDIR}/ssh-host/ssh_host_ecdsa_key.pub     ${D}${sysconfdir}/ssh/
    install -m 0600 ${WORKDIR}/ssh-host/ssh_host_ed25519_key       ${D}${sysconfdir}/ssh/
    install -m 0644 ${WORKDIR}/ssh-host/ssh_host_ed25519_key.pub   ${D}${sysconfdir}/ssh/
    install -m 0600 ${WORKDIR}/ssh-host/ssh_host_rsa_key           ${D}${sysconfdir}/ssh/
    install -m 0644 ${WORKDIR}/ssh-host/ssh_host_rsa_key.pub       ${D}${sysconfdir}/ssh/
}
