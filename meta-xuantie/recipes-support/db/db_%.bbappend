# GCC 15 将旧式 K&R 函数定义视为错误，db 5.3.28 仍存在大量 K&R 风格代码。
# 通过 -std=gnu17 回退到 C17 标准，保留旧的无原型函数调用行为。
CFLAGS:append = "${@' -std=gnu17' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
