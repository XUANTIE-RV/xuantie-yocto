SUMMARY = "WebKit web rendering engine for the GTK+ platform"
HOMEPAGE = "https://www.webkitgtk.org/"
BUGTRACKER = "https://bugs.webkit.org/"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_riscv64 = "file://ResourceResponseBase.cpp \
			           "

do_configure_prepend_riscv64() {
	  echo "THISDIR= ${THISDIR}/${PN}"
	  echo "PN=${PN}"
	  echo "PV=${PV}"
      if [ -f "${WORKDIR}/ResourceResponseBase.cpp" -a -d "${WORKDIR}/${PN}-${PV}/Source/WebCore/platform/network/" ]; then
	      cp -rf ${WORKDIR}/ResourceResponseBase.cpp ${WORKDIR}/${PN}-${PV}/Source/WebCore/platform/network/
	  fi
}
