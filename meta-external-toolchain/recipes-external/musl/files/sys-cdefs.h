/* Minimal sys/cdefs.h stub for musl libc compatibility.
 * glibc provides this header with various compiler attribute macros.
 * Most packages only need __BEGIN_DECLS/__END_DECLS which musl
 * does not provide. Provide no-op stubs here. */
#ifndef _SYS_CDEFS_H
#define _SYS_CDEFS_H

#ifdef __cplusplus
#define __BEGIN_DECLS extern "C" {
#define __END_DECLS }
#else
#define __BEGIN_DECLS
#define __END_DECLS
#endif

#ifndef __THROW
#define __THROW
#endif

#ifndef __nonnull
#define __nonnull(params)
#endif

#endif /* _SYS_CDEFS_H */
