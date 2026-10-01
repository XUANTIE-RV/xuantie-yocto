# aibench

玄铁 AI 推理基准测试套件,按 CPU 型号(c907/c908/c920v2/c920v3/r908…)选择对应模型目录构建。

## 构建方式

- Recipe:`aibench.bb`,autotools + python do_compile(按 `XUANTIE_MCPU` 选模型子目录)
- 继承 `xuantie-set-package-arch-with-mcpu-benchmark`:per-mcpu 打包,deb 落 `deploy/deb/<mcpu>/`
- recipe 追加 `CFLAGS += "-Wno-implicit-function-declaration"`

## 构建参数

| 参数 | 值 | 来源 |
|---|---|---|
| -O2 | 有 | 类默认 `XUANTIE_BENCH_OPTFLAGS = "-O2"`(未置空);另模型子目录 Makefile 自带 `CFLAGS += -O2` |
| -mcpu | `-mcpu=<XUANTIE_MCPU>` | 类注入,CFLAGS/LDFLAGS 均带 |
| -static | 有,LDFLAGS | 类注入 |
| -Wno-* | `-Wno-implicit-function-declaration` + 类注入的兼容项 | recipe + 类 |

## 说明

- python do_compile 依据 `XUANTIE_MCPU` 选择 `c907/c908/c920v2/c920v3/r908…` 模型目录,
  模型 Makefile 内部自带 `-O2`
- 属于 overlay 包集(`PKGSET_overlay_aibench`),仅 aibench 变体镜像携带
