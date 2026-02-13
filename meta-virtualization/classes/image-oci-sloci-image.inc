IMAGE_CMD:oci() {
    sloci_options=""

    bbdebug 1 "OCI image settings:"
    bbdebug 1 "  author: ${OCI_IMAGE_AUTHOR}"
    bbdebug 1 "  author email: ${OCI_IMAGE_AUTHOR_EMAIL}"
    bbdebug 1 "  tag: ${OCI_IMAGE_TAG}"
    bbdebug 1 "  arch: ${OCI_IMAGE_ARCH}"
    bbdebug 1 "  subarch: ${OCI_IMAGE_SUBARCH}"
    bbdebug 1 "  entrypoint: ${OCI_IMAGE_ENTRYPOINT}"
    bbdebug 1 "  entrypoing args: ${OCI_IMAGE_ENTRYPOINT_ARGS}"
    bbdebug 1 "  labels: ${OCI_IMAGE_LABELS}"
    bbdebug 1 "  uid: ${OCI_IMAGE_RUNTIME_UID}"
    bbdebug 1 "  working dir: ${OCI_IMAGE_WORKINGDIR}"
    bbdebug 1 "  env vars: ${OCI_IMAGE_ENV_VARS}"
    bbdebug 1 "  ports: ${OCI_IMAGE_PORTS}"

    # Change into the image deploy dir to avoid having any output operations capture
    # long directories or the location.
    cd ${IMGDEPLOYDIR}

    oci_image_label_options=""
    if [ -n "${OCI_IMAGE_LABELS}" ]; then
	for l in ${OCI_IMAGE_LABELS}; do
	    oci_image_label_options="${oci_image_label_options} --label ${l}"
	done
    fi
    oci_image_env_options=""
    if [ -n "${OCI_IMAGE_ENV_VARS}" ]; then
	for l in ${OCI_IMAGE_ENV_VARS}; do
	    oci_image_env_options="${oci_image_env_options} --env ${l}"
	done
    fi
    oci_image_port_options=""
    if [ -n "${OCI_IMAGE_PORTS}" ]; then
	for l in ${OCI_IMAGE_PORTS}; do
	    oci_image_port_options="${oci_image_port_options} --port ${l}"
	done
    fi

    if [ -n "${OCI_IMAGE_RUNTIME_UID}" ]; then
	oci_image_user_options="--user ${OCI_IMAGE_RUNTIME_UID}"
    fi

    if [ -n "${OCI_IMAGE_WORKINGDIR}" ]; then
	oci_image_working_dir_options="--working-dir ${OCI_IMAGE_WORKINGDIR}"
    fi

    if [ -n "${OCI_IMAGE_TAR_OUTPUT}" ]; then
	sloci_options="$sloci_options --tar"
    fi

    # options that always appear are required for a valid oci container image
    # others are optional based on settings.
    sloci-image $sloci_options \
		--arch ${OCI_IMAGE_ARCH} \
		--arch-variant "${OCI_IMAGE_SUBARCH}" \
		--entrypoint ${OCI_IMAGE_ENTRYPOINT} \
		--cmd "${OCI_IMAGE_ENTRYPOINT_ARGS}" \
		--author ${OCI_IMAGE_AUTHOR_EMAIL} \
		${oci_image_user_options} \
		${oci_image_label_options} \
		${oci_image_env_options} \
		${oci_image_working_dir_options} \
		${oci_image_port_options} \
		${IMAGE_ROOTFS} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}-oci:${OCI_IMAGE_TAG}
}
