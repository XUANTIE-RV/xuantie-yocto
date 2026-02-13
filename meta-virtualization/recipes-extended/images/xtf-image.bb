require recipes-extended/images/xen-image-minimal.bb
DESCRIPTION = "A minimal Xen Test Framework (XTF) image for testing the Xen hypervisor"

# To run XTF tests with an image built for the qemux86-64 MACHINE:
#
#   runqemu xtf-image nographic slirp
#       (login as root)
#       # xtf-runner expects to be run from the top of the tests directory:
#       cd /usr/libexec/xtf
#       # list the tests available for PV guest types:
#       ./xtf-runner --list pv
#       # run an example test:
#       ./xtf-runner test-pv64-livepatch-priv-check
#
# This image also supports the OE QA framework, so XTF tests can be
# run from bitbake by adding the following (or similar) to local.conf:
#
#   INHERIT += "testimage"
#   QEMU_USE_SLIRP = "1"
#   TEST_SERVER_IP = "127.0.0.1"
#
# and the tests that are configured (see the xtf-oeqa-conf package)
# can be run with: bitbake -c testimage xtf-image
#
# For testimage, see the qemu boot log: ${WORKDIR}/testimage/qemu_boot_log.*
# and the test log: ${WORKDIR}/temp/log.do_testimage

IMAGE_NAME="xtf"

IMAGE_INSTALL:append = " xtf"

DEFAULT_TEST_SUITES:append = " xtf_minimal"

QB_DEFAULT_FSTYPE_x86-64 = "wic"
