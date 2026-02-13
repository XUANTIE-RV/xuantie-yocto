CFLAGS:toolchain-clang:append = " -Wno-incompatible-function-pointer-types -Wno-implicit-function-declaration"
CXXFLAGS:toolchain-clang:append = " -Wno-register -Wno-enum-constexpr-conversion -Wno-c++11-narrowing-const-reference -Wno-cast-function-type-strict"
