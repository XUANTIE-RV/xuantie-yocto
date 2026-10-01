# linpack

LINPACK 线性方程组求解基准测试,一次构建 4 个变体:
单精度/双精度 × 展开/不展开(SP/DP × UNROLL/ROLL)。

## 构建方式

- Recipe:`linpack_1.0.bb`,autotools + 自定义 do_compile:
  ```
  ${CC} ${CFLAGS} ${LDFLAGS} -DDP -DUNROLL -o linpack_dp_unroll ${WORKDIR}/linpack.c -lm
  ${CC} ${CFLAGS} ${LDFLAGS} -DSP -DUNROLL -o linpack_sp_unroll ${WORKDIR}/linpack.c -lm
  ${CC} ${CFLAGS} ${LDFLAGS} -DDP -DROLL   -o linpack_dp        ${WORKDIR}/linpack.c -lm
  ${CC} ${CFLAGS} ${LDFLAGS} -DSP -DROLL   -o linpack_sp        ${WORKDIR}/linpack.c -lm
  ```
  (clang 工具链只编 DP-ROLL/SP-ROLL 两个变体)
- 继承 `xuantie-set-package-arch-with-mcpu-benchmark`:per-mcpu 打包,deb 落 `deploy/deb/<mcpu>/`

## 构建参数

| 参数 | 值 | 来源 |
|---|---|---|
| -O2 | 有 | 类默认 `XUANTIE_BENCH_OPTFLAGS = "-O2"` |
| -mcpu | `-mcpu=<XUANTIE_MCPU>` | 类注入 CFLAGS/LDFLAGS |
| -static | 有 | 类 LDFLAGS + recipe `LDFLAGS:append = " -static"` |
| -std=gnu17 | 有 | recipe `CFLAGS:append` |
| -frename-registers | gcc 工具链时追加 | recipe python 逻辑 |
| -DDP/-DSP -DUNROLL/-DROLL | 变体宏 | do_compile 写死 |

## 说明

- 上游源码(linpack.c)无自带 -O 级别,优化级别由类注入的 -O2 提供
