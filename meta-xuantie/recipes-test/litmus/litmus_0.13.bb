DESCRIPTION = "WebDAV server protocol compliance test suite"
HOMEPAGE = "http://www.webdav.org/neon/litmus/"
SECTION = "tests"
LICENSE = "CLOSED"

SRC_URI = "http://www.webdav.org/neon/litmus/litmus-0.13.tar.gz"
SRC_URI[sha256sum] = "09d615958121706444db67e09c40df5f753ccf1fa14846fdeb439298aa9ac3ff"

S = "${WORKDIR}/litmus-0.13"

DEPENDS = "neon"

inherit autotools
