DESCRIPTION = "LINPACK Benchmarks are a measure of a system's floating point computing power"
SUMMARY = "LINPACK is a software library for performing numerical linear algebra on digital computers"

LICENSE = "PD"
LIC_FILES_CHKSUM ="file://${WORKDIR}/linpack.c;beginline=1;endline=23;md5=e4934af5a9343fbf49a1dd9d0f005378"

SRC_URI = "http://www.netlib.org/benchmark/linpackc;downloadfilename=linpack.c; \
           file://block_getrusage.patch \
           file://0001-set-NTIMES-1000.patch"
SRC_URI[md5sum] = "42343777e9063f66bc63f4d8a1dfabc3"
SRC_URI[sha256sum] = "aeeb16b8e0a966794fa1f670097e92b557ceb001b157a34d0b757183d9b3897c"
S = "${WORKDIR}"
inherit autotools
inherit xuantie-set-package-arch-with-mcpu-benchmark
PACKAGE_ARCH = "${XUANTIE_MCPU_PACKAGE_ARCH}_external"

PR = "r8"

# GCC 15 defaults to -std=gnu23 which treats K&R `()` declarations as `(void)`.
# linpack.c uses K&R prototypes such as `REAL ddot(),t;` followed by calls
# `ddot(k,...)` — these now fail with "too many arguments". Force pre-C23
# semantics so the upstream K&R sources still compile.
CFLAGS:append = " -std=gnu17"
LDFLAGS:append = " -static"

DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'xtstring-opt', ' libxtstring-muslc', '', d)}"
export LIBS = "${LD_LIBXTSTRING}"

python () {
    if "c906" in d.getVar('XUANTIE_MCPU') or \
       "c907" in d.getVar('XUANTIE_MCPU') or \
       "c908" in d.getVar('XUANTIE_MCPU') or \
       "r908" in d.getVar('XUANTIE_MCPU'):
        d.setVar('CFLAGS', d.getVar('CFLAGS') + " -frename-registers")
}

do_compile () {
    ${CC} ${CFLAGS} ${LDFLAGS} -DDP  -DUNROLL -o linpack_dp_unroll ${WORKDIR}/linpack.c -lm ${LIBS}
    ${CC} ${CFLAGS} ${LDFLAGS} -DSP  -DUNROLL -o linpack_sp_unroll ${WORKDIR}/linpack.c -lm ${LIBS}
    ${CC} ${CFLAGS} ${LDFLAGS} -DDP  -DROLL   -o linpack_dp ${WORKDIR}/linpack.c -lm ${LIBS}
    ${CC} ${CFLAGS} ${LDFLAGS} -DSP  -DROLL   -o linpack_sp ${WORKDIR}/linpack.c -lm ${LIBS}
}

do_install () {
    install -Dm 0755 linpack_dp ${D}${bindir}/linpack_dp
    install -Dm 0755 linpack_sp ${D}${bindir}/linpack_sp
    install -Dm 0755 linpack_dp_unroll ${D}${bindir}/linpack_dp_unroll
    install -Dm 0755 linpack_sp_unroll ${D}${bindir}/linpack_sp_unroll
}

do_compile:toolchain-clang () {
    ${CC} ${CFLAGS} ${LDFLAGS} -DDP -DROLL -o linpack_dp ${WORKDIR}/linpack.c -lm ${LIBS}
    ${CC} ${CFLAGS} ${LDFLAGS} -DSP -DROLL -o linpack_sp ${WORKDIR}/linpack.c -lm ${LIBS}
    ${CC} ${CFLAGS} ${LDFLAGS} -DDP -DUNROLL -o linpack_dp_unroll ${WORKDIR}/linpack.c -lm ${LIBS}
    ${CC} ${CFLAGS} ${LDFLAGS} -DSP -DUNROLL -o linpack_sp_unroll ${WORKDIR}/linpack.c -lm ${LIBS}
}

do_install:toolchain-clang () {
    install -Dm 0755 linpack_dp ${D}${bindir}/linpack_dp
    install -Dm 0755 linpack_sp ${D}${bindir}/linpack_sp
    install -Dm 0755 linpack_dp_unroll ${D}${bindir}/linpack_dp_unroll
    install -Dm 0755 linpack_sp_unroll ${D}${bindir}/linpack_sp_unroll
}

