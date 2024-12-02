require gdb.inc
require gdb-${PV}.inc

inherit python3-dir

EXTRA_OEMAKE:append:libc-musl = "\
                                 gt_cv_func_gnugettext1_libc=yes \
                                 gt_cv_func_gnugettext2_libc=yes \
                                 gl_cv_func_working_strerror=yes \
                                 gl_cv_func_strerror_0_works=yes \
                                 gl_cv_func_gettimeofday_clobber=no \
                                "

do_configure:prepend() {
	if [ "${@bb.utils.filter('PACKAGECONFIG', 'python', d)}" ]; then
		cat > ${WORKDIR}/python << EOF
#!/bin/sh
case "\$2" in
	--includes) echo "-I${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI}/" ;;
	--ldflags) echo "-Wl,-rpath-link,${STAGING_LIBDIR}/.. -Wl,-rpath,${libdir}/.. -lpthread -ldl -lutil -lm -lpython${PYTHON_BASEVERSION}${PYTHON_ABI}" ;;
	--exec-prefix) echo "${exec_prefix}" ;;
	*) exit 1 ;;
esac
exit 0
EOF
		chmod +x ${WORKDIR}/python
	fi
}

SRCREV = "24f34af303fb837aedb4e455fc5456017d15a4f4"

SRC_URI = "git://github.com/XUANTIE-RV/binutils-gdb;branch=xuantie-binutils-gdb-2.35;protocol=https;destsuffix=${S} \
           file://0001-make-man-install-relative-to-DESTDIR.patch \
           file://0002-mips-linux-nat-Define-_ABIO32-if-not-defined.patch \
           file://0003-ppc-ptrace-Define-pt_regs-uapi_pt_regs-on-GLIBC-syst.patch \
           file://0004-Add-support-for-Renesas-SH-sh4-architecture.patch \
           file://0005-Dont-disable-libreadline.a-when-using-disable-static.patch \
           file://0006-use-asm-sgidefs.h.patch \
           file://0007-Use-exorted-definitions-of-SIGRTMIN.patch \
           file://0008-Change-order-of-CFLAGS.patch \
           file://0009-resolve-restrict-keyword-conflict.patch \
           file://0010-Fix-invalid-sigprocmask-call.patch \
           file://0011-gdbserver-ctrl-c-handling.patch \
"
