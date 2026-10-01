# mysql 8.4.11（玄铁 Yocto 版）编译参数说明

按 `docs/superpowers/specs/2026-08-05-new-package-import-implementation-spec.md` §6 模板记录本包的编译参数选择。本文件随 recipe 同目录存放，不进镜像。

## 一、包来源

| 项 | 值 |
|---|---|
| 生效配方 | `meta-xuantie/recipes-devtools/mysql/mysql_8.4.11.bb` + `mysql/`（4 个交叉构建 patch） |
| 版本来源 | 官方 LTS tarball `https://cdn.mysql.com/Downloads/MySQL-8.4/mysql-8.4.11.tar.gz`（sha256 `eb305116...f1394c2`） |
| 旧版处置 | 原 `mysql_8.4.3.bb` 为内部 gerrit **预编译二进制**包（`MySQL-prebuilt.git`，8.4.3-zhijiang 分支），**保留共存**；版本经 `PREFERRED_VERSION_mysql` 选择（xuantie-base.inc 默认 `8.4.11` 源码构建，local.conf 覆盖为 `8.4.3` 即回退 prebuilt 二进制） |
| LICENSE 修正 | 旧 recipe 错标 MIT，改为源码实际的 `GPL-2.0-only`（`LIC_FILES_CHKSUM` 核对 tarball 内 LICENSE） |
| C 库 | 坚持 glibc，musl 不支持（spec §4.4） |

## 二、原生配置来源（上游 cmake 自带，原样保留）

- 顶层 `CMakeLists.txt:115-158`：未显式指定 `CMAKE_BUILD_TYPE` 时默认 **RelWithDebInfo**，其优化/调试级别（cmake 原生 `-O2 -g -DNDEBUG`）为上游设定，非 Yocto 注入。
- `WITH_DEFAULT_COMPILER_OPTIONS` 默认 ON → `cmake/build_configurations/compiler_options.cmake`（上游告警/编译选项集）。
- 编译行上游原生参数实证（do_compile 日志）：`-std=c++20 -fno-omit-frame-pointer -ffp-contract=off -ftls-model=initial-exec`。
- bundled 依赖（`extra/protobuf`（protobuf-24.4）、`extra/libedit`、`extra/lz4`、`extra/icu` 等）按上游原生静态配置 in-tree 构建（spec §2：服务包走上游原生 bundled 静态依赖路径），与主包共用同一 cmake toolchain 文件。

## 三、注入参数及理由

1. **`-mcpu=<CPU型号>`**（如 `-mcpu=xt-c9501fdvkmt`）：
   路径 = recipe `mysql_get_mcpu()` → `TUNE_CCARGS:append:riscv64/riscv32` → `TARGET_CC_ARCH`/`HOST_CC_ARCH` → cmake.bbclass `OECMAKE_C_FLAGS`/`OECMAKE_CXX_FLAGS` → 生成的 toolchain 文件 `CMAKE_C_FLAGS/CXX_FLAGS`。
   必须 recipe 级补回：继承 `xuantie-set-package-arch-with-mcpu` 后 `XUANTIE_PACKAGE_ARCH_SET=YES`，`xuantie-base.inc` 的全局 `TUNE_CCARGS:append` 对本 recipe 失效（同 openssl-xuantie 先例，与 nginx README §三.1 同一机制）。
   **bundled 穿透**：protobuf 等 in-tree 子目录与主包共用同一 toolchain 文件，天然同吃 `-mcpu`（编译日志 grep 实证）。
2. **`FULL_OPTIMIZATION = ""`**：不注入 Yocto 默认优化参数（`-O2` 等），mysql cmake 原生 build type（RelWithDebInfo）自带的优化级别生效（spec §4.4）。security_flags.inc 加固参数（`-fstack-protector-strong -D_FORTIFY_SOURCE=2 -Wformat*`）经 `TARGET_CC_ARCH` 进入，属发行版安全加固、非优化参数，与 redis 一致保留。
3. **交叉构建 host 工具（qemu usermode）**：`-DCMAKE_CROSSCOMPILING_EMULATOR=qemu-riscv64;-L;${STAGING_DIR_TARGET}`（`DEPENDS qemu-native`）。mysql 构建期需运行目标架构代码生成器（comp_err / comp_sql / gen_lex_hash / gen_lex_token / protoc）；cmake 对 `add_custom_command` 中的 target 名自动代换全路径并前置 emulator，`-L` 指向目标 sysroot 解析 glibc 等运行时库。
4. **其余 `EXTRA_OECMAKE`**：
   - `-DWITH_PACKAGE_FLAGS=0`：上游 DEB/RPM standalone 打包 flag 体系（`CMakeLists.txt:710-748`），交叉构建本不触发，防御性关闭；
   - `-DWITH_UNIT_TESTS=OFF`、`-DINSTALL_MYSQLTESTDIR=`：测试套件不构建不进包；
   - `-DINSTALL_MYSQLDATADIR=${localstatedir}/lib/mysql`、`-DSYSCONFDIR=${sysconfdir}`：对齐 OE 布局。

