# GCC 15 (C23): xinetd K&R + incompatible-pointer-types
CFLAGS:append = "${@' -std=gnu17 -Wno-error=incompatible-pointer-types' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
