IMAGE_CMD:oci() {
    umoci_options=""

    bbdebug 1 "UMOCI image settings:"
    bbdebug 1 "  author: ${OCI_IMAGE_AUTHOR}"
    bbdebug 1 "  author email: ${OCI_IMAGE_AUTHOR_EMAIL}"
    bbdebug 1 "  tag: ${OCI_IMAGE_TAG}"
    bbdebug 1 "  arch: ${OCI_IMAGE_ARCH}"
    bbdebug 1 "  subarch: ${OCI_IMAGE_SUBARCH}"
    bbdebug 1 "  entrypoint: ${OCI_IMAGE_ENTRYPOINT}"
    bbdebug 1 "  entrypoint args: ${OCI_IMAGE_ENTRYPOINT_ARGS}"
    bbdebug 1 "  labels: ${OCI_IMAGE_LABELS}"
    bbdebug 1 "  uid: ${OCI_IMAGE_RUNTIME_UID}"
    bbdebug 1 "  working dir: ${OCI_IMAGE_WORKINGDIR}"
    bbdebug 1 "  env vars: ${OCI_IMAGE_ENV_VARS}"
    bbdebug 1 "  ports: ${OCI_IMAGE_PORTS}"

    OCI_REUSE_IMAGE=""

    # Change into the image deploy dir to avoid having any output operations capture
    # long directories or the location.
    cd ${IMGDEPLOYDIR}

    new_image=t
    image_name="${IMAGE_NAME}${IMAGE_NAME_SUFFIX}-oci"
    image_bundle_name="${IMAGE_NAME}${IMAGE_NAME_SUFFIX}-oci-bundle"
    if [ -n "$OCI_REUSE_IMAGE" ]; then
	if [ -d $image_name ]; then
	    bbdebug 1 "OCI: reusing image directory"
	    new_image=""
	fi
    else
	bbdebug 1 "OCI: removing existing container image directory"
	rm -rf $image_name $image_bundle_name
    fi

    if [ -z "${OCI_IMAGE_TAG}" ]; then
	OCI_IMAGE_TAG="initial-tag"
    fi

    if [ -n "$new_image" ]; then
	bbdebug 1 "OCI: umoci init --layout $image_name"
	umoci init --layout $image_name
	umoci new --image $image_name:${OCI_IMAGE_TAG}
	umoci unpack --rootless --image $image_name:${OCI_IMAGE_TAG} $image_bundle_name
    else
	# todo: create a different tag, after checking if the passed one exists
	true
    fi

    bbdebug 1 "OCI: populating rootfs"
    bbdebug 1 "OCI: cp -r ${IMAGE_ROOTFS}/* $image_bundle_name/rootfs/"
    cp -r ${IMAGE_ROOTFS}/* $image_bundle_name/rootfs

    bbdebug 1 "OCI: umoci repack --image $image_name:${OCI_IMAGE_TAG} $image_bundle_name"
    umoci repack --image $image_name:${OCI_IMAGE_TAG} $image_bundle_name

    bbdebug 1 "OCI: configuring image"
    if [ -n "${OCI_IMAGE_LABELS}" ]; then
	for l in ${OCI_IMAGE_LABELS}; do
	    bbdebug 1 "OCI: umoci config --image $image_name:${OCI_IMAGE_TAG} --config.label $l"
	    umoci config --image $image_name:${OCI_IMAGE_TAG} --config.label $l
	done
    fi
    if [ -n "${OCI_IMAGE_ENV_VARS}" ]; then
	for l in ${OCI_IMAGE_ENV_VARS}; do
	    bbdebug 1 "umoci config --image $image_name:${OCI_IMAGE_TAG} --config.env $l"
	    umoci config --image $image_name:${OCI_IMAGE_TAG} --config.env $l
	done
    fi
    if [ -n "${OCI_IMAGE_PORTS}" ]; then
	for l in ${OCI_IMAGE_PORTS}; do
	    bbdebug 1 "umoci config --image $image_name:${OCI_IMAGE_TAG} --config.exposedports $l"
	    umoci config --image $image_name:${OCI_IMAGE_TAG} --config.exposedports $l
	done
    fi
    if [ -n "${OCI_IMAGE_RUNTIME_UID}" ]; then
	bbdebug 1 "umoci config --image $image_name:${OCI_IMAGE_TAG}  --config.user ${OCI_IMAGE_RUNTIME_UID}"
	umoci config --image $image_name:${OCI_IMAGE_TAG} --config.user ${OCI_IMAGE_RUNTIME_UID}
    fi
    if [ -n "${OCI_IMAGE_WORKINGDIR}" ]; then
	bbdebug 1 "umoci config --image $image_name:${OCI_IMAGE_TAG}  --config.workingdir ${OCI_IMAGE_WORKINGDIR}"
	umoci config --image $image_name:${OCI_IMAGE_TAG} --config.workingdir ${OCI_IMAGE_WORKINGDIR}
    fi
    if [ -n "${OCI_IMAGE_STOPSIGNAL}" ]; then
	bbdebug 1 "umoci config --image $image_name:${OCI_IMAGE_TAG}  --config.stopsignal ${OCI_IMAGE_STOPSIGNAL}"
	umoci config --image $image_name:${OCI_IMAGE_TAG} --config.stopsignal ${OCI_IMAGE_STOPSIGNAL}
    fi
    if [ -n "${OCI_IMAGE_OS}" ]; then
	bbdebug 1 "umoci config --image $image_name:${OCI_IMAGE_TAG}  --os ${OCI_IMAGE_OS}"
	umoci config --image $image_name:${OCI_IMAGE_TAG} --os ${OCI_IMAGE_OS}
    fi

    bbdebug 1 "umoci config --image $image_name:${OCI_IMAGE_TAG}  --architecture ${OCI_IMAGE_ARCH}"
    umoci config --image $image_name:${OCI_IMAGE_TAG} --architecture ${OCI_IMAGE_ARCH}
    # NOTE: umoci doesn't currently expose setting the architecture variant,
    #       so if you need it use sloci instead
    if [ -n "${OCI_IMAGE_SUBARCH}" ]; then
	bbnote "OCI: image subarch is set to: ${OCI_IMAGE_SUBARCH}, but umoci does not"
	bbnote "     expose variants. use sloci instead if this is important"
    fi
    umoci config --image $image_name:${OCI_IMAGE_TAG} --config.entrypoint ${OCI_IMAGE_ENTRYPOINT}
    if [ -n "${OCI_IMAGE_ENTRYPOINT_ARGS}" ]; then
	umoci config --image $image_name:${OCI_IMAGE_TAG} --config.cmd "${OCI_IMAGE_ENTRYPOINT_ARGS}"
    fi
    umoci config --image $image_name:${OCI_IMAGE_TAG} --author ${OCI_IMAGE_AUTHOR_EMAIL}

    # make a tar version of the image direcotry
    if [ -n "${OCI_IMAGE_TAR_OUTPUT}" ]; then
	tar -cf "$image_name.tar" "$image_name"
    fi

    # We could make this optional, since the bundle is directly runnable via runc
    rm -rf $image_bundle_name

    ln -sf $image_name ${IMAGE_BASENAME}-${OCI_IMAGE_TAG}-oci
}
