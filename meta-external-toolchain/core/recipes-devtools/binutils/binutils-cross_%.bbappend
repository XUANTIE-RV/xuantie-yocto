do_install_append_tcmode-external () {
    install -d ${D}${bindir}/gcc
    for i in ${D}${bindir}/${TARGET_PREFIX}*; do
        ln -s ../$(basename "$i") ${D}${bindir}/gcc/${i##*/${TARGET_PREFIX}}
    done
}
