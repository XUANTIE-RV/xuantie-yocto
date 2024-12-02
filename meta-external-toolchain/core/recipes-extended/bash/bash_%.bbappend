do_compile:append:tcmode-external () {
    if [ -e support/bash.pc ] ; then
        sed -i -e 's#-B${gcc_bindir}##' support/bash.pc
    fi
}
