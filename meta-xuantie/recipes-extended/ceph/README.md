# ceph 18.2.1 编译参数选择记录

迁移自 yocto-dev-vcpu 已验证 recipe（patch 集原样，零新增源码改造），设计依据：
`docs/superpowers/specs/2026-08-05-new-package-import-implementation-spec.md` §4.5 / §5。

## 1. 原生配置来源

- 构建体系：ceph 上游 cmake（`src/CMakeLists.txt`），优化级别/告警/语言标准全部沿用上游默认，
  recipe 不注入 `-O2`/`-O3`，不加玄铁优化开关（`XUANTIE_OPT_LOOP/_INLINE/_STRUCT`）。
- patch 集：vcpu 已验证的 29 个 patch 原样迁移。其中 riscv64 可编译性来自
  `0042-add-riscv64-support-for-ceph-18.2.1.patch` 的 3 处 7 行
  （arrow `SetupCxxFlags.cmake` 处理器识别——缺失会把 x86 SIMD flag 灌进 riscv 编译；
  `fast_float/float_common.h` 64 位识别；`common/Cycles.h` rdtime 实现），
  属「必要的编译器兼容」范畴，必须保留。

## 2. 注入参数及理由

- `-mcpu=<CPU型号>` 双通道：
  1. toolchain flags 通道：CPU mode DISTRO 全局 `TUNE_CCARGS = -mcpu=<型号>` →
     `OECMAKE_C_FLAGS = HOST_CC_ARCH + TOOLCHAIN_OPTIONS + CFLAGS` 写入 toolchain.cmake。
  2. compiler 变量通道（选项 A，为 bundled rocksdb 而设）：
     `OECMAKE_C_COMPILER`/`OECMAKE_CXX_COMPILER` 在 vcpu 现有 `--sysroot=${RECIPE_SYSROOT}`
     手法上补 `${HOST_CC_ARCH}`，把 `-mcpu` 烧进 compiler 变量。
- rocksdb flag 覆盖因果（spec §5 证据链）：`cmake/modules/BuildRocksDB.cmake` 转发
  toolchain 文件与 CXX compiler，但其对 GCC 必然追加
  `-DCMAKE_C_FLAGS=-Wno-stringop-truncation` / `-DCMAKE_CXX_FLAGS='-Wno-deprecated-copy
  -Wno-pessimizing-move'`；cmake.bbclass 的 toolchain 文件 `set(... CACHE STRING ...)`
  不带 FORCE，命令行 `-D` 先入缓存压制 toolchain 值，`-mcpu` 随 CFLAGS 整体丢失。
  `oecmake_map_compiler` 只取 `CC` 首词（注释明写 "without cpu arch/tune arguments"），
  compiler 变量天然不含 `-mcpu`；`-DPORTABLE=ON` 又使 rocksdb 不注入 `-march=native`。
  净效果：不处理则 rocksdb 目标文件退回工具链默认 rv64gc。选项 A 后 `-mcpu` 经
  compiler 变量通道进入 rocksdb 子构建，不受 flags 覆盖影响。
- 兼容参数（vcpu 现状，原样保留）：`LDFLAGS += -Wl,--no-as-needed -latomic -Wl,--as-needed`
  （rv64 部分原子操作需 libatomic）；`inherit xuantie-clang-compatible`
  （clang 下的 `-Wno-*` 兼容告警抑制，与 vcpu `xuantie-clang18-compatible` 内容一致）。

## 3. 禁用项及原因（照 vcpu 现状）

- `WITH_SYSTEM_BOOST=ON`：不编 bundled boost，走系统 boost 1.80（CPU mode 下经全局
  TUNE_CCARGS 天然带 `-mcpu`）；`0032-BuildBoost.patch` 在此配置下不生效，无需处理。
- `WITH_SYSTEM_ROCKSDB=OFF`：使用 bundled rocksdb（ceph 对 rocksdb 有私有 patch，
  系统 rocksdb 7.5.3 不满足），`-mcpu` 继承见 §2。
- `WITH_MGR=OFF` / `WITH_MGR_DASHBOARD_FRONTEND=OFF`：dashboard 依赖 node 前端构建链。
- `WITH_SPDK=OFF`、`WITH_RDMA=OFF`：SPDK/DPDK 与 RDMA 硬件栈在目标平台不可用。
- `WITH_FUSE=OFF`：不交付 ceph-fuse（do_install 同步删除 ceph-fuse systemd unit）。
- `WITH_LTTNG=OFF`/`WITH_BABELTRACE=OFF`、`WITH_TESTS=OFF`、`WITH_MANPAGE=OFF`、
  `WITH_JAEGER=OFF`、`WITH_RADOSGW_*`=OFF：裁剪运行时/构建依赖，照 vcpu 已验证配置。

## 4. 特殊处理

- per-mcpu 出包：`inherit xuantie-set-package-arch-with-mcpu`，
  `PACKAGE_ARCH = <CPU型号>`，.deb 落 `tmp/deploy/deb/<CPU型号>/`。
- 版本隔离：`xuantie-base.inc` 加 `PREFERRED_VERSION_ceph = "18.2.1"`，
  压制 meta-virtualization 的 ceph 15.2.15。
