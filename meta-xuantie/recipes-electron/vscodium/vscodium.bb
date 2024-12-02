SUMMARY = "VSCodium Build Recipe"
DESCRIPTION = "Recipe to build VSCodium"
HOMEPAGE = "https://vscodium.com/"
LICENSE = "CLOSED"

inherit pkgconfig mime-xdg

SRC_URI = " \
    git://github.com/VSCodium/vscodium.git;branch=master;protocol=https \
    file://riscv64-linux-g++;subdir=${S}/depot_tools \
    file://riscv64-linux-gcc;subdir=${S}/depot_tools \
    file://codium.sh \
    file://codium.desktop \
    file://patches \
"

S = "${WORKDIR}/git"
B = "${S}"

VSCODE_RELEASE_VERSION="24165"
SRCREV = "1709f8b501aec801d663f025127f988d260e8031"
PV = "1.90.1.${VSCODE_RELEASE_VERSION}"

DEPENDS += " \
    ca-certificates-native \
    libx11 \
    libxkbfile \
    libsecret \
    krb5 \
    gcc-runtime \
    curl-native \
    gnupg-native \
    unzip-native \
    jq-native \
    nodejs-native \
    electron \
"

RDEPENDS:${PN} += " \
    bash \
    ripgrep \
"

export UNAME_ARCH="${TARGET_ARCH}"

do_configure() {
    FLAG_FILE="${S}/patch_code_file"
    if [ ! -f "$FLAG_FILE" ]; then
        sed -i 's/UNAME_ARCH=$( uname -m )/#UNAME_ARCH=$( uname -m )/g' ${S}/build/build.sh
        sed -i 's/date=$( date +%Y%j )/date="${VSCODE_RELEASE_VERSION}"/' ${S}/get_repo.sh
        for i in $(ls ${WORKDIR}/patches/*.patch); do patch -p1 < $i; done
        touch $FLAG_FILE
    fi
}

do_extract_proxies() {
    YARNRC_PATH="$HOME/.yarnrc"

    set +e
    if [ -f "$YARNRC_PATH" ]; then
        HTTP_PROXY=$(grep -E '^proxy ' "$YARNRC_PATH" | sed 's/proxy "//; s/"//')
        [ -n "$HTTP_PROXY" ] && export http_proxy="$HTTP_PROXY"

        HTTPS_PROXY=$(grep -E '^https-proxy ' "$YARNRC_PATH" | sed 's/https-proxy "//; s/"//')
        [ -n "$HTTPS_PROXY" ] && export https_proxy="$HTTPS_PROXY"
    fi
    set -e
    mkdir -p ${S}/depot_tools
    ln -sf /usr/bin/yarn ${S}/depot_tools
    ln -sf /usr/bin/yarnpkg ${S}/depot_tools
    # fix error vscode/remote/node_modules/@parcel/watcher/build/node_modules/node-addon-api/nothing.target.mk:109
    ln -sf /usr/bin/cc ${S}/depot_tools
    export PATH="${S}/depot_tools:$PATH"
}

do_compile[network] = "1"

do_compile() {
    do_extract_proxies
    cd ${S}
    FLAG_FILE="${S}/fetch_code_file"
    if [ ! -f "$FLAG_FILE" ]; then
        ./build/build.sh
        touch $FLAG_FILE
    else
        ./build/build.sh -s
    fi
}

do_install() {
    install -d ${D}/${bindir}
    install -d ${D}/${datadir}/pixmaps
    install -d ${D}/${datadir}/applications
    install -d ${D}${datadir}/codium
    install -d ${D}${datadir}/codium/resources/app

    cp -rf ${RECIPE_SYSROOT}/${datadir}/electron/* ${D}/${datadir}/codium/
    mv ${D}/${datadir}/codium/electron ${D}/${datadir}/codium/codium
    ln -sf ${datadir}/codium/bin/codium ${D}/${bindir}/codium

    install -m 0644 ${B}/VSCode-linux-riscv64/resources/linux/code.png ${D}/${datadir}/pixmaps/vscodium.png
    install -m 0644 ${WORKDIR}/codium.desktop ${D}/${datadir}/applications

    install -m 0755 ${WORKDIR}/codium.sh ${D}/${datadir}/codium/codium.sh
    cp -rf ${B}/VSCode-linux-riscv64/bin ${D}/${datadir}/codium
    cp ${B}/VSCode-linux-riscv64/LICENSE.txt ${D}/${datadir}/codium/resources/app
    cp ${B}/VSCode-linux-riscv64/node_modules.asar ${D}/${datadir}/codium/resources/app
    cp -rf ${B}/VSCode-linux-riscv64/node_modules.asar.unpacked ${D}/${datadir}/codium/resources/app
    cp -rf ${B}/VSCode-linux-riscv64/out ${D}/${datadir}/codium/resources/app
    cp -rf ${B}/VSCode-linux-riscv64/package.json ${D}/${datadir}/codium/resources/app
    cp -rf ${B}/VSCode-linux-riscv64/product.json ${D}/${datadir}/codium/resources/app
    cp -rf ${B}/VSCode-linux-riscv64/resources ${D}/${datadir}/codium/resources/app
    cp -rf ${B}/VSCode-linux-riscv64/ThirdPartyNotices.txt ${D}/${datadir}/codium/resources/app
    cp -rf ${B}/VSCode-linux-riscv64/extensions ${D}/${datadir}/codium/resources/app
    rm -rf ${D}/${datadir}/codium/resources/app/node_modules.asar.unpacked/@vscode/ripgrep/bin/rg
    ln -sf ${bindir}/rg ${D}/${datadir}/codium/resources/app/node_modules.asar.unpacked/@vscode/ripgrep/bin/rg
}

FILES:${PN} = "${bindir} ${datadir}"

PRIVATE_LIBS:${PN} = "libffmpeg.so libEGL.so libGLESv2.so libvulkan.so.1 libvk_swiftshader.so"

INSANE_SKIP:${PN} = "libdir already-stripped"
