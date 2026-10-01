# openpgm 5.3.128（新建，backport）

[linux-sdk-ai-expert]

## 为什么引入

工单 84841963（vllm deb 源补包）要求 zmq 的 PGM 多播支持
（`libpgm-5.3.so.0`，pkg-config 名 openpgm-5.3）。zeromq 4.3.5 的
`-DWITH_OPENPGM=ON` 依赖它，树内无该 recipe，从 meta-oe 上游 backport。

## 交叉编译适配（do_configure 三处）

1. `CACHED_CONFIGUREVARS` 预置 `ac_cv_file__proc_cpuinfo=yes
   ac_cv_file__dev_rtc=no ac_cv_file__dev_hpet=no`：configure 用
   AC_CHECK_FILE 探测宿主路径，交叉编译必然失败，必须缓存绕过
2. do_configure:prepend 复制 `openpgm-5.2.pc.in` → `openpgm-5.3.pc.in`：
   上游 release-5-3-128 归档只带 5.2 的 pc.in，而 AC_CONFIG_FILES 按
   RELEASE_INFO=5.3 要 openpgm-5.3.pc，不补则 config.status 报错
3. `0001-cpu.c-guard-x86-only-cpuid-helpers-...patch`：cpu.c 的
   `__cpuidex`/`_xgetbv` 辅助函数含 x86 内联汇编（`=a` 约束），但未被
   架构宏保护，riscv64 编译直接报 invalid output constraint；x86 版
   pgm_cpuid 本就有 `__i386__||__x86_64__` 保护且 #else 有通用回退，
   补丁把辅助函数并入同一保护块
4. do_install:append 把安装头文件里的裸 `restrict` 关键字改写为
   `__restrict`：裸 restrict 在 C 合法，但本 DISTRO 默认 clang 工具链下
   C++ 消费者（zeromq）包含 pgm 头文件会报
   `redefinition of parameter 'restrict'`；`__restrict` 是 gcc/clang
   C/C++ 通用拼写

## 产物

- libpgm-5.3.so.0 + openpgm-5.3.pc
- DEPENDS = perl-native（上游构建脚本需 perl）

## 编译参数

走 Yocto 默认 flags（ABI 稳定运行时库，不做 per-mcpu 出包）。
