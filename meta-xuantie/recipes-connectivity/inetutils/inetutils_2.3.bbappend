do_configure:append:toolchain-clang() {
    sed -i 's/__maybe_unused__/gnu::unused/g' ${B}/config.h
}
