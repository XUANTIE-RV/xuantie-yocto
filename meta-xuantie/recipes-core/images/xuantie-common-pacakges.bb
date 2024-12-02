DESCRIPTION = "xuantie common pacakges"
LICENSE = "MIT"

PACKAGE_ARCH = "${TUNE_PKGARCH}"
inherit packagegroup
require ../packagegroups/xuantie-buildroot-lite.bb
RDEPENDS:${PN} += " \
    acl \
    alsa-lib \
    alsa-utils \
    asciidoc \
    autoconf \
    automake \
    bc \
    binutils-gdb-prebuilt \
    bison \
    cmake \
    coreutils \
    curl \
    elfutils \
    file \
    flex \
    gdbm \
    gettext \
    git \
    glib-2.0 \
    gnu-config \
    grep \
    libarchive \
    libcap \
    libcereal \
    libcroco \
    libffi \
    libnsl2 \
    libpcre \
    libsamplerate0 \
    libtest-harness-perl \
    libudev \
    libusb1 \
    libxml2 \
    lz4 \
    lzbench-xuantie-test \
    m4 \
    mpfr \
    nasm \
    ncurses \
    ninja \
    openssl \
    perl \
    python3 \
    python3-pip \
    python3-setuptools \
    readline \
    ripgrep \
    sed \
    sqlite3 \
    tar \
    util-linux \
    wget \
    xz \
    yasm \
    zip \
    zlib \
"
RDEPENDS:${PN}:append:riscv64 = "libbpf pahole avahi cups"
