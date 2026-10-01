inherit xuantie-set-package-arch-with-mcpu


# Keep ${TOOLCHAIN_OPTIONS} (= --sysroot=${STAGING_DIR_TARGET}) — without it
# the cross compiler cannot find DEPENDS' headers under recipe-sysroot.
CC  = "${CCACHE}${HOST_PREFIX}gcc ${XUANTIE_TUNE_CCARGS_GENERIC} ${TOOLCHAIN_OPTIONS}"
CXX = "${CCACHE}${HOST_PREFIX}g++ ${XUANTIE_TUNE_CCARGS_GENERIC} ${TOOLCHAIN_OPTIONS}"
CC:toolchain-clang  = "${CCACHE}${HOST_PREFIX}clang ${XUANTIE_TUNE_CCARGS_GENERIC} ${TOOLCHAIN_OPTIONS}"
CXX:toolchain-clang = "${CCACHE}${HOST_PREFIX}clang++ ${XUANTIE_TUNE_CCARGS_GENERIC} ${TOOLCHAIN_OPTIONS}"

# Optimization level, kept at the -O2 this class used to hardcode. A benchmark
# that wants the pure flag set (cpu args + -Wno-* + -static) opts out with
# XUANTIE_BENCH_OPTFLAGS = "" in its own recipe; another level goes in the same
# way, e.g. "-O3". This class also clears FULL_OPTIMIZATION, so opting out means
# no -O at all unless the recipe's own build system supplies one.
XUANTIE_BENCH_OPTFLAGS ?= "-O2"

# 不吃 OE 嵌入 -O2 的 benchmark:集中在此以 :pn-xxx 置空,优化级别完全由
# 上游构建系统决定。新增同类需求只在这里加一行,不改 recipe。
XUANTIE_BENCH_OPTFLAGS:pn-lzbench = ""
XUANTIE_BENCH_OPTFLAGS:pn-lzbench-xuantie-test = ""
XUANTIE_BENCH_OPTFLAGS:pn-memtier-benchmark = ""
XUANTIE_BENCH_OPTFLAGS:pn-cpufp = ""
XUANTIE_BENCH_OPTFLAGS:pn-regex-benchmark = ""
# 以下 benchmark 优化级别由上游构建系统原生承担(gapbs/mem-barrier-bench
# Makefile -O3、spinlock Makefile -O2、cpubench SPEC 模式目标机编译),
# 统一在类内置空,recipe 不写 XUANTIE_BENCH_OPTFLAGS。
XUANTIE_BENCH_OPTFLAGS:pn-gapbs = ""
XUANTIE_BENCH_OPTFLAGS:pn-cpubench = ""
XUANTIE_BENCH_OPTFLAGS:pn-mem-barrier-bench = ""
XUANTIE_BENCH_OPTFLAGS:pn-spinlock = ""

# Include XUANTIE_TUNE_CCARGS_GENERIC in CFLAGS/CXXFLAGS so CMake toolchain file
# receives the correct -march/-mabi/-mcpu flags for cross-compilation
CFLAGS = " ${XUANTIE_BENCH_OPTFLAGS} ${XUANTIE_TUNE_CCARGS_GENERIC} ${XUANTIE_OPT_FLAGS}"
CXXFLAGS = " ${XUANTIE_BENCH_OPTFLAGS} ${XUANTIE_TUNE_CCARGS_GENERIC} ${XUANTIE_OPT_FLAGS}"
CFLAGS += " -Wno-implicit-int -Wno-deprecated-non-prototype -Wno-implicit-function-declaration -Wno-return-type -Wno-return-mismatch "
CXXFLAGS += " -Wno-implicit-int -Wno-deprecated-non-prototype -Wno-implicit-function-declaration -Wno-return-type -Wno-return-mismatch "

# mcpu 注入点:export 进 do_compile 环境,上游 Makefile 引用
# $(EXTRA_CFLAGS)/$(EXTRA_CXXFLAGS) 即可在编译行拿到 -mcpu=<核>,
# recipe 的 make 命令行不必写任何 flag。
EXTRA_CFLAGS   = "${XUANTIE_TUNE_CCARGS_GENERIC}"
EXTRA_CXXFLAGS = "${XUANTIE_TUNE_CCARGS_GENERIC}"
export EXTRA_CFLAGS
export EXTRA_CXXFLAGS

# LDFLAGS also needs XUANTIE_TUNE_CCARGS_GENERIC for correct multilib linking
LDFLAGS = " ${XUANTIE_BENCH_OPTFLAGS} -static ${XUANTIE_TUNE_CCARGS_GENERIC}"
FULL_OPTIMIZATION = ""


