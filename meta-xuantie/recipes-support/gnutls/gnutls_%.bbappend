# GCC 15 (C23): gnutls 内嵌 gnulib 使用了 C23 不兼容的旧式语法
CFLAGS:append = "${@' -std=gnu17' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
