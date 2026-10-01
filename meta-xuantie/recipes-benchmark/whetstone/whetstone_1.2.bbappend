LDFLAGS += "-static"

DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'xtstring-opt', ' libxtstring-muslc', '', d)}"
export LIBS = "${LD_LIBXTSTRING}"

do_compile () {
	${CC} ${CFLAGS} ${LDFLAGS} -o whetstone whetstone.c -lm ${LIBS}
}
