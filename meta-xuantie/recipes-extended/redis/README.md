# redis 8.10.0 编译参数说明

以 meta-oe master `redis_8.8.1.bb` 为底 bump 至 8.10.0，按 spec《2026-08-05-new-package-import-implementation-spec》§2 flag 原则：上游原生配置保留，仅注入 `-mcpu` 与必要兼容参数，不加 Yocto 优化参数、不加玄铁优化开关。

## 1. 原生配置来源（上游构建系统自带，原样保留）

| 位置 | 原生设定 |
|---|---|
| `src/Makefile:28` | `OPTIMIZATION?=-O3`（redis 8.x 默认 -O3） |
| `src/Makefile:39` | `OPTIMIZATION+=-fno-omit-frame-pointer`（非 -O0 时） |
| `src/Makefile:45` | `STD=-pedantic -DREDIS_STATIC=''` |
| `src/Makefile:57` | `WARN=-Wall -W -Wno-missing-field-initializers -Werror=deprecated-declarations -Wstrict-prototypes` |
| `deps/hiredis/Makefile:44-47` | `OPTIMIZATION?=-O3`、`WARNINGS=-Wall -Wextra -Werror ...`、`REAL_CFLAGS=$(OPTIMIZATION) -fPIC $(CFLAGS) ...` |
| `deps/lua/src/Makefile` | 上游 lua 5.1 原生构建（经 0002 patch 改吃环境 CC/CFLAGS） |
| `deps/linenoise/Makefile` | `OPT= -Os`、`WARN= -Wall` |
| `deps/hdr_histogram/Makefile:1` | `STD= -std=c99` |

`OPTIMIZATION`/`WARN`/`STD` 全部走上游默认值，recipe 不覆盖 `OPT`/`OPTIMIZATION`，不注入 Yocto `FULL_OPTIMIZATION`。

## 2. 注入参数及理由

- **`-mcpu=<CPU型号>`**：CPU mode DISTRO 下全局 `TUNE_CCARGS = -mcpu=<型号>` 生效；recipe 保留 7.0.5 的 `python()` 扩展段（非 c9xx 型号时把 `-mcpu=` 追加进 `TUNE_CCARGS`），经 OE `CC = gcc ${HOST_CC_ARCH} --sysroot=...` 进入编译行。
- **bundled deps 继承 `-mcpu`**：
  - hiredis：0001 patch（`hiredis-use-default-CC`）使 hiredis 不自行猜测 CC，直接用环境 `CC`（内含 `-mcpu`）；deps/Makefile 另以 `HIREDIS_CFLAGS="$(DEPS_CFLAGS)"` 转发环境 CFLAGS。
  - lua：0002 patch 使 lua Makefile 使用环境 `CC`/`CFLAGS`。
  - linenoise / hdr_histogram / fpconv：其 Makefile 原生 `R_CC=$(CC)` / `$(CC)`，环境 `CC` 天然带 `-mcpu`。
- **`TARGET_LDFLAGS:append = " ${DEBUG_PREFIX_MAP}"`**：上游 8.8.1 recipe 原生行，调试用路径映射，非优化参数。
- **0003 patch（oe-use-libc-malloc）**：强制 `uname_S := "USE_LIBC_MALLOC"` 走 libc malloc 路径——jemalloc 在 riscv64 上构建有问题（沿用 7.0.5 以来的决策）。
- **0004 patch（FINAL_LIBS+=-lm）**：不重置环境传入的 `FINAL_LIBS`（recipe 为 x86/mips/arm/powerpc/riscv32 导出的 `-latomic` 依赖它存活）；8.10.0 因新增 `BUILD_COMPRESSION` 块导致上下文漂移，已手工重刷（`FINAL_LIBS=-lm` 在 8.10.0 的 src/Makefile:154）。
- **0005 patch（_GNU_SOURCE）**：`zmalloc.c` 需 `_GNU_SOURCE` 取得 `PTHREAD_MUTEX_INITIALIZER`，属编译兼容。

## 3. 禁用项及原因

- **Redis modules（redisbloom/redisearch/redisjson/redistimeseries）不构建**：8.10.0 tarball 自带 `modules/*/src`，顶层默认 `build` 目标会逐个编译它们（需 Rust 工具链，本基线 Xuantie-900 GCC V3.5.0-beta 不满足）。recipe `do_compile` 显式用 `oe_runmake build none`（经 `scripts/build.sh none` 只编 `src/` 核心；`make install` 顶层只递归 src，模块不进安装）。
- **jemalloc**：见 0003 patch 说明，强制 libc malloc。
- **`0006-Define-correct-gregs-for-RISCV32.patch` 不再携带**：8.10.0 上游 `src/debug.c` 已原生覆盖 RISC-V——`getAndSetMcontextEip()` 有 `#elif defined(__riscv)` 分支（`__gregs[REG_PC]`），`logRegisters()` 有通用 `__riscv` 分支，riscv32/riscv64 均适用，本地 patch 失去存在意义且上下文已不适用。

## 4. 特殊处理

- `redis.conf` / `init-redis-server` / `redis.service` 从主树 7.0.5 的 `redis/` 目录平移（redis.conf 与上游 8.8.1 逐字节一致；service/init 脚本保持本地已验证版本）。
- 本 oe-core 基线无 `UNPACKDIR`，`do_install` 沿用 `${WORKDIR}`（上游 8.8.1 用 `${UNPACKDIR}`）。
- 选用 `PREFERRED_VERSION_redis = "8.10.0"`（`meta-xuantie/conf/distro/include/xuantie-base.inc`）压过 meta-oe 本地改造的 7.0.5。

## 5. 许可证变更（合规提示）

7.4 起 redis 由 BSD-3-Clause 变更为 **AGPL-3.0-only OR RSAL-2 OR SSPL-1**（使用者三选一）。`LIC_FILES_CHKSUM` 核对 8.10.0 tarball 内 `LICENSE.txt`（md5 `9f1da87999fb281d8a9e3e3b2d0403b4`，与上游 8.8.1 一致）。随镜像出货/设备内部使用三证均允许；修改后对外提供网络服务时 RSAL-2/SSPL-1 禁止 DBaaS、AGPL 需公开修改源码。**合入前需法务评审通过**（spec §7）。
