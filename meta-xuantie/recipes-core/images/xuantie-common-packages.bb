DESCRIPTION = "xuantie common pacakges"
LICENSE = "MIT"

require xuantie-image-minimal.bb
require ../packagegroups/xuantie-buildroot-lite.bb
IMAGE_INSTALL += " \
    acl \
    alsa-lib \
    alsa-ucm-conf \
    alsa-utils \
    asciidoc \
    autoconf \
    automake \
    bc \
    binutils \
    binutils-tools \
    bison \
    brotli \
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
    jbig \
    kernel-selftest \
    lcms \
    lerc \
    libarchive \
    libcap \
    libcereal \
    libcroco \
    libdeflate \
    libevent \
    libffi \
    libgfortran \
    libgomp \
    libgssapi-krb5 \
    libk5crypto \
    libkrb5 \
    libkrb5support \
    libnsl2 \
    libpcre \
    libsamplerate0 \
    libtest-harness-perl \
    libtheora \
    libudev \
    libusb1 \
    libwebp \
    libxml2 \
    libxv \
    litmus-tests-riscv \
    ltp \
    m4 \
    mpfr \
    nasm \
    ncurses \
    nfs-utils \
    ninja \
    nss \
    ntfs-3g-ntfsprogs \
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
    snappy \
    sqlite3 \
    sshfs-fuse \
    stress-ng \
    tar \
    texinfo \
    tiff \
    tk \
    util-linux \
    wget \
    xz \
    yasm \
    zeromq \
    zip \
    zlib \
"

IMAGE_INSTALL:append:riscv64 = " \
    acpica \
    acpitool \
    audit \
    bash-completion \
    build-essential \
    devmem2 \
    dmidecode \
    dos2unix \
    dtc \
    eject \
    fio \
    fontconfig \
    glib-networking \
    glog \
    gmp \
    googletest \
    iozone3 \
    iperf3 \
    ipmitool \
    kernel-devsrc \
     \
    kmod \
    libnl \
    libssh \
    libvirt \
    lsscsi \
    lz4 \
    man-db \
    memtester \
    meson \
    ndctl \
    netcat \
    netperf \
    nginx \
    numactl \
    perftest \
    python3-cython \
    python3-docutils \
    python3-openpyxl \
    python3-pexpect \
    python3-pytest \
    qperf \
    rdma-core \
    rpcsvc-proto \
    rt-tests \
    shadow \
    smallpt \
    sshpass \
    sysstat \
    tcl \
    tcpdump \
    trace-cmd \
    tree \
    ttf-dejavu-common \
"

IMAGE_INSTALL:append:riscv64 = " \
    async-profiler \
    avahi \
    cups \
    go \
    kdump \
    libbpf \
    libunwind \
    nmap \
    nvme-cli \
    openjdk \
    openmpi \
    pahole \
    php \
    softfloat \
    spdk \
    strace \
    tcmalloc \
    testfloat \
    trinity \
    vdsotest \
"

export IMAGE_BASENAME = "xuantie-common-packages"

do_rootfs[noexec] = "1"
do_image[noexec] = "1"
do_image_complete[noexec] = "1"
