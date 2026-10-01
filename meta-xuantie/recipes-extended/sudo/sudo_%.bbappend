# GCC 15 (C23): sudo K&R 函数声明 + 不兼容指针类型
CFLAGS:append = "${@' -std=gnu17 -Wno-error=incompatible-pointer-types' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
