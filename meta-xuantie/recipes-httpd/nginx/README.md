# nginx 1.30.4（玄铁 Yocto 版）编译参数说明

按 `docs/superpowers/specs/2026-08-05-new-package-import-implementation-spec.md` §6 模板记录本包的编译参数选择。本文件随 recipe 同目录存放，不进镜像。

## 一、包来源

| 项 | 值 |
|---|---|
| 生效配方 | `meta-xuantie/recipes-httpd/nginx/nginx_1.30.4.bb` + `nginx.inc` + `files/` |
| 上游底本 | meta-openembedded meta-webserver **master** 的 nginx 1.30.4 recipe（backport） |
| 版本锁定 | `xuantie-base.inc` 中 `PREFERRED_VERSION_nginx = "1.30.4"` |
| 旧版处置 | meta-webserver 自带 1.20.1 / 1.21.1；1.21.1 上游本就 `DEFAULT_PREFERENCE="-1"` 禁用，PREFERRED_VERSION 直接选中本层 1.30.4 |
| 依赖变化 | `DEPENDS` 由 libpcre 转为 **libpcre2**（上游 master 已改好，本树 openembedded-core 提供 libpcre2 10.40） |

## 二、原生配置来源（上游构建系统自带，原样保留）

nginx 用自家 `auto/` configure 体系，优化级别与告警不由 Yocto CFLAGS 决定：

- `auto/cc/gcc:115` `CFLAGS="$CFLAGS $PIPE $CPU_OPT"`，`:133-135` 追加
  `${NGX_GCC_OPT:--O} -W -Wall -Wpointer-arith`（`NGX_GCC_OPT="-O"`，`:45`）——
  即生成 Makefile 自带的 `-pipe -O -W -Wall -Wpointer-arith ...` 为 nginx 原生设定，保留
- `auto/cc/conf:144` `CFLAGS="$CFLAGS $NGX_CC_OPT"`：env CFLAGS 会被折入
  `objs/Makefile` 的 `CFLAGS=` 行；`--with-cc-opt` 的值（NGX_CC_OPT）追加其后
- `auto/make:214-222`：链接行由 `$(CC)` + `MAIN_LINK` + `NGX_LD_OPT $CORE_LIBS` 组成，
  `--with-ld-opt` 进入 NGX_LD_OPT

## 三、注入参数及理由（仅以下三项，无 Yocto -O2、无玄铁优化开关）

1. **`-mcpu=<CPU型号>`**（如 `-mcpu=xt-c9501fdvkmt`）：
   路径 = `XUANTIE_TUNE_CCARGS`（CPU mode 下为 `-mcpu=${XUANTIE_MCPU}`，arch mode 下为
   `-march/-mabi`）→ `--with-cc-opt` / `--with-ld-opt`。
   必须 recipe 级显式传递：继承 `xuantie-set-package-arch-with-mcpu` 后
   `XUANTIE_PACKAGE_ARCH_SET=YES`，`xuantie-base.inc` 的
   `TUNE_CCARGS:append` 对本 recipe 失效（只追加空），distro 全局 `-mcpu` 不进本包，
   由本 recipe 经 cc-opt/ld-opt 通道注入，编译行与链接行均覆盖。
2. **环境变量净化**：`do_configure` 在 `./configure` 前 `unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS`，
   防止 Yocto TARGET_CFLAGS（-O2、-fstack-protector、DEBUG_PREFIX_MAP 等）经
   `auto/cc/conf:144` 折入 Makefile 覆盖 nginx 原生 `-O`。
   **env CC 同样被净化**：nginx 把 CC 原样烧进 `objs/Makefile`（`$(CC) -c $(CFLAGS)`），
   而 distro CC 经 `TARGET_CC_ARCH` 携带 `security_flags.inc` 的
   `-fstack-protector-strong -O2 -D_FORTIFY_SOURCE=2 -Wformat*`（首轮构建实测编译行确有
   `-O2`，据此修复）。重建为 `gcc + --no-sysroot-suffix + --sysroot`。
3. **兼容参数（仅两项）**：
   - `-Wl,--hash-style=gnu`：Yocto LDFLAGS 被净化后链接行缺 GNU_HASH，会触发
     `insane.bbclass` 的 `ldflags` **ERROR_QA**（`QAPATHTEST[ldflags] = package_qa_hash_style`），
     加入 ld-opt。上游 recipe 用 `MAIN_LINK` 注入整串 Yocto LDFLAGS 解决同一问题，
     本包改为只加这一项。
   - `--no-sysroot-suffix`：外部 multilib 工具链驱动在 oe sysroot 查找 crt 文件的
     必要兼容参数（`meta-external-toolchain/tcmode-external.inc` `NO_SYSROOT_SUFFIX`，
     原属 `HOST_CC_ARCH`），随净化后的 CC 保留。

同时删除了上游的 `CFLAGS:append = " -fPIE -pie"` / `CXXFLAGS:append`（Yocto 侧 flag 注入，与纯洁性原则冲突；净化后本就不会生效）。

## 四、禁用项及原因

- **http_v3（HTTP/3 QUIC）不启用**：基线 OpenSSL 3.0.x 无 QUIC/0-RTT API
  （`SSL_provide_quic_data` 等 3.5+ 才有），`--with-http_v3_module` 无法配置；
  上游 recipe 本身也未提供该 PACKAGECONFIG，保持不启用。

## 五、backport 适配（相对 meta-webserver master 的偏差）

| 上游写法 | 本包写法 | 原因 |
|---|---|---|
| `inherit upstream-version-is-even` | 删除 | 本树 openembedded-core 无该 bbclass |
| `do_install` 中 `${UNPACKDIR}` | `${WORKDIR}` | 本树 bitbake 无 UNPACKDIR（旧版命名） |
| `CVE_STATUS[CVE-2026-*]` | 保留原样 | 本树 cve-check 不解析 CVE_STATUS，仅作文档，无副作用 |
| `MAIN_LINK` 注入 `${LDFLAGS}` | 删除，改 ld-opt 加 hash-style | flag 纯洁性，见 §三.3 |
| `CFLAGS/CXXFLAGS:append -fPIE -pie` | 删除 | 同上 |

## 六、验证要点

- `bitbake nginx -e | grep ^PACKAGE_ARCH=` → CPU 型号名（如 `xt-c9501fdvkmt`）
- `do_compile` 日志编译行含 `-mcpu=`，且无 `-O2`（nginx 原生为 `-O`）
- `readelf -A tmp/work/.../nginx/1.30.4-*/image/usr/sbin/nginx | grep Tag_RISCV_arch` → 与 -mcpu 展开一致
- `tmp/deploy/deb/<cpu型号>/nginx_1.30.4-*.deb` 存在
