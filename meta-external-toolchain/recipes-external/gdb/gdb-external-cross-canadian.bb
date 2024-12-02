require recipes-external/gdb/gdb-external.inc
inherit external-toolchain-cross-canadian

PN .= "-${TRANSLATED_TARGET_ARCH}"

FILES_MIRRORS =. "\
    ${exec_prefix}|${target_exec_prefix}/${EXTERNAL_TARGET_SYS}\n \
"

#FILES:${PN} += "\
#    ${@' '.join('${bindir}/${EXTERNAL_TARGET_SYS}-' + i for i in '${gdb_binaries}'.split())} \
#    ${exec_prefix}/share/gdb \
#"

INSANE_SKIP:${PN} += "dev-so staticdev installed-vs-shipped"
#INSANE_SKIP:${PN} += "dev-so staticdev"
