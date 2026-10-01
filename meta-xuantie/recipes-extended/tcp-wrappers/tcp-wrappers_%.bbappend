# GCC 15 (C23): tcp-wrappers 7.6 大量使用 K&R 风格函数声明和
# 不兼容指针类型赋值。回退到 C17 标准并抑制指针类型警告。
CFLAGS:append = "${@' -std=gnu17 -Wno-error=incompatible-pointer-types' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
