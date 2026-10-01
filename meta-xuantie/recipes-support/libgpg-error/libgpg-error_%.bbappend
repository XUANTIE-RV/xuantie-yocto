# GCC 15 (C23): nullptr 成为关键字，libgpg-error 测试代码将其用作标识符
CFLAGS:append = "${@' -std=gnu17' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
