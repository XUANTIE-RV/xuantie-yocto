/*
 * Workaround for rv32 uclibc-ng toolchain missing section symbols.
 *
 * The __uClibc_main.os in the prebuilt rv32 uclibc-ng toolchain was
 * compiled without -DSHARED, causing it to reference these section
 * symbols even when building shared libraries.
 *
 * This linker script provides weak aliases to empty arrays as a
 * temporary workaround until the toolchain is rebuilt with the fix.
 *
 * Root cause: Makerules in uclibc-ng compiles .os files with only
 * -fPIC but not -DSHARED, while __uClibc_main.c uses #ifndef SHARED
 * to guard the extern declarations of these symbols.
 */

/* Provide weak references to empty arrays for the missing symbols */
PROVIDE (__preinit_array_start = .);
PROVIDE (__preinit_array_end = .);
PROVIDE (__init_array_start = .);
PROVIDE (__init_array_end = .);
PROVIDE (__fini_array_start = .);
PROVIDE (__fini_array_end = .);
