FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_d1 = "file://80-wifi-station.network \
"

do_install_append_d1 () {
	install -m 0644 ${WORKDIR}/80-wifi-station.network ${D}${systemd_unitdir}/network/80-wifi-station.network
}
