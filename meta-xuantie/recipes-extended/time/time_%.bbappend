# GCC 15: -Wincompatible-pointer-types 默认升级为 error
# time 1.9 的 sighandler 类型 void(*)(void) 与 signal() 期望的 void(*)(int) 不兼容
CFLAGS:append = "${@' -Wno-error=incompatible-pointer-types' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
