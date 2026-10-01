# stream

经典内存带宽基准测试(Copy / Scale / Add / Triad)。

## 构建方式

- Recipe:`stream_5.10.bb`,pkgconfig + autotools,自定义 do_compile:
  `oe_runmake CC="${CC}" FC="${FC}"`
- clang + glibc 变体:`FC` 换 `flang-new`(do_compile:toolchain-clang:libc-glibc)
- 继承 `xuantie-set-package-arch-with-mcpu-benchmark`:per-mcpu 打包,deb 落 `deploy/deb/<mcpu>/`

## 构建参数

| 参数 | 值 | 来源 |
|---|---|---|
| -O2 | 有(双来源) | 类默认 `XUANTIE_BENCH_OPTFLAGS = "-O2"`;上游源码 Makefile 自带 -O2 |
| -mcpu | `-mcpu=<XUANTIE_MCPU>` | 类注入,随 CC/环境 flags 进 make |
| -static | 有 | 类 LDFLAGS |
| -Wno-* | 类注入的兼容项 | 类 |

## 说明

- 上游 Makefile 自带 -O2,与类注入并存,gcc 取最后出现的 -O,级别不变
- Fortran 版本(FC)仅影响 stream_f 变体;clang 工具链下用 flang-new
