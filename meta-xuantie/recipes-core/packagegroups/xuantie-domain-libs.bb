DESCRIPTION = "This packagegroup includes domain libs"
LICENSE = "MIT"

# install opencv acccording to opencv_xx.bb
python () {
    supported_cpus = ["c906fdv", "c908v", "c920v2", "c920"]
    if d.getVar('XUANTIE_MCPU') in supported_cpus:
        d.appendVar('IMAGE_INSTALL', ' opencv')
}

# install openblas according to openblas_xx.bb
python () {
    supported_cpus = ["c906fdv", "c907fdvm", "c908v", "c920v2", "c920"]
    if d.getVar('XUANTIE_MCPU') in supported_cpus:
        d.appendVar('IMAGE_INSTALL', ' openblas')
}

# install libeigen according to libeigen_xx.bb
python () {
    supported_cpus = ["c906fdv", "c907fdvm", "c908v", "c920v2", "c920"]
    if d.getVar('XUANTIE_MCPU') in supported_cpus:
        d.appendVar('IMAGE_INSTALL', ' libeigen-dev')
}

