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
