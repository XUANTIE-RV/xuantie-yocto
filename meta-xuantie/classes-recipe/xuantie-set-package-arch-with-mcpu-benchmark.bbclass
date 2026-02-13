inherit xuantie-set-package-arch-with-mcpu


CC  = "${CCACHE}${HOST_PREFIX}gcc"
CXX = "${CCACHE}${HOST_PREFIX}g++"
CC:toolchain-clang  = "${CCACHE}${HOST_PREFIX}clang"
CXX:toolchain-clang = "${CCACHE}${HOST_PREFIX}clang++"
CFLAGS = " -O2 "
CFLAGS += " -Wno-implicit-int -Wno-deprecated-non-prototype -Wno-implicit-function-declaration -Wno-return-type -Wno-return-mismatch "
LDFLAGS = " -O2 -static"
FULL_OPTIMIZATION = ""

# Extend cpu configuration for different xuantie cpu
python () {
    if d.getVar('XUANTIE_TUNE_CCARGS_USE_MCPU') != 'NO':
        d.setVar('CC', d.getVar('CC') + " -mcpu=" + d.getVar('XUANTIE_MCPU'))
        d.setVar('CXX', d.getVar('CXX') + " -mcpu=" + d.getVar('XUANTIE_MCPU'))
    else:
        d.setVar('CC', d.getVar('CC') + " " + d.getVar('XUANTIE_TUNE_CCARGS_GENERIC'))
        d.setVar('CXX', d.getVar('CXX') + " " + d.getVar('XUANTIE_TUNE_CCARGS_GENERIC'))
}

