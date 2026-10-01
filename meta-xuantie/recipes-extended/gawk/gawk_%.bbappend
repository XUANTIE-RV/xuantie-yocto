# GCC 15 (C23): gawk K&R 函数声明导致指针类型不匹配
CFLAGS:append = "${@' -std=gnu17 -Wno-error=incompatible-pointer-types' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
