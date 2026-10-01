# GCC 15: K&R 无原型函数声明与系统头文件类型冲突
# (conflicting types for 'gmtime'/'localtime')
CFLAGS:append = "${@' -std=gnu17' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
CFLAGS:append = "${@' -Wno-error=implicit-function-declaration' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
