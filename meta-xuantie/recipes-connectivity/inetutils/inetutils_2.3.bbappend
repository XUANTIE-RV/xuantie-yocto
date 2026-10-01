# Fix GCC 15 C23 incompatible-pointer-types error
CFLAGS:append = "${@' -Wno-error=incompatible-pointer-types -std=gnu17' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"

do_configure:append:toolchain-clang() {
    sed -i 's/__maybe_unused__/gnu::unused/g' ${B}/config.h
}
