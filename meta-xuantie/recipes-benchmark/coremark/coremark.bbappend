do_configure:append() {
    UNSUPPORTED_FLAGS="msignedness-cmpiv mno-thread-jumps1 mno-iv-adjust-addr-cost mno-expand-split-imm"

    TEST_FILE="test_flags.c"
    echo 'int main() { return 0; }' > "$TEST_FILE"

    for flag in $UNSUPPORTED_FLAGS; do
        echo "test GCC is support -$flag..."

        if ${CC} -"$flag" -S "$TEST_FILE" 2>/dev/null; then
            echo "GCC support -$flag"
        else
            echo "GCC not support -$flag"
            sed -i "s/-$flag//g" "${S}/Makefile"
        fi
    done
    rm -f "$TEST_FILE"
}

EXTRA_OEMAKE:append = " TOOLCHAIN=${TOOLCHAIN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'xtstring-opt', ' libxtstring-muslc', '', d)}"
EXTRA_OEMAKE:append = "${@bb.utils.contains('DISTRO_FEATURES', 'xtstring-opt', ' LFLAGS_END+=-lXTString', '', d)}"

# XuanTie clang has a private LLVM RISC-V backend pass `-riscv-default-unroll`
# that open-source clang 18 does not recognize ("Unknown command line argument").
# The patch ships the clang CFLAGS without that flag; re-inject it here for
# vendor-xuantie + toolchain-clang only.
do_configure:append:vendor-xuantie:toolchain-clang () {
    sed -i 's|-mllvm -inline-threshold=500|-mllvm -inline-threshold=500 -mllvm -riscv-default-unroll=false|' "${S}/Makefile"
}
