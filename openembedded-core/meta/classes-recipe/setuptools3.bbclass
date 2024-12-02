#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit setuptools3-base python_pep517

DEPENDS += "python3-setuptools-native python3-wheel-native"

SETUPTOOLS_BUILD_ARGS ?= ""

SETUPTOOLS_SETUP_PATH ?= "${S}"

setuptools3_do_configure() {
    :
}

setuptools3_do_compile() {
        cd ${SETUPTOOLS_SETUP_PATH}
        NO_FETCH_BUILD=1 \
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py \
        bdist_wheel --verbose --dist-dir ${PEP517_WHEEL_PATH} ${SETUPTOOLS_BUILD_ARGS} || \
        bbfatal_log "'${PYTHON_PN} setup.py bdist_wheel ${SETUPTOOLS_BUILD_ARGS}' execution failed."
}
setuptools3_do_compile[vardepsexclude] = "MACHINE"
do_compile[cleandirs] += "${PEP517_WHEEL_PATH}"

# This could be removed in the future but some recipes in meta-oe still use it
setuptools3_do_install() {
        python_pep517_do_install
}

EXPORT_FUNCTIONS do_configure do_compile do_install

export LDSHARED="${CCLD} -shared"

inherit deploy

WHLDEPLOYDIR ?= "${DEPLOYDIR}/whl"

do_deploy() {
}

do_deploy:class-target () {
    if [ "$(ls -A  ${PEP517_WHEEL_PATH}/*.whl 2>/dev/null)" ]; then
        for file in  ${PEP517_WHEEL_PATH}/*.whl; do
            if [ ! -d ${WHLDEPLOYDIR} ]; then
                mkdir -p ${WHLDEPLOYDIR}
            fi
            case "$file" in
                *_x86_64.whl)
                    filename=$(echo $(basename $file) | sed "s/_x86_64.whl//")
                    cp "$file" "${WHLDEPLOYDIR}/${filename}_${TUNE_FEATURES}.whl"
                    ;;
                *)
                    cp "$file" "${WHLDEPLOYDIR}/"
                    ;;
            esac
        done
    fi
}

addtask deploy before do_package after do_install
