#!/bin/sh

function gettop
{
    local TOPFILE=openembedded-core/envsetup.sh
    if [ -n "$MPSOC_TOP" -a -f "$MPSOC_TOP/$TOPFILE" ] ; then
        # The following circumlocution ensures we remove symlinks from TOP.
        (\cd $MPSOC_TOP; PWD= /bin/pwd)
    else
        if [ -f $TOPFILE ] ; then
            # The following circumlocution (repeated below as well) ensures
            # that we record the true directory name and not one that is
            # faked up with symlink names.
            PWD= /bin/pwd
        else
            local here="${PWD}"
            while [ "${here}" != "/" ]; do
                if [ -f "${here}/${TOPFILE}" ]; then
                    (\cd ${here}; PWD= /bin/pwd)
                    break
                fi
                here="$(dirname ${here})"
            done
        fi
    fi
}


pack_usage()
{
	printf "Usage: pack [-cARCH] [-iIMAGE] [-h]
	-a ARCH (default: riscv)
	-h print this help message
"
}

T=$(gettop)
ARCH=riscv
IMAGE_DIR=thead-build/ice-base
while getopts "a:o:h" arg
do
	case $arg in
		a)
			ARCH=${OPTARG}
			;;
		o)
			IMAGE_DIR=${OPTARG}
			;;
		h)
			pack_usage
			exit 0
			;;
		*)
		#exit 0
		;;
	esac
done

if [ "x$ARCH" = "x" ]; then
	echo "ARCH:$ARCH not support"
	exit 1
fi
#$T/scripts/pack_img.sh -c $chip -p $platform -b $board \
echo "$T/openembedded-core/oe-init-build-env $IMAGE_DIR"
[ -e ./openembedded-core/oe-init-build-env ] &&
    source ./openembedded-core/oe-init-build-env $IMAGE_DIR

