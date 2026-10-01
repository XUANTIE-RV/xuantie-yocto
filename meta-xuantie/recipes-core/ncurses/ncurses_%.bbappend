# GCC 15: ncurses 将 NCURSES_BOOL 定义为 unsigned char，与 GCC 15 的
# C++ type_traits 模板特化冲突（redefinition of __is_integral_helper）
# 同时存在 void* 到 unsigned char 的隐式转换问题
CFLAGS:append = "${@' -std=gnu17' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
CXXFLAGS:append = "${@' -fpermissive' if (d.getVar('GCC_VERSION') or '').startswith('15') else ''}"
