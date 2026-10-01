# coremark

EEMBC CoreMark 处理器性能基准测试(链表/矩阵/状态机综合),跑分结果为 iterations/sec。

## 构建方式

meta-oe 包 + 玄铁 bbappend 模式(**不接** `xuantie-set-package-arch-with-mcpu-benchmark` 类):

- 基 recipe:`meta-openembedded/meta-oe/recipes-benchmark/coremark/coremark.bb`
- 玄铁侧:`coremark.bbappend` + `coremark/0001-coremark-add-cflags.patch`
  (补丁把优化参数硬写进上游 Makefile)
- `EXTRA_OEMAKE += "TOOLCHAIN=${TOOLCHAIN}"`:上游 Makefile 按工具链选 flag 集

## 构建参数

补丁硬写进上游 Makefile 的 CFLAGS:

| 工具链 | CFLAGS |
|---|---|
| clang | `-O3 -static -DITERATIONS=10000 -mllvm -inline-threshold=500 -mllvm -jump-threading-threshold=0 -mllvm -enable-dfa-jump-thread=true` |
| gcc | `-static -O3 -funroll-all-loops -finline-limit=500 -fgcse-sm -DITERATIONS=10000 -msignedness-cmpiv -fno-code-hoisting -mno-thread-jumps1 -mno-iv-adjust-addr-cost -mno-expand-split-imm -fselective-scheduling -fgcse-las` |

关键参数:

- **-O3 -static**:优化级别与链接形态由 patch 提供;上游 Makefile `CFLAGS +=`
  追加在 OE 环境 CFLAGS(含 -O2)之后,gcc 以最后出现的 -O 为准,实际 **-O3**
- **-DITERATIONS=10000**:固定迭代次数,保证各平台跑分可比
- gcc 私有 flag(`-msignedness-cmpiv` 等 4 项)、clang 私有 pass flag
  (`-mllvm -riscv-default-unroll=false`)仅玄铁工具链支持,见下方适配逻辑

## bbappend 的适配逻辑

1. **gcc 私有 flag 试探**:do_configure 阶段对 `-msignedness-cmpiv`、
   `-mno-thread-jumps1`、`-mno-iv-adjust-addr-cost`、`-mno-expand-split-imm`
   逐个试编译,编译器不支持就从 Makefile sed 掉——开源 gcc 也能编
2. **clang 私有 pass**:vendor-xuantie + toolchain-clang 时补回
   `-mllvm -riscv-default-unroll=false`(开源 clang 18 不认该参数,补丁默认不带)
3. **xtstring 接线**:distro 开 `xtstring-opt` 时 `DEPENDS += libxtstring-muslc`、
   `LFLAGS_END += -lXTString`(字符串函数优化库)

## 与 mcpu-benchmark 类包的区别

coremark 未接类,不做 per-mcpu 打包(按 tune 归入对应架构目录);-O2 的取舍也
不走 `XUANTIE_BENCH_OPTFLAGS` 机制,而是靠 patch 的 -O3 覆盖 OE -O2。
