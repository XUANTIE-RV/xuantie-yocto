LDLIBS:append:riscv32 = " -latomic"

do_configure:prepend:riscv32 () {
    export LDLIBS="${LDLIBS}"
}
