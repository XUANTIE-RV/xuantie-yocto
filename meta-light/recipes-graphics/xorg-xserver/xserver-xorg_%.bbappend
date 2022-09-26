EXTRA_OECONF_remove = "--enable-secure-rpc CPPFLAGS=`pkg-config --cflags libtirpc`"

EXTRA_OECONF_append = " --disable-secure-rpc"
