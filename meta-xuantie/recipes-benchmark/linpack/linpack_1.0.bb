DESCRIPTION = "LINPACK Benchmarks are a measure of a system's floating point computing power"
SUMMARY = "LINPACK is a software library for performing numerical linear algebra on digital computers"

LICENSE = "PD"
LIC_FILES_CHKSUM ="file://${WORKDIR}/linpack.c;beginline=1;endline=23;md5=e4934af5a9343fbf49a1dd9d0f005378"

SRC_URI = "http://www.netlib.org/benchmark/linpackc;downloadfilename=linpack.c; \
           file://block_getrusage.patch"
SRC_URI[md5sum] = "42343777e9063f66bc63f4d8a1dfabc3"
SRC_URI[sha256sum] = "aeeb16b8e0a966794fa1f670097e92b557ceb001b157a34d0b757183d9b3897c"
S = "${WORKDIR}"
inherit autotools
inherit xuantie-set-package-arch-with-mcpu-benchmark
PACKAGE_ARCH = "${XUANTIE_MCPU}_external"
PR="r3"
do_patch () {
    patch < block_getrusage.patch
}

python () {
    if "c906" in d.getVar('XUANTIE_MCPU') or \
       "c907" in d.getVar('XUANTIE_MCPU') or \
       "c908" in d.getVar('XUANTIE_MCPU') or \
       "r908" in d.getVar('XUANTIE_MCPU'):
        d.setVar('CFLAGS', d.getVar('CFLAGS') + " -frename-registers")
}

do_compile () {
    ${CC} ${CFLAGS} ${LDFLAGS} -DDP  -DUNROLL -o linpack_dp ${WORKDIR}/linpack.c -lm
    ${CC} ${CFLAGS} ${LDFLAGS} -DSP  -DUNROLL -o linpack_sp ${WORKDIR}/linpack.c -lm
}

do_install () {
    install -Dm 0755 linpack_dp ${D}${bindir}/linpack_dp
    install -Dm 0755 linpack_sp ${D}${bindir}/linpack_sp
}

