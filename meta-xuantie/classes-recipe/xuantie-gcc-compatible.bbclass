# Suppress GCC warnings that break builds across GCC versions (14, 15, ...)
# -Wno-unknown-warning-option ensures forward compatibility with newer GCC versions
# that may not recognize flags added for older versions.
CFLAGS += " -Wno-incompatible-pointer-types -Wno-calloc-transposed-args -Wno-deprecated-declarations -Wno-maybe-uninitialized -Wno-builtin-declaration-mismatch -Wno-implicit-function-declaration -Wno-pointer-to-int-cast -Wno-implicit-int -Wno-unused-result -Wno-unknown-warning-option"
CXXFLAGS += " -Wno-dangling-reference -Wno-unknown-warning-option -Wno-calloc-transposed-args -Wno-dangling-pointer"
