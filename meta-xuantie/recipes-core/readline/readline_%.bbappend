# GCC 15 (C23): readline 8.1 的 signals.c 在 void 函数中 return 值
# (-Wreturn-mismatch 在 GCC 15 升级为 error)
CFLAGS:append = "${@' -std=gnu17 -Wno-error=return-mismatch' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