## 四、patch 适配结论（2 个，均为交叉构建专用）

- **0001 mysys: run build_id_test via CMAKE_CROSSCOMPILING_EMULATOR**
  `verify_build_id.cmake` 以 POST_BUILD `cmake -P` 脚本模式执行，脚本模式不加载 toolchain 文件、不知 emulator，直接在 build host 执行目标二进制失败（`ELF ...: not found` → `Bad build id`）。显式把 `CMAKE_CROSSCOMPILING_EMULATOR` 传入脚本并在调用 `./build_id_test` 时前置。
- **0002 utilities: build comp_err when cross compiling with emulator**
  `comp_err` 被 `IF(NOT CMAKE_CROSSCOMPILING)` 包住（`utilities/CMakeLists.txt:33`），上游交叉模型期望 PATH 中存在 host 版 comp_err（8.4 已删除旧 `IMPORT_EXECUTABLES` 机制）；生成 `mysqld_error.h` 等的 custom command 以裸名调用 → `comp_err: not found`（首个编译失败实证）。本工程走 emulator 路线：有 `CMAKE_CROSSCOMPILING_EMULATOR` 时照常将 comp_err 构建为 target，custom command 引用 target 名，cmake 自动代换路径 + 前置 qemu + 自动补 target 级依赖。无 emulator 的交叉构建保持上游语义。
- **0003 plugin/x: run protoc-gen-yplg through emulator wrapper**
  mysqlx 用 protoc（目标二进制，已由 emulator 拉起）加 `--plugin=protoc-gen-yplg=$<TARGET_FILE:xprotocol_plugin>` 生成代码；protoc 对插件路径直接 `execve()`，qemu usermode 不再仿真子进程 → `xprotocol_plugin: program not found or is not executable`（第二次编译失败实证）。有 emulator 时由 `FILE(GENERATE)` 生成重入 emulator 的 wrapper 脚本作为 `--plugin` 路径；native 构建保持直用插件 target 文件。
- 三个 patch 均 `Upstream-Status: Inappropriate [cross compilation specific]`。spec §4.4 关切的 rv64 lock-free/atomic 适配：8.4.11 上游原生覆盖，无需本地 patch。

## 五、禁用项及原因

- **单元测试不构建**：`WITH_UNIT_TESTS=OFF` + `INSTALL_MYSQLTESTDIR=`（交叉构建无法运行测试，减少构建目标与包体积）。
- **musl**：不支持，坚持 glibc（spec §4.4）。
- **不采用 host-tools/IMPORT 路线**：8.4.11 已无 `IMPORT_EXECUTABLES`，统一走 qemu emulator 路线（见 §三.3），无需 mysql-native 伴生包。

## 六、特殊处理

- `INSANE_SKIP:${PN} += "file-rdeps"`：mysql 自带 perl/shell 脚本引用运行时模块，QA 无法静态解析。
- `RDEPENDS` 显式声明 `libssl libcrypto libtirpc perl ncurses`；`inherit binconfig` 处理 `mysql_config`。

## 七、验证要点

- `bitbake mysql -e | grep ^PACKAGE_ARCH=` → CPU 型号名（`xt-c9501fdvkmt`）；`^PV=` → `8.4.11`；`^FULL_OPTIMIZATION=` → 空。
- do_compile 日志中 bundled protobuf 编译行含 `-mcpu=`（关键验证点），且无 Yocto `-O2` 注入痕迹（RelWithDebInfo 原生 `-O2` 除外）。
- `readelf -A <主二进制> | grep Tag_RISCV_arch` → 与 `-mcpu` 隐含展开一致。
- `tmp/deploy/deb/xt-c9501fdvkmt/` 存在 mysql `.deb`。
