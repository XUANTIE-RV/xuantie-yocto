# GCC 15 (C23): bool 是关键字，gdbm 1.23 的 tools/var.c 将 bool 用作
# 枚举成员名导致 "'bool' cannot be used here" 错误。
# 回退到 C17 标准使 bool 重新成为宏而非关键字。
CFLAGS:append = "${@' -std=gnu17' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
