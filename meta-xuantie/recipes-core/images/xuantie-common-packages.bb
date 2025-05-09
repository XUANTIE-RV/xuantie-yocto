DESCRIPTION = "xuantie common pacakges"
LICENSE = "MIT"

require xuantie-image-minimal.bb
require ../packagegroups/xuantie-buildroot-lite.bb
IMAGE_INSTALL += " \
    acl \
    alsa-lib \
    alsa-utils \
    asciidoc \
    autoconf \
    automake \
    bc \
    binutils \
    binutils-tools \
    bison \
    clang \
    cmake \
    coreutils \
    curl \
    elfutils \
    ethtool \
    expect \
    file \
    flex \
    gcc \
    gdbm \
    gettext \
    gflags \
    git \
    glib-2.0 \
    gnu-config \
    grep \
    libarchive \
    libcap \
    libcereal \
    libcroco \
    libevent \
    libffi \
    libnsl2 \
    libpcre \
    libsamplerate0 \
    libtest-harness-perl \
    libudev \
    libusb1 \
    libxml2 \
    lz4 \
     \
    m4 \
    mpfr \
    nasm \
    ncurses \
    nfs-utils \
    ninja \
    ntp \
    openssl \
    patchelf \
    perf \
    perl \
    pkgconfig \
    popt \
    python3 \
    python3-pip \
    python3-setuptools \
    readline \
    ripgrep \
    rsync \
    sed \
    sqlite3 \
    sshfs-fuse \
    snappy \
    tar \
    texinfo \
    tk \
    util-linux \
    wget \
    xz \
    yasm \
    zeromq \
    zip \
    zlib \
"

IMAGE_INSTALL:append:riscv64 = " libbpf pahole avahi cups openmpi nmap "

export IMAGE_BASENAME = "xuantie-common-packages"

do_rootfs[noexec] = "1"
do_image[noexec] = "1"
do_image_complete[noexec] = "1"
