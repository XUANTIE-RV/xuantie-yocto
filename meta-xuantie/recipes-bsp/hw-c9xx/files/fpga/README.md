# XuanTie FPGA DTS / gdbinit 生成器

模板驱动的 DTS 与 gdbinit 生成工具,用于玄铁 RISC-V SoC 的 FPGA 验证。
所有硬件常量(基地址、寄存器偏移、boot 参数)集中在**平台 JSON**里,
模板(`.dtsi.in` / `.gdbinit.in`)只保留 `${MACRO}` / `##IF##` / `##REPEAT##`
占位,由 Python 引擎渲染。替代了旧的 `sed + symlink` 脚本流程。

> 运行环境:仅需 `python3`(FPGA 验证机自带)。`dtc` 与 `jsonschema` 为可选。

---

## 快速上手

一条命令生成本次验证所需的全部产物:

```bash
./update_dts_cpu.sh xiaohui_v1 -cpu c910 -layout 1111
```

生成三个文件:

| 产物 | 说明 |
|------|------|
| `.hw.dts`                     | 设备树源文件 |
| `gdbinit/zsb.gdbinit`         | 核 layout / flag / opensbi 加载 |
| `gdbinit/zsb_socinit.gdbinit` | link / net / pmc 初始化 |

先跑 `-h` 看全部选项:

```bash
./update_dts_cpu.sh -h
```

---

## 常用示例

> 位置参数写平台 JSON 名(`xiaohui_v1` / `xiaohui_v2` / `xiaohui_v1_rv32`);
> 也可省略/写旧名(`zsb_rv64` / `zsb_rv32`),此时平台由 `-cpu` 自动推导(见「工作原理」)。
> 下面示例的平台名均与该 CPU 自动推导的结果一致,写出来只为显式、可读
> (`xiaohui_v1_pa40` 例外:不在 CSV,必须显式写)。

> 每个生成示例下面配一条对应的 `run_fpga.sh` 上板命令(把 `<ip:port>` 换成实际
> HAPS 的 gdbserver 地址;加 `--simulate_run=true` 可只本地跑 dtc+gdb 冒烟不连板)。
> 下面用 `--system=<alias>` 一次性锁定 `rva_profile / abi_mode / opensbi_ver /
> kernel_ver / kernel_cfg / rootfs_type` 一整套(见 `runtime/system_alias_config.sh`),
> 免得逐个传参。现有 alias:
>
> | alias | profile | abi | opensbi | kernel | kcfg | 适用 |
> |-------|---------|-----|---------|--------|------|------|
> | `xuantie_rv64_std_kernel6_6_kcdefault`  | rva22 | rv64_std | 1.7 | 6.6 | default | RV64 v1 |
> | `xuantie_rv64_std_kernel6_6_kcdebug`    | rva22 | rv64_std | 1.7 | 6.6 | debug   | RV64 v1(debug 内核) |
> | `xuantie_rva23_std_kernel6_6_kcdefault` | rva23 | rv64_std | 1.7 | 6.6 | default | xiaohui_v2 |
> | `xuantie_rva23_std_kernel6_6_kcdebug`   | rva23 | rv64_std | 1.7 | 6.6 | debug   | xiaohui_v2(debug 内核) |
> | `xuantie_rv64_std_kernel5_10_kcdebug`   | rva22 | rv64_std | 0.9 | 5.10| debug   | RV64 v1(5.10 线) |
>
> （唯一源是 `runtime/system_alias_config.sh`;`run_fpga.sh -h` 会动态列出全部 alias。）
> profile 首选名为 `rva22`(原 `rva20`),`--rva_profile=rva20` 仍兼容可用。
> alias 只是把 `--rva_profile / --abi_mode / --opensbi_ver / --kernel_ver / --kernel_cfg /
> --rootfs_type` 打包,给了 `--system` 后仍可再单独传某个同名参数覆盖其中一项。
> 现有 alias 全是 `abi_mode=rv64_std`——**RV32 暂无 alias,直接传 `--abi_mode=rv32_std`**
> (其余走默认)。镜像路径不可自定义,
> 固定在 `run_fpga.sh` 同级的 `../images/`(rva23 用 `../images-rva23/`),
> 见下方[镜像目录约定](#run_fpga-镜像目录约定)。

```bash
# —— xiaohui_v1(RV64,c910/c908/c908x 等自动选)——
# c908,2G 内存
./update_dts_cpu.sh xiaohui_v1 -cpu c908 -layout 1111 -memsize 2G
./run_fpga.sh --target_remote=<ip:port> --system=xuantie_rv64_std_kernel6_6_kcdefault

# XuanTie Link + pro 网卡
./update_dts_cpu.sh xiaohui_v1 -cpu c908x -bus stlk -gmac pro
./run_fpga.sh --target_remote=<ip:port> --system=xuantie_rv64_std_kernel6_6_kcdefault

# Sv48 分页(限制内核页表级数)
./update_dts_cpu.sh xiaohui_v1 -cpu c910 -layout 1111 -sv 48
./run_fpga.sh --target_remote=<ip:port> --system=xuantie_rv64_std_kernel6_6_kcdefault

# 开 PMC(仅 c907fdv 系需要;其他型号无需 -pmc 参数)
# -pmc true 用平台 JSON 的 pmc.base 作 reg base;传地址则覆盖 reg base
./update_dts_cpu.sh xiaohui_v1 -cpu c907fdv -layout 1111 -pmc true
./update_dts_cpu.sh xiaohui_v1 -cpu c907fdv -layout 1111 -pmc 0x26900000
./run_fpga.sh --target_remote=<ip:port> --system=xuantie_rv64_std_kernel6_6_kcdefault

# 开 OP-TEE(仅 xiaohui_v1 系;c925/c930/c950 不支持 OP-TEE)
# kernel/rootfs 落 0x8xxxxxxx 窗口,另加载 bl31 + tee-os;地址取平台 JSON 的
# images_layout.optee 块,gdbinit 改渲染 zsb_rv64_optee.gdbinit
./update_dts_cpu.sh xiaohui_v1 -cpu c910 -layout 1111 -optee true
# 生成后用 OP-TEE 专用入口上板(而非 run_fpga.sh)
./run_fpga_optee.sh --target_remote=<ip:port>

# 开 N-Trace(true = 按 -cpu 自动选版本:C907/C908/R908→v1;拓扑由 -layout 按 8-window 推导)
./update_dts_cpu.sh xiaohui_v1 -cpu c908 -layout 1111 -ntrace true
# 直接指定常量 json(绕过 CSV,调试或 apb/base 变体用)
./update_dts_cpu.sh xiaohui_v1 -cpu c908 -layout 1111 -ntrace ntrace_xiaohui_v1
./run_fpga.sh --target_remote=<ip:port> --system=xuantie_rv64_std_kernel6_6_kcdefault

# —— xiaohui_v2(RV64,c930/c925/c950 系自动选,联动 aplic + h100_v2 gmac)——
# 8 核 c930
./update_dts_cpu.sh xiaohui_v2 -cpu xt-c9301fdt -layout 11111111
./run_fpga.sh --target_remote=<ip:port> --system=xuantie_rva23_std_kernel6_6_kcdefault

# 用 -clusters/-cores_num 对等表达 layout(下式 == -layout 1111000011110000,启用核 4-7,12-15)
./update_dts_cpu.sh xiaohui_v2 -cpu xt-c9301fdt -clusters 2 -cores_num 4,5,6,7,12,13,14,15

# c930 + N-Trace v2 + sba
./update_dts_cpu.sh xiaohui_v2 -cpu xt-c9301fdt -layout 1110000011100000 -ntrace true -trace-sba
./run_fpga.sh --target_remote=<ip:port> --system=xuantie_rva23_std_kernel6_6_kcdefault

# —— ngf(C950/C950V2 NGF 平台)——
# 平台 JSON/模板为增量层,见 meta-xuantie-ngf/recipes-bsp/hw-c9xx/files/
# fpga-ngf/README.ngf.md(含 c9501 pcu v1 / c9502 pcu-v2 生成指令)。

# —— xiaohui_v1_rv32(RV32,rv32 CPU 自动选)——
# 内存封顶 2G:rv32 U-Boot 关 MMU 平坦 32 位寻址(上限 0xFFFFFFFF),从 DDR base
# 0x60000000 起最多可寻址 0xA0000000(2.5G);平台 JSON 已把 size 定为 2G
# (base+size=0xE0000000,落在天花板内),别用 -memsize 顶到 4G/8G,否则 U-Boot 的
# u32 fdt_size_t 会回绕成 ram_size=0(DRAM: 0 Bytes)而挂死。
# rv32 CPU
./update_dts_cpu.sh xiaohui_v1_rv32 -cpu c907-rv32 -layout 1111
./run_fpga.sh --target_remote=<ip:port> --abi_mode=rv32_std

# rv32 开 PMC(c907fdv-rv32)
./update_dts_cpu.sh xiaohui_v1_rv32 -cpu c907fdv-rv32 -layout 1111 -pmc true
./run_fpga.sh --target_remote=<ip:port> --abi_mode=rv32_std

# —— xiaohui_v1_pa40(xiaohui_v1 的 PA40 IMSIC 基址变体,如 C9205;不在 CSV,须显式写平台名)——
./update_dts_cpu.sh xiaohui_v1_pa40 -cpu xt-c9205fdvkt -layout 11 -intc aplic
./run_fpga.sh --target_remote=<ip:port> --system=xuantie_rv64_std_kernel6_6_kcdefault
```

---

## 选项参考

| 选项 | 取值 | 默认 | 说明 |
|------|------|------|------|
| `<soc_platform>` | `xiaohui_v1` / `xiaohui_v2` / `xiaohui_v1_rv32` / `xiaohui_v1_pa40`(旧名 `zsb_rv64` / `zsb_rv32` 也收) | `zsb_rv64` | 位置参数;仅当命名到真实平台 JSON 时才覆盖,否则平台由 CPU 自动选(见下)。`xiaohui_v1_pa40` 的 PA40 基址仅在 `-intc aplic` 下生效 |
| `-cpu`     | `c910` / `c920v2` / `c908` / `c907` / `xt-c9301fdt` … | — | **必填**,须在 `data/cpu-isa-configs/*.conf` 中登记(不认得时会列出全部合法值) |
| `-layout`  | `1111` / `11111111` / `110011` …              | `1`  | 核使能位图,**MSB-first**(最左位=最高 hart,最右位=hart0)。1=启用。如 `1111000011110000` 启用核 4-7,12-15 |
| `-clusters` | cluster 数(整数)                            | 无 | 与 `-cores_num` 配合定 layout 宽度(= clusters × cores_per_cluster)。省略时按 `-cores_num` 最大核号自动推导 |
| `-cores_num` | 逗号分隔的**全局**核号,如 `4,5,6,7,12,13,14,15` | 无 | `-layout` 的对等替代写法(给了就覆盖 `-layout`)。等价:`-clusters 2 -cores_num 4,5,6,7,12,13,14,15` == `-layout 1111000011110000` |
| `-isa`     | `default` / 自定义 isa 串                       | `default` | |
| `-timer`   | 频率(Hz)                                      | `25000000` | |
| `-bus`     | `none` / `ncore` / `stlk` / `stlk2` / `stlk3` | `ncore` | 互连/Link IP |
| `-intc`    | `plic` / `aplic`                              | `plic` | 中断控制器(c925/c930/c950 默认 aplic)。`plic` 模板(`intc/plic.dtsi.in`)一次渲染 **3 个子节点**:`clint0`(`thead,c900-clint`,即 mtimer / 机器定时器)、`aclint_sswi`(`riscv,aclint-sswi`,核间软中断)、`intc`(`thead,c900-plic`,PLIC 本体) |
| `-sv`      | `39` / `48` / `57`                            | 无 | MMU 分页级别。**只改内核 bootargs、不动 DTS `mmu-type`**(rv64 恒 `riscv,sv57`):`39` → 加 `no4lvl`(限 Sv39),`48` → 加 `no5lvl`(限 Sv48,禁 Sv57),`57` / 不传 → 不限(内核用最高 Sv57)。只影响内核用几级页表,不改镜像加载地址 |
| `-gmac`    | `h100` / `pro`                                | `h100` | 网卡(c930/c925 自动切 h100_v2) |
| `-ntrace`  | `false` / `true` / `<ntrace-json 名>`         | `false` | N-Trace 使能 + 版本选择。`true`(或裸 `-ntrace`)按 `-cpu` 自动选常量 json:C907/C908/R908 → `ntrace_xiaohui_v1`,C930/C925/C920 → `ntrace_xiaohui_v2`(不支持的型号如 c950 会 **fail-fast** 并提示);传 `<名>` 则直接用 `data/ntrace/<名>.json`,绕过 CSV(调试或新增 apb/base 变体用;未知名会列出可用配置)。**拓扑不来自 json,而由 `-layout` 按 8-window 规则推导**(全局位 `p` → `cluster=p//8`、`port=p%8`),见下 |
| `-trace-sba` | 开关(无值)                                  | 关 | **仅 v2**:sink 走 system-bus-access(SBA)寻址;v1 无此路径,传了也不生效 |
| `-memsize` | `1G` / `2G` / `3G` / `4G` …                   | 平台默认 | 内存大小 |
| `-optee`   | `true` / `false`                              | `false` | OP-TEE 模式(仅 `xiaohui_v1` 系;`c925`/`c930`/`c950` 不支持) |
| `-pmc`     | `false` / `true` / `<base-addr>`              | `false` | 电源管理初始化(仅 `c907fdv` / `c907fdv-rv32` 需要,其他型号无需此参数)。`true` 用平台 JSON 的 `hsm.pmc.base` 作 reg base;传地址(如 `0x26900000`)则覆盖 reg base。**注意**:这是 DTS 里的 PMC 节点(`pmc_v1.dtsi`),与下文核复位/释放的 `hsm.pcu.boot_release` 是两套不同机制 |

---

## 工作原理

```
  平台 JSON  ──┐
 (硬件常量)    │   ┌──────────────┐
               ├──▶│ dtsi_engine  │──▶  .hw.dts / *.gdbinit
  模板 .in   ──┘   │  (渲染引擎)   │
 (结构+占位)        └──────────────┘
```

三个入口脚本被 `update_dts_cpu.sh` 串起来:

1. `generator/generate_dts.py`   —— 由模板渲染完整 `.hw.dts`
2. `generator/render_gdbinit.py` —— 渲染 `zsb.gdbinit`(核 layout;`-optee true` 时改渲染 `zsb_rv64_optee.gdbinit`,含 bl31/tee-os 加载与 0x8xxxxxxx 窗口)
3. `generator/render_socinit.py` —— 渲染 `zsb_socinit.gdbinit`(link/net/pmc)

**平台 + 子模块选择**:平台(`resolve_platform`)与各子模块变体(intc/pmu/gmac/link/ntrace)
均由 CPU 型号决定。下表即 [`data/platforms/platform-selection.csv`](data/platforms/platform-selection.csv),
按 alias 归一后的 CPU 名**自上而下 fnmatch、首个命中生效**。其中 **`platform/intc/pmu/gmac/link/ntrace`
六列都是数据驱动的唯一源**——`generate_dts.py` 经 `platform_selection_row(cpu)` 读取整行,
改这几列不用改 Python(`ntrace` 列 `v1`/`v2` 直接给版本,`unsupported`/`-` 则 `-ntrace` fail-fast)。
第 7 列 `pcu` 是**按 CPU 的覆盖列**(非唯一源):`v1`/`v2` 覆盖平台 JSON 的 `hsm.pcu.version`
默认(PCU HSM 节点版本),`-` 沿用平台默认;现有行均为 `-`。
仍留在 Python 且优先于本表的:显式 platform 覆盖、arch=rv32(→`xiaohui_v1_rv32`
+`pmu_v1_rv32`)、以及 `-intc`/`-gmac` 命令行覆盖。

| CPU 通配 | platform | intc | pmu | gmac | link | ntrace | pcu |
|----------|----------|------|-----|------|------|--------|-----|
| `*c930*` / `*c925*`     | `xiaohui_v2` | aplic | `pmu_v2` | h100_v2 | none  | v2 | - |
| `*c9502*`               | `minimesh`   | aplic | `pmu_v2` | h100_v2 | none  | v2 | v2 |
| `*c9501*`               | `minimesh`   | aplic | `pmu_v2` | h100_v2 | none  | v2 | - |
| `*c950*`                | `xiaohui_v2` | aplic | `pmu_v2` | h100_v2 | none  | 不支持(fail-fast) | - |
| `*c908x*` / `*c908*_v2*`| `xiaohui_v1` | plic  | `pmu_v1_ext` | h100 | xl200 | v1 | - |
| `*c920v3*`              | `xiaohui_v1` | plic  | `pmu_v1` | h100 | xl200 | v2 | - |
| `*r908*`                | `xiaohui_v1` | plic  | `pmu_v1_ext` | h100 | xl200 | v1 | - |
| `*c920*`                | `xiaohui_v1` | plic  | `pmu_v1` | h100 | none  | v2 | - |
| `*r920*`                | `xiaohui_v1` | plic  | `pmu_v1` | h100 | none  | 无(fail-fast) | - |
| `*c907*` / `*c908*`     | `xiaohui_v1` | plic  | `pmu_v1_ext` | h100 | none  | v1 | - |
| `*`(其余 RV64)         | `xiaohui_v1` | plic  | `pmu_v1` | h100 | none  | 无(`-ntrace` 时 fail-fast) | - |

> **pmu 模板拆分**:`pmu_v1` = C906/C910/C920 基础事件表(branches/branch-misses 为 0x07/0x06);
> `pmu_v1_ext` = C907/C908 扩展事件表(0x36/0x38 仅存在于该家族)。R920 走 C920 系列(`pmu_v1`),
> R908 走 C908 系列(`pmu_v1_ext`)。

> **覆盖优先级**:`PLATFORM_CONFIG` 环境变量 > `<soc_platform>` 位置参数(须命名到存在的
> `data/platforms/<name>.json`)> 按 CPU 自动选(读 CSV)。显式平台覆盖只改 **platform**,
> intc/pmu/gmac/link/ntrace 仍按 CPU 读 CSV(`-intc`/`-gmac` 可再单独覆盖对应列)。
> 别名 `c930/c925/c950` 先归一到 `xt-c9301fdt/xt-c9251fdt/xt-c9501fdvt`,通配匹配同样命中。
> **rv32 变体**(如 `c907-rv32`):platform→`xiaohui_v1_rv32`、pmu→`pmu_v1_rv32`(arch 驱动),
> 其余列不变;c930/c925/c950 均为 RV64,无 rv32 变体。
> **xl300**:**整体 v2-only**——`*c930*`/`*c925*`/`*c950*`(均 `xiaohui_v2`)可用 `-bus stlk3` 选入 xl300(PMU+CBQRI),默认 none。仅 `xiaohui_v2.json` 声明 `link_ip.xl300`;`xiaohui_v1` / `xiaohui_v1_rv32` 均无该块,对 v1/rv32 平台传 `-bus stlk3` 时 `render_xl300` **fail-fast**(`sys.exit`,提示 xl300 仅 xiaohui_v2 rv64 支持)。CBQRI 存在与否即 v2-only 判据:`render_xl300` 以「平台 JSON 是否声明 `link_ip.xl300.cbqri`」为唯一开关(无 CPU 名判断)。
> 地址**派生**(仿 `ncore` 块):JSON 只给单 cluster 原型(`pmu`/`cbqri` 的 base/size/irq_base/irq_stride…)+ `clusters` + `cluster_stride`,脚本按 `i∈[0,clusters)` 派生每 cluster 的 pmu/cbqri 地址(`base + i*cluster_stride`)与 irq(`irq_base + i*irq_stride`);cbqri 的 cache phandle 用 `&l3_cache_${CBQRI_INDEX}`——`l3_cache_` 前缀硬编码在模板 `link_xl300.dtsi.in` 里,只用 cluster 下标做变量。每节点 `status` = 该 cluster 是否被 `-layout` 填充(`core//cores_per_cluster==i`),pmu/cbqri 用同一判据。
> **L3 cache 亦 xl300 门控**:`cpu.dtsi.in` 的 `l3_cache_<i>: l3-cache-<i>` 节点与各核 `next-level-cache = <&l3_cache_<i>>` **仅在 `-bus stlk3` 时发射**(`render_cpu` 以「`-bus stlk3` 且 rv64 且平台声明 `link_ip.xl300.l3_cache`」为开关);非 xl300 的 RV64 build 不再带 L3。节点值来自 `link_ip.xl300.l3_cache` 单 cluster 原型(`level`/`block-size`/`sets`/`size`),按 `clusters`(物理 cluster 数)派生 `l3_cache_0..N-1`,核 i → `l3_cache_<min(i//cores_per_cluster, clusters-1)>`;节点数取物理 `clusters` 而非 `-layout` 派生数,好让 cbqri 的 `&l3_cache_<i>` phandle 恒可解析。
> **ntrace**:版本(v1/v2)即本表 `ntrace` 列(唯一源);硬件常量按「SoC 家族 + 版本」存 `data/ntrace/ntrace_<家族>_v<版本>.json`(现有 `ntrace_xiaohui_v1` / `ntrace_xiaohui_v2`)。

**模板引擎指令**(`generator/dtsi_engine.py`):

| 指令 | 含义 |
|------|------|
| `${MACRO}` | 替换为 `defines[MACRO]` |
| `##IF MACRO## … ##ENDIF##` | `MACRO` 非空且非 `"0"` 时才输出(不支持嵌套) |
| `##REPEAT NAME## … ##ENDREPEAT##` | 对 `repeats[NAME]`(dict 列表)逐项展开,块内 `${...}` 可引用每项字段 |

其余内容(含 `//` 与 `/* */` 注释)原样保留。未定义的 `${MACRO}`
或缺数据的 `##REPEAT##` 会直接报错退出,便于及早发现配置漏项。

**中断控制器联动 ISA / mtimer**(`generator/generate_dts.py: render_cpu` / `render_intc`):

- **AIA 扩展随 `-intc`**:AIA 扩展(`smaia` / `smcsrind` / `ssaia` / `sscsrind`)只在
  aplic/imsics 中断树下有意义,故其存在与否由中断控制器决定,而非 CPU 原始 ISA:
  - `aplic` → 自动补齐缺失的这四个扩展(老型号 ISA 未声明 AIA 时,接到 aplic 也会补上),
    同时写入 `riscv,isa` 与 `riscv,isa-extensions`;
  - `plic` → 从两处一并移除这四个扩展。
  - `svvptc` 与 AIA 无关,两种情况都原样保留。
- **mtimer 随 Sstc(按 ISA 检测)**:aplic 路径下是否生成 ACLINT `mtimer` 节点,取决于
  CPU 的 ISA 是否声明 `sstc`(`render_intc_dtsi.isa_has_sstc`,不再用硬编码 CPU 列表):
  - 有 `sstc` → 用 stimecmp,省略 `mtimer`;
  - 无 `sstc` → 生成 `mtimer` 节点(地址取平台 JSON `intc.aplic.mtimer`;平台未提供则报错退出)。
  - plic 路径由 CLINT(`clint@` 节点)提供定时,不涉及此 `mtimer` 节点。

---

## 目录结构

```
fpga/
├── update_dts_cpu.sh          # 主入口(解析选项 → 调 generator/ 下三个生成器)
├── run_fpga.sh                # FPGA 上板运行入口
├── run_fpga_optee.sh          # OP-TEE 上板运行入口
├── generator/                 # 生成器(渲染引擎 + 各生成脚本)
│   ├── generate_dts.py        #   DTS 生成器
│   ├── dtsi_engine.py         #   渲染引擎(${MACRO}/##IF##/##REPEAT##)
│   ├── render_gdbinit.py
│   ├── render_socinit.py
│   ├── render_intc_dtsi.py
│   ├── render_ntrace_dts.py
│   └── normalize_plic_isa.py  #   plic 基线剔除 AIA(校验重建脚本调用)
├── data/                      # 数据源(硬件常量 + 模板 + 配置)
│   ├── platforms/             #   平台 JSON(硬件常量)
│   │   ├── xiaohui_v1.json
│   │   ├── xiaohui_v2.json
│   │   ├── xiaohui_v1_rv32.json
│   │   └── xiaohui_v1_pa40.json   # v1 的 PA40 IMSIC 基址变体(如 C9205);不在 CSV
│   ├── ntrace/                #   N-Trace 硬件常量(ntrace_<家族>_v<版本>;CPU→版本在 platform-selection.csv)
│   │   ├── ntrace_xiaohui_v1.json  # C907/C908/R908 常量(topology_model=single-l2-funnel)
│   │   └── ntrace_xiaohui_v2.json  # C930/C925/C920 常量(topology_model=chained-dmi)
│   ├── templates/             #   .dtsi.in 模板
│   │   ├── cpu/               #     cpu.dtsi.in
│   │   ├── gmac/              #     gmac_h100.dtsi.in / gmac_h100_v2.dtsi.in / gmac_pro.dtsi.in
│   │   ├── intc/              #     plic.dtsi.in / aplic.dtsi.in
│   │   ├── pmu/               #     pmu_v1.dtsi.in / pmu_v1_rv32.dtsi.in / pmu_v2.dtsi.in
│   │   ├── pmc/               #     pmc_v1.dtsi.in
│   │   ├── link/              #     link_xl200.dtsi.in / link_xl300.dtsi.in
│   │   ├── ntrace/            #     encoder/funnel/sink/reserved_memory.dtsi.in
│   │   └── zsb.dts.in         #     顶层装配模板
│   ├── cpu-isa-configs/       #   CPU→ISA 映射(目录下所有 *.conf 都会被读取)
│   │   ├── cpu-isa-mapping.conf #  主映射,toolchain 经 sync-cpu-isa-mapping.sh additive 合并生成(保留手改)
│   │   └── xt-*.conf           #   并列的 per-vendor 独立补充(读法相同)
│   ├── soc-schema.json        #   平台 JSON 的 JSON Schema
│   └── ntrace-schema.json     #   ntrace v1/v2 常量的 JSON Schema
├── tests/                     # 回归校验
│   ├── generate_golden_refs.py #  校验(语义比对 + lint)
│   ├── generate_golden_refs.sh #  重建基线(旧生成器产出)
│   └── golden-refs-master/    #   校验基线
├── runtime/                   # 上板运行时辅助
│   ├── get_images.sh
│   ├── get_images_optee.sh
│   ├── load_image.csh
│   ├── system_alias_config.sh
│   └── tftpd.py
├── tools/                     # 预编译二进制
│   ├── dtc
│   ├── riscv64-linux-gdb
│   └── riscv64-unknown-elf-gdb
└── gdbinit/
    ├── zsb.gdbinit.in            # 模板(通用 boot)
    ├── zsb_rv64_optee.gdbinit.in # 模板(OP-TEE:bl31 + tee-os,kernel/rootfs 落 0x8xxxxxxx 窗口)
    └── zsb_socinit.gdbinit.in    # 模板
```

---

## 直接调用底层生成器

`update_dts_cpu.sh` 只是把选项转成下列命令,调试时可单独跑:

```bash
# 仅生成 DTS
python3 generator/generate_dts.py --cpu xt-c9301fdt --layout 11111111 --output .hw.dts

# 仅生成 zsb.gdbinit
python3 generator/render_gdbinit.py --cpu c910 --layout 1111 \
        --gdbinit gdbinit/zsb.gdbinit

# 仅生成 zsb_socinit.gdbinit
python3 generator/render_socinit.py --cpu c910 --bus ncore --gmac h100
```

各脚本均支持 `--platform <name>` 覆盖平台,`-h` 查看完整参数。

---

## 新增 / 修改平台

1. 在 `data/platforms/` 复制一份现有 JSON 改基地址/偏移。
   地址派生优先用 `base + offset`(如 ncore snoop/dvm、ipmc per-core),
   而非硬编码绝对地址,便于阅读与复用。
2. 若新增字段,同步更新 `data/soc-schema.json`。
3. 校验 JSON 合法(下面片段用相对路径,须在本 README 所在的 `fpga/` 目录、即 `data/` 的上级目录下执行):

```bash
python3 - <<'EOF'
import json, jsonschema
from pathlib import Path
schema = json.loads(Path("data/soc-schema.json").read_text())
data = json.loads(Path("data/platforms/<your_platform>.json").read_text())
jsonschema.validate(data, schema)
print("OK")
EOF
```

---

## 各子模块如何扩展

DTS 由若干子模块(block)拼装,每个 block 的「选哪个变体」由
`generator/generate_dts.py` 里一个选择函数决定。扩展成本分两档:**纯数据**
(加 JSON/CSV,零 Python)与 **需改 Python**(加模板 + 改选择函数)。

下面先给四个**典型场景**走通完整链路(新人照着做即可):新增平台、换 intc/gmac 变体、改子模块基址/stride 派生规则、新增带派生地址的 IP 子模块;再附**逐子模块机制表**(老手直接查)。

### 场景一:新增一颗 SoC 平台并在 emu / FPGA 上验证

要把一颗新 SoC(不同基地址 / DDR 大小 / 中断控制器 / 互连 IP)接进来,基本是**纯数据**:

1. **写平台 JSON**:在 [`data/platforms/`](#新增--修改平台) 复制一份最接近的现有 JSON,改
   `memory`(base / size)、`peripherals`、`intc`(plic / aplic)、`link_ip`、`hsm`
   (pmc / pcu.boot_release / pcu.nodes)等硬件常量。地址尽量用 `base + offset` 派生而非硬编码绝对值。
2. **同步 schema 并校验**:若新增字段,更新 `data/soc-schema.json`,再按
   [「新增 / 修改平台」](#新增--修改平台) 的片段做 `jsonschema.validate`(不合法会 fail-fast)。
3. **登记 CPU→平台映射**:在 `data/platforms/platform-selection.csv` 的 `platform` 列,
   用通配把目标 CPU 指到新平台名(这样 `-cpu` 即可自动选中它);同一行顺带确认
   `intc` / `pmu` / `gmac` / `link` / `ntrace` 列。
4. **生成 DTS + gdbinit**:显式指定平台名,或用 `auto` 让脚本按 `-cpu` 自动选:
   ```bash
   ./update_dts_cpu.sh <新平台名> -cpu <cpu> -layout 1111
   # 或: ./update_dts_cpu.sh auto -cpu <cpu> -layout 1111
   ```
5. **回归校验**:跑 [golden refs](#回归校验golden-refs)(`python3 tests/generate_golden_refs.py`)确认
   dtc 语义 + lint 通过;新平台没有 master 基线属正常(只做自洽渲染/编译校验)。
6. **上板冒烟**:镜像就位后按 [run_fpga 冒烟](#run_fpga-冒烟--run-fpga) 用
   `--run-fpga` / `run_fpga.sh` 走一遍完整启动管线。

> 只改地址 / 尺寸 / 实例数时,模板与 Python 全都不动——见文末「三条经验法则」第 1 条。

### 场景二:给已有 CPU 换 intc / gmac 变体

目标 CPU 已在 `*.conf` 登记、平台也已存在,只想换中断控制器或网卡——**纯数据、零 Python**:

- **换 intc(plic ⇄ aplic)**:改 `platform-selection.csv` 的 `intc` 列(该 CPU 那行)即永久生效;
  或单次生成时用 `-intc aplic` / `-intc plic` 临时覆盖。`plic` 模板一次渲染 3 个子节点
  (`clint0`/mtimer、`aclint_sswi`、`intc`/PLIC 本体),见 [选项参考](#选项参考) 的 `-intc` 行。
- **换 gmac(h100 ⇄ pro)**:改 CSV 的 `gmac` 列;或单次用 `-gmac pro` / `-gmac h100` 覆盖
  (c930/c925 会自动切 `h100_v2`)。
- 改完照例 [golden refs](#回归校验golden-refs) + [run_fpga 冒烟](#run_fpga-冒烟--run-fpga) 验一遍。

> 注意:上面是「在**已有模板**之间换归类」。若要引入一个**结构全新、还没有模板**的 intc/gmac 类型,
> 属于「需改 Python」档——加 `.dtsi.in` 模板 + 改选择函数,见下表与「三条经验法则」第 3 条。

### 场景三:改某个子模块的基址 / stride 派生规则

多核子模块(`link_ip.ncore`、`link_ip.xl300`、`pmc.ipmc`、`intc.aplic.imsics_*` 等)的每实例地址
**不是硬编码枚举**,而是脚本按「原型基址 + 步进」派生。要挪地址、改步进、增减实例数——**纯数据、零 Python**:

1. **查派生公式**:先看 `data/soc-schema.json` 里该 block 的 `description` 与逐字段描述——
   派生规则(哪个字段当基址、哪个当步进、如何组合)就写在 schema 里。现有几例:
   - `link_ip.xl300`:实例 i 地址 = `base + i*cluster_stride`,irq = `irq_base + i*irq_stride`,cbqri cache = `&l3_cache_<i>`(前缀在模板里,下标为 cluster i);实例数 = `clusters`。`l3_cache` 原型(`level`/`block-size`/`sets`/`size`)按 `clusters` 派生 `l3_cache_<i>` 节点写入 `cpu.dtsi.in`,仅 `-bus stlk3` 时发射(见「xl300」小节)。
   - `link_ip.ncore`:snoop 寄存器 i = `cluster<c>_base + i*snoop_stride + snoop_offset`,DVM = `cluster<c>_base + dvm_offset`;实例数 = `cluster<c>_snoop_count`。
   - `pmc.ipmc`:core i 的 release/clock 寄存器 = `base_addr + i*release_stride + {release,clock}_offset`;实例数 = `n_cores`。
   - `intc.aplic.imsics_{m,s}`:reg size = `size_per_core * slots`(slots = 核数向上取整到 `{2,4,16,32}`)。
2. **只改数值**:在 `data/platforms/<平台>.json` 对应 block 里改基址 / 步进 / 计数字段即可,
   **不要**去手算各实例的绝对地址(那正是脚本的活)。改步进只动一个字段,全实例自动跟随。
3. **schema 校验**:这些字段都在 schema 中 `required`,漏填会在加载时 fail-fast;
   按 [「新增 / 修改平台」](#新增--修改平台) 片段跑 `jsonschema.validate` 自检。
4. **回归**:跑 [golden refs](#回归校验golden-refs);若刻意改了地址/步进,同步更新对应 master 基线。

> 派生规则集中在 schema + 平台 JSON,是本项目「硬件常量只出现在数据文件」铁律的延伸:
> schema 定**规则**(必填字段 + 公式描述),平台 JSON 出**实数**,脚本按数值**推算** DTS。

### 场景四:新增一个带派生地址的 IP 子模块

要接入一个**全新的、多实例且地址线性**的 IP(还没有模板)——**需改 Python**,照 `render_xl300` 这条范式走:

1. **在 schema 定规则**:`data/soc-schema.json` 的 `link_ip`(或相应父块)下加一个 block,
   声明「单实例原型 + 步进」字段(base / size / irq_base / irq_stride / stride / count 等),
   写 `required` 锁住必填项,并在 `description` 里把派生公式讲清楚(仿 `xl300`/`ncore`)。
2. **平台 JSON 出实数**:在 `data/platforms/<平台>.json` 该父块下填单实例原型 + 步进值。
   若是「某代际专属」(如 xl300 v2-only),就只在对应平台声明,其它平台不写该 key。
3. **写单实例模板**:`data/templates/<block>/<x>.dtsi.in`,只写**一个** cluster/实例,
   用 `##REPEAT <LIST>##` 包住、`${MACRO}` 占位(参照 `link/link_xl300.dtsi.in`,「模版写 1 核」)。
4. **写 render 函数**:仿 `generator/generate_dts.py:render_xl300`——读原型 base/stride,
   `for i in range(count)` 派生每实例的 `base + i*stride`、irq、cache 名、`status`,
   喂给 `_render_file(模板, defines, {列表宏: 实例列表})`。gating(如仅某平台支持)在函数开头 fail-fast。
5. **main 接线**:在主渲染流程把 `render_<x>(...)` 的返回填进对应 `${..._BLOCK}` 占位;
   若该 IP 由某开关(如 `-bus`)选入,在选项解析处接上。
6. **回归**:跑 [golden refs](#回归校验golden-refs);新 block 无 master 基线时先做自洽渲染/编译校验,
   再按需补基线。

> 判据:**实例数固定、地址零散** → 用枚举数组(如 `link_ip.xl200.pmu`,`$defs/pmuEntry`);
> **实例数随拓扑变、地址线性** → 用原型 + 步进派生(如 `xl300`),避免手编每实例地址。

### 逐子模块机制表

| 子模块 | 变体选择机制 | 加一个新变体要动 | 成本 |
|--------|--------------|------------------|------|
| 平台 platform | `resolve_platform` 读 `data/platforms/platform-selection.csv` 的 `platform` 列(唯一源,经 `platform_selection_row` fnmatch 首个命中);显式覆盖 + arch=rv32 仍在 Python 优先 | 加 `data/platforms/<name>.json`(必要时同步 `soc-schema.json`)+ 在 CSV 加/改一行把 CPU 通配映射到它 | 纯数据(详见上节) |
| CPU / ISA | 单模板 `cpu/cpu.dtsi.in`,ISA 串来自 `data/cpu-isa-configs/` 下**所有** `*.conf`(shell 与 Python 两个入口都 glob),每行 `default_isa[<cpu>]="<isa>"` | 优先经 toolchain 重新生成(见下注);要手工加就在某个 `*.conf` 补一行 `default_isa[...]`;地址常量在平台 JSON | 纯数据 |
| 中断控制器 intc | `select_intc_kind(cpu)` 读 CSV `intc` 列(唯一源,plic/aplic);`-intc` 可覆盖 | 已有类型换归类:改 CSV `intc` 列一行;全新 intc 类型:加模板 `intc/<x>.dtsi.in` + 写 `build_<x>` | 换归类=纯数据 / 新类型=需改 Python |
| PMU | `select_pmu(cpu, arch)` 读 CSV `pmu` 列(唯一源);arch=rv32 强制 `pmu_v1_rv32`(Python 优先) | 已有模板换归类:改 CSV `pmu` 列一行;全新版本:加模板 `pmu/pmu_<v>.dtsi.in` + CSV 填该名(模板路径 `pmu/<v>.dtsi.in` 由 `select_pmu` 拼) | 换归类=纯数据 / 新模板=半 |
| PMC | 单模板 `pmc/pmc_v1.dtsi.in`,`-pmc` 三态(`false`/`true`/`<base-addr>`),reg base 默认取平台 JSON `hsm.pmc.base`,传地址则覆盖 | 改平台 JSON 常量;新版本加模板并在 main 接线 | 半(常量数据,版本要 Python) |
| PCU HSM 节点 | `select_pcu_version(cpu, plat)`:CSV `pcu` 列(v1/v2)覆盖平台 JSON `hsm.pcu.version` 默认;地址由平台 JSON `hsm.pcu.nodes` 派生:`pcu_base = complex_base + (hartid/2)*complex_stride + core{0\|1}_pcu_offset`(2 核一 complex)。v1(`pcu/pcu.dtsi.in`,xuantie,pcu)每活跃核一子节点;v2(`pcu/pcu-v2.dtsi.in`,xuantie,pcu-v2)相邻活跃双核成组(偶数对齐 master),每组一子节点。version 缺失或 `hsm.pcu.nodes` 未登记 → 无节点(opensbi 自动降级 wfi);v2 layout 未成对 fail-fast | 改地址:平台 JSON `hsm.pcu.nodes`(纯数据);换版本归类:CSV `pcu` 列或 `hsm.pcu.version`(纯数据);新平台登记 `hsm.pcu.nodes` + CSV 行 | 纯数据 |
| GMAC 网卡 | `detect_gmac_type(cpu, -gmac)` 读 CSV `gmac` 列(唯一源)→ `select_gmac` 映射模板;`-gmac pro/h100` 可覆盖 | 已有类型换归类:改 CSV `gmac` 列一行;全新类型:加模板 `gmac/gmac_<x>.dtsi.in` + 在 `select_gmac` 加一条路径映射 | 换归类=纯数据 / 新类型=半 |
| XuanTie Link | `-bus`(`ncore/stlk/stlk2/stlk3/none`)选族;xl200 由 `select_link_pmu(cpu)` 读 CSV `link` 列(唯一源)+bus 使能,xl300 由 `-bus stlk3` 使能(**v2-only**,v1/rv32 fail-fast);地址由平台 JSON `link_ip.xl300` 单 cluster 原型 + `cluster_stride` 按 `clusters` 派生 | 同族加实例:改平台 JSON `link_ip.*`(xl300 只需调 `clusters`/原型);换 CPU 是否走 xl200:改 CSV `link` 列;全新 link 族:加模板 `link/link_<x>.dtsi.in` + 写 `render_<x>` + main 接线 | 半 / 需改 Python |
| N-Trace | `select_trace_config(cpu, platform)` 读 `platform-selection.csv` 的 `ntrace` 列(唯一源,v1/v2;unsupported/- fail-fast)定版本,再由平台名取 SoC 家族,拼出 `data/ntrace/ntrace_<家族>_v<版本>.json`;拓扑算法由所选 json 的 `topology_model` 经 `TOPOLOGY_MODELS` 注册表分派 | 复用已有拓扑的新版本:CSV `ntrace` 列填该版本 + `data/ntrace/ntrace_<家族>_v<版本>.json`(`topology_model` 指向已有 model),**零 Python**;某 SoC 家族改 apb/base 地址:加 `data/ntrace/ntrace_<新家族>_v<版本>.json`,**零 Python**;全新拓扑:写渲染函数 + 注册表加一行 + `ntrace-schema.json` 加 per-model 分支 | 复用/换 apb=纯数据 / 新拓扑=需改 Python |

> **CPU→ISA 的 `*.conf`**:`data/cpu-isa-configs/` 下所有 `*.conf` 会被当作一组一起读取。
> 主映射 `cpu-isa-mapping.conf` 由 toolchain 经 `sync-cpu-isa-mapping.sh` 自动合并生成
> (additive、保留手改),通常经 `toolchain-init.sh` / `gen-cpu-confs.sh` 重新生成;`xt-*.conf`
> 是并列的 per-vendor 独立补充,读法相同。登记/改某 CPU 的 ISA **优先走 toolchain 重新生成**;
> 要手工加,就在某个 `*.conf` 补一行 `default_isa[<cpu>]="<isa>"`(手改在 mapping.conf 里会被
> additive 合并保留)。

> **核复位 / 释放(`hsm.pcu.boot_release`)**:上表都是 DTS block,这一项不同——它是 **gdbinit** 输入,
> ZSB 阶段 GDB 靠它把各 hart 从复位向量拉起。`render_gdbinit.py` 读平台 JSON 的 `hsm.pcu.boot_release`:
> `reset_addrs` = 每核复位向量寄存器(写 `$zsb_addr`),`release_addr` / `release_layout` = 全局释放寄存器。
> 按 SoC 代际分两种硬件:**906 / 907 / 908 / 920(v1)= PMC v1 式释放**(release 寄存器在
> `0x18030000`,无每核写);**925 / 930 / 950(v2)= PCU**(reset/config 落在 `0x2690_0000` 区,
> 另含每核 `release_addrs` + `release_value`)。**v1、v2 复位/配置/释放都按 `-layout` 门控**:只有被
> `-layout` 填充的 hart 激活,其余用 `# ` 注释掉;全局释放掩码按核数选 `release_default`(≤8 核)/
> `release_layout`(>8 核),单核时该行注释为基线。改地址是纯数据(改平台 JSON `hsm.pcu.boot_release`)。
> 注意它与 `-pmc`(DTS 电源管理节点 `pmc_v1.dtsi`)是**两套不同的东西**;boot_release 的 v1/v2 指
> 放核时序代际,与 PCU HSM 节点版本(`hsm.pcu.version`,xuantie,pcu / xuantie,pcu-v2)是两套概念。

**三条经验法则:**

1. **只是换地址 / 尺寸 / 实例数** —— 几乎总是纯数据:改对应的平台 JSON
   (`data/platforms/*.json`)或版本 JSON(`data/ntrace/*.json`),模板与 Python
   都不动。硬件常量集中在数据文件是本项目第一设计铁律。
2. **给已有 CPU 换变体归类** —— 纯数据:改 `platform-selection.csv` 对应列一行
   (platform/intc/pmu/gmac/link/ntrace/pcu 均由 `platform_selection_row` 读该 CSV),Python 不动。
3. **要引入结构不同的新 block(新 intc/pmu/gmac/link 族)** —— 三步:
   ① 在 `data/templates/<block>/` 加一个 `.dtsi.in` 模板(只放结构 +
   `${MACRO}`/`##IF##`/`##REPEAT##` 占位);② 在 CSV 该列填新值,并在 `generate_dts.py`
   把新值映射到模板路径(`select_gmac` 的字典 / `build_<x>` / `render_<x>`;pmu/link
   走命名约定);③ 若模板引入新宏,在对应 builder/render 函数里补齐 defines
   (未定义宏会 fail-fast)。

> **数据驱动的 block**:平台 + intc/pmu/gmac/link/ntrace 六列的「选哪个变体」现在都是纯数据——
> `generate_dts.py` 经 `platform_selection_row(cpu)` 读 `platform-selection.csv`,加
> CPU→变体映射只改 CSV(pcu 列是按 CPU 覆盖 `hsm.pcu.version` 的同类数据列);
> N-Trace 版本即该 CSV 的 `ntrace` 列,并把「版本→算法」也
> 做成数据分派(`topology_model` 注册表),复用已有拓扑加版本纯数据即可。仍需改 Python 的
> 只剩「引入结构全新的变体」:要加对应模板,并把 CSV 里的新值映射到模板路径 / builder。

---

## 回归校验(golden refs)

改动模板或生成器后,务必跑校验,确认输出与基线一致:

```bash
python3 tests/generate_golden_refs.py
```

- DTS(38 例):经 `dtc` 归一化后做**语义比对**(不受 hi/lo、大小写、格式差异影响)
- gdbinit:做**逐字节比对**
- N-Trace(7 例):新 `render_ntrace_dts.py` 对比 master 独立生成器 `generate_trace_dts.sh`(详见下)
- 期望结果:`38 ok, 0 diff … PASS` + `ntrace … 7 ok, 0 fail`

只想跑 lint(dtc 编译 + unit-address 检查):

```bash
python3 tests/generate_golden_refs.py --lint-only
```

### run_fpga 冒烟(--run-fpga)

前两项校验保证生成物**内容**正确;这一项校验生成物**可跑**:对每个 golden 用例
走真实上板链路 —— `update_dts_cpu.sh`(渲染 `.hw.dts` + gdbinit)接
`run_fpga.sh --simulate_run=true`(`dtc` 编译 `.hw.dts`,再让 GDB 无 target
source `zsb.gdbinit`),以「无脚本错误跑到底」为通过(既查退出码,也扫 GDB
错误特征,因为 GDB 遇脚本错误仍会 `exit 0`)。

```bash
python3 tests/generate_golden_refs.py --run-fpga   # 只跑这一项
python3 tests/generate_golden_refs.py              # 默认门禁也含此阶段
```

- **依赖镜像**:`run_fpga.sh` 在 simulate 前会校验各启动镜像存在,故本阶段在
  `../images` 未就绪时**整体自动跳过**(CI 场景无镜像依赖,默认门禁照常 PASS);
  镜像齐备时(rv64/rv32 各按默认 `opensbi 1.7 / kernel 6.6 kcdebug / lite`)才实跑。
- **不污染工作区**:脚本会就地改写 `.hw.dts` / `hw.dtb` / `gdbinit/*`,本阶段用
  `try/finally` 快照并还原这些文件、删除生成物,跑完工作区保持干净。
- **OP-TEE 跳过**:`run_fpga.sh` 无 OP-TEE 启动路径(只加载 `zsb.gdbinit`;optee 的
  gdbinit 开头 `monitor` 复位需在线 target,simulate 从不连接),故 `--optee` 两例
  跳过 —— 其 gdbinit 正确性仍由上面的逐字节比对守护。
- 期望结果(镜像齐备):`run_fpga.sh simulate: 38 ok, 0 fail, 2 skipped`。

基线本身由旧生成器在 master 分支产出,重建用 `tests/generate_golden_refs.sh`。
其中 plic 用例重建后会经 `generator/normalize_plic_isa.py` 剔除 AIA 扩展
(旧生成器透传 CPU 原始 ISA,可能带 AIA,而 plic 架构不应含 AIA),
使基线本身即自洽 —— 比较时不再对 AIA 做额外调和。类似地,`xt-c9501*`(c950)
会在生成期做两处 SoT 调和:`sed` 剔除 `smrnmi`(见 `data/cpu-isa-configs/xt-c9501.conf`),
以及经 `generator/mirror_xl300_status.py` 把 xl300 PMU/CBQRI 节点的 `status`
**镜像**成同 layout 下 c9301(c930)基线的值。原因:pinned master 早于 c950 成为
xl300 目标,基线里 c9501 的 xl300/cbqri 全 disabled;而新生成器视 c950 同 c930
(同 `xiaohui_v2` + `-bus stlk3`)使能它们。镜像只改 `status`(地址/cache/rcid 仍取
c9501 自身产出),既保持基线**独立来源**(c930 旧输出即真值、不自证),又让 c950
真正参与语义比对(比较端因此不再 drop c950 的 xl300/cbqri key)。

### run_fpga 镜像目录约定

`run_fpga.sh` 的镜像路径**不可自定义**,由 `runtime/get_images.sh` 硬编码。脚本一进来
先 `cd` 到自身所在的 `fpga/` 目录,所有路径都相对它解析:

- **镜像根目录**:默认 `../images/`(即 `fpga/` 的同级目录);`--rva_profile=rva23` 时切到 `../images-rva23/`。
- **子目录 + 文件名也固定**,按参数拼死,不能改名:

  | 子目录 | 文件名(示例) | 随哪些参数变 |
  |--------|----------------|--------------|
  | `zsb/`     | `zero_stage_boot-v1.3-rv64.bin`             | `abi_mode`(rv64/rv32) |
  | `opensbi/` | `fw_dynamic-v1.7-rv64.bin`                  | `opensbi_ver` + `abi_mode` |
  | `kernel/`  | `Image-6.6-rv64-kcdebug`                    | `kernel_ver` / `kernel_cfg` / `abi_mode` |
  | `rootfs/`  | `rootfs.xuantie-image-rv64-6.6-lite.cpio.gz`| `rootfs_type` / `kernel_ver` / `abi_mode` |
  | `u-boot/`  | `u-boot-fpga-rv64.bin`                      | `enable_uboot` 时才需要 |

  `hw.dtb` 不在 `images/` 里,而是 `fpga/` 当前由 `update_dts_cpu.sh` 生成的那份。

- **路径本身不能传参**,只能通过 `--abi_mode / --opensbi_ver / --kernel_ver / --kernel_cfg /
  --rootfs_type`(或打包成 `--system=<alias>`)在上述命名规则内**选择**具体文件。
- **上板 / simulate 前会逐个校验**:`run_fpga.sh` 对 `images_array` 每个镜像做 `realpath` +
  非空检查(`[ -s ... ]`),缺文件或空文件直接 `exit 1`。这也是上面 `--run-fpga` golden 阶段
  以 `../images/` 是否就绪作为**整体跳过**门控的原因。

### 手动多核(2core)调试(纯 bench 操作,不在脚本内)

自动化引导**不需要**多核 GDB:`run_fpga.sh` 单个 GDB source `zsb.gdbinit` 时,已把所有
使能 hart 的复位向量设好并一把 `set *0x18030000 = 0xff/0xffff` 释放全部核,硬件从各自
复位向量自动拉起从核。因此启动界面上**只有一个 GDB 会话**,看不到"多核链接"。

> 早期 `--enable_2core` 开关 + `gdbinit/zsb.gdbinit_core1`(后台再起一个 GDB 连
> `1026` 端口给从核)**已删除**:它不改变生成的 `.hw.dts` / gdbinit,自动化引导用不到。
> 与之配套的运行时 `ctl_enable_2core` 门控也一并删除——生成器已按 platform 直接产出对应
> 复位体(v1 走 `0x18030xxx`、v2 走 `0x268xxxxx` 逐 hart),`run_fpga.sh` 直接 source 即可,
> 无需再用变量在运行时选分支。

若确需交互式单独调某个从核(给 hart 下断点、看异常),在 bench 上**手动**用一个额外的
GDB attach 到该 hart 的 gdbserver 端口即可;要点是**重连时不要带 `-prereset`**——它会在
attach 时复位目标,把已启动的核和已加载的镜像一并复位。这属于纯手动排障步骤,不进
`run_fpga.sh`。

### N-Trace golden(7 例,独立基线)

master 的 `script/trace-configs/*.json`(7 份)由**独立**的旧生成器
`script/generate_trace_dts.sh` 消费,产出 `ntrace{,-memory}.dtsi`;这套基线
存于 `tests/golden-refs-master/ntrace/`(与 DTS 基线同一 pinned master ref 产出,
重建同样走 `tests/generate_golden_refs.sh`,需 `jq`)。这是**独立来源**,能发现
自产自校基线抓不到的移植 bug。

master 把拓扑写死在配置里(`clusters`/`cores_per_cluster`/`enabled_cores`/
`sba_dmi_addr`),新方案改由 `-layout`(8-window)推导,故每份配置映射为一组
`(version, layout, sink, sba)` 调用 `render_ntrace_dts.render()`。比对器
**以节点 unit-address 为键**,并把 `&label` 端点解析回目标地址再比,因而对两处
**有意差异**免疫:

- **v2 多 cluster 的 CPU-id / 节点名重编号**:master 跨 cluster 顺序编号
  (…3,4,5),新方案用全局 8-window 位置(…8,9,10)以对齐 cpu-node/gdbinit;
  地址完全一致,故按地址解析的连线一致,`cpu=<>` 整数与 `&label` 名不参与比对。
- **v1 funnel1 的 `input_port`**:旧 shell 输出**空**列表(bug),新方案列出使能的
  encoder;`output_port` 已完整描述 DAG 且逐一匹配,`input_port` 按
  "master 边 ⊆ new 边" 校验 —— 允许该修复,但若新方案漏掉 master 有的边仍会失败。

所有节点的地址/`reg`/`level` 及 reserved-memory 区间必须完全一致。7 例覆盖:

| 配置(master json)                    | 新方案调用                                          | 覆盖点 |
|---------------------------------------|-----------------------------------------------------|--------|
| `v1-1c1c-xiaohui-h100`                | `ntrace_xiaohui_v1` layout `1`                      | v1 单核(无 funnel1) |
| `v1-1c2c-xiaohui-h100`                | `ntrace_xiaohui_v1` layout `11`                     | v1 单簇 2 核(funnel1 修复) |
| `v1-1c4c-xiaohui-h100`                | `ntrace_xiaohui_v1` layout `1111`                   | v1 单簇 4 核(funnel1 修复) |
| `v1-1c8c-xiaohui-h100`                | `ntrace_xiaohui_v1` layout `11111111`               | v1 单簇 8 核(funnel1 修复) |
| `v2-1c2c-xiaohui-h100`                | `ntrace_xiaohui_v2` layout `11`                     | v2 单簇 2 核(链式) |
| `v2-2c4c-cluster0_in-xiaohui-h100`    | `ntrace_xiaohui_v2` layout `1100000000`             | v2 双簇仅 cluster0 使能(partial sink) |
| `v2-2c6c-sba-xiaohui-h100`            | `ntrace_xiaohui_v2` layout `11100000111` + `--sba`  | v2 双簇 2×3 核 + SBA sink 寻址 |

---

## 设计方案

### 一、改造前的问题(master)

旧流程基于 `sed` + `ln -sr`(符号链接)拼装 DTS,并用独立脚本处理 ntrace:

- `update_dts_cpu.sh` 约 690 行,靠 `sed` 就地替换字段、`ln -sr` 把预制的
  `.dtsi` 片段链进目标目录;
- `generate_trace_dts.sh` 约 930 行,单独用 shell 拼 ntrace 设备树;
- 中断控制器、pmu、gmac 等以**预制片段**存在(如
  `aplic_2cores/4cores/16cores.dtsi`、`aplic_c930.dtsi`、`aplic_rv64.dtsi`、
  `plic.dtsi`、`gmac_*.dtsi`、`pmu_*.dtsi`),按核数/型号挑一个软链进去;
- 基地址、寄存器偏移、boot 参数等硬件常量**散落**在脚本与片段里;
- 核 layout 只有 2 / 4 / 16 核几种预设,不支持任意核数;
- 无任何自动校验,改动只能靠人工比对。

### 二、改造后架构

```
  平台 JSON  ──┐                     update_dts_cpu.sh(薄封装)
 (硬件常量)    │   ┌──────────────┐        │
               ├──▶│ dtsi_engine  │──▶ ┌───┴───────────────────────┐
  模板 .in   ──┘   │  (渲染引擎)   │    │ generate_dts.py           │──▶ .hw.dts
 (结构+占位)        └──────────────┘    │ render_gdbinit.py         │──▶ zsb.gdbinit
                                        │ render_socinit.py         │──▶ zsb_socinit.gdbinit
                                        └───────────────────────────┘
```

三层分离 + 一层薄封装:

1. **数据源**:`data/platforms/*.json` —— 所有硬件常量(基地址、偏移、boot 参数);
2. **模板**:`data/templates/*.dtsi.in` / `gdbinit/*.in` —— 只保留结构与占位;
3. **引擎**:`generator/dtsi_engine.py` —— `${MACRO}` / `##IF##` / `##REPEAT##` 渲染;
4. **薄封装**:`update_dts_cpu.sh` 保留原有命令行,转调三个 Python 生成器。

### 三、改动清单

| 类别 | 内容 |
|------|------|
| 新增生成器 | `generator/generate_dts.py`、`generator/render_gdbinit.py`、`generator/render_socinit.py`、`generator/render_intc_dtsi.py`、`generator/render_ntrace_dts.py` |
| 新增数据 | `data/platforms/{xiaohui_v1,xiaohui_v2,xiaohui_v1_rv32}.json` + `data/platforms/platform-selection.csv`(CPU→变体,含 ntrace 版本列)+ `data/soc-schema.json`;`data/ntrace/ntrace_xiaohui_{v1,v2}.json` + `data/ntrace-schema.json` |
| 新增模板 | cpu / gmac×3 / intc×2 / pmu×3 / pmc / link(xl200+xl300)/ ntrace×4 / `zsb.dts.in` + gdbinit×2(`.in`) |
| 新增校验 | `tests/generate_golden_refs.py`(38 例)+ `tests/generate_golden_refs.sh` + `generator/normalize_plic_isa.py` + `tests/golden-refs-master/` 基线 |
| 删除 | 全部 legacy 静态 `.dtsi` 片段(`cpu`/`gmac_*`/`pmu_*`/`pmc`/`intc`/`ntrace-*`/`32cores`/`xuantie-link-*` 及 `aplic_{2,4,16}cores`/`aplic_c930/rv64` 等)、`zsb_rv{32,64}.dts`、`generate_trace_dts.sh`(930 行)、`data/trace-configs/*.json`(7 个,拓扑改由 `-layout` 推导) |
| 重写 | `update_dts_cpu.sh` 收敛为薄封装;`run_fpga.sh` 加固 rootfs 校验 |

### 四、设计要点

- **base + offset 派生地址**:ncore 的 snoop/dvm、ipmc 的 per-core 寄存器,统一由
  `基地址 + i×stride + offset` 计算,不再硬编码绝对地址,便于阅读与移植。
- **有序写序列**:GMAC 等初始化用 `[{offset, value}]` 列表表达,顺序即执行顺序。
- **hi/lo 单宏**:reg 的地址/大小用单个 `${..}` 宏同时携带 hi/lo 两个 cell,
  由引擎拆分,避免模板里手写高低位。
- **平台可选**:优先级 `PLATFORM_CONFIG` 环境变量 > `<soc_platform>` 位置参数
  (需匹配到 json)> 按 CPU 自动选。
- **xl200 单一入口**:`link/link_xl200.dtsi.in` 与 `link/link_xl300.dtsi.in` 同目录平行,
  后续 xl200 配置只改这一处。
- **任意核数**:核 layout 由使能位串驱动,不再受限于 2/4/16 核预设。
- **N-Trace 拓扑由 `-cpu`+`-layout` 推导**:trace 版本由 CPU 选(`platform-selection.csv`
  的 `ntrace` 列,唯一源,v1=C907/C908/R908,v2=C930/C925/C920,c950 fail-fast),硬件常量按「SoC 家族 + 版本」存
  `data/ntrace/ntrace_<家族>_v<版本>.json`;拓扑走 **8-window 规则**(全局位 `p` → `cluster=p//8`,
  `core=p%8`),只为 enabled 核渲染 encoder/funnel1,节点名按全局位编号(与 cpu 节点 /
  gdbinit 复位编号一致)。退役了外部 `trace-configs/*.json`。
- **版本→算法用 `topology_model` 数据分派**:每个 `data/ntrace/ntrace_<家族>_v<版本>.json` 声明一个
  `topology_model`(现有两种:`single-l2-funnel`=v1、`chained-dmi`=v2),
  `render_ntrace_dts.render` 按此字段经 `TOPOLOGY_MODELS` 注册表选渲染函数,**不按版本字符串**。
  由此**加一个复用已有拓扑的新 trace 版本 = 纯数据改动**:改 `platform-selection.csv` 的 `ntrace`
  列 + `data/ntrace/ntrace_<家族>_v<版本>.json`(`topology_model` 指向已有 model),零 Python;只有**全新拓扑**才需写一个新渲染函数并在注册表
  加一行(同时把其常量键加进 `ntrace-schema.json` 的 per-model 分支)。schema 的 per-model
  required-key 也按 `topology_model` 分组,故复用 model 的新版本不必改 schema。
- **N-Trace 版本选择「归口」是待评审的开放项**(暂不定论,保留两套逻辑对照):
  - **原先实现(独立选择)**:CPU→trace 版本曾由**独立配置** `data/ntrace/cpu-trace-version.csv`
    维护,与平台/intc/pmu/gmac 等选择解耦——trace 版本被视为**独立轴**,单独一张表(或
    单独 json)描述,改版本只动 trace 自己的文件,不触碰平台选择表。
  - **当前实现(统一 platform 定义)**:已把版本并入 `platform-selection.csv` 的 `ntrace`
    列作**唯一源**(`select_trace_config` 读该列;`cpu-trace-version.csv` 已删),
    好处是「一个 CPU 的所有 block 选择」集中一行、fnmatch 一次命中;
    代价是 trace 版本与平台选择表耦合在同一文件。
  - **评审要点**:统一表(当前,聚合直观、单一事实源)vs. trace 单独开 json/csv 配置
    (原先,轴解耦、trace 可独立演进)哪种更合适,**留待评审定夺**;无论结论如何,
    「版本→算法」仍由 `data/ntrace/ntrace_<家族>_v<版本>.json` 的 `topology_model` 经 `TOPOLOGY_MODELS`
    分派(此层不受选择归口影响)。当前代码保持统一表实现,便于回退到独立配置。
  - **触发场景(为何重提此开放项)**:出现「同一 trace 拓扑(如 v1 single-l2-funnel)、
    但 `dmi_apb_base` 随 SoC 家族变化」的需求。apb 本质是**平台 address-map 属性**,不是
    版本属性,故已把 SoC 家族并入 ntrace 文件名(见下「现有方案」),从命名层面解开
    「同拓扑异 apb」;此项仍留作**「归口」**讨论。
  - **现有方案(SoC 家族 + 版本命名,已落地)**:ntrace 配置文件按
    `ntrace_<SoC 家族>_v<trace 版本>.json` 命名(现有 `ntrace_xiaohui_v1` /
    `ntrace_xiaohui_v2`)。`-ntrace` 接受 `false` / `true` / `<ntrace-json 名>`——传 `<名>`
    直接用 `data/ntrace/<名>.json`(绕过 CSV,仅调试用);`true`(或裸 `-ntrace`)按 CPU 经 CSV
    自动选:`select_trace_config(cpu, platform)` 先由 CSV `ntrace` 列定版本 `v1`/`v2`,再由平台名
    取 SoC 家族(`_soc_family`:`xiaohui_v1`/`xiaohui_v2`/`xiaohui_v1_rv32` → `xiaohui`),
    拼出 `ntrace_<家族>_v<版本>`。同家族同版本共用一个 apb map(如 c920v3 在 xiaohui_v1、
    c930 在 xiaohui_v2,均用 `ntrace_xiaohui_v2`)。**apb/base 地址随 SoC 家族变化的应用方式 =
    在 `data/ntrace/` 新增 `ntrace_<新家族>_v<版本>.json`**(拷贝同版本文件,改 `dmi_apb_base`
    等,`topology_model` 不变),自动选择即命中,零 Python、零 CLI 改动;拓扑仍由 `-layout`
    推导、常量走 schema 校验。仍开放的是**「归口」本身**——统一 `platform-selection.csv`
    vs. trace 独立 json/csv 配置,留待评审。

### 五、质量保障

- **golden 回归**(38 例):DTS 经 `dtc` 归一化做语义比对(不受 hi/lo、大小写、
  格式差异影响);gdbinit 做逐字节比对。
- **schema 校验**:平台 JSON 受 `soc-schema.json` 约束(`jsonschema` 可选)。
- **基线可信**:`tests/golden-refs-master/` 由 master 旧生成器产出,确保新旧输出等价。

### 六、兼容性

命令行开关名称与默认值大多保留,旧命令基本可原样运行;仅少数差异(位置参数
语义、`-bus stlk3`、`PLATFORM_CONFIG`、`-ntrace` 取值改为「json 文件名」、`-trace-sba`)需注意。
逐项对照与迁移注意详见下方「与旧脚本(master)的参数差异 / 迁移说明」。

---

## 与旧脚本(master)的参数差异 / 迁移说明

命令行开关(`-cpu -layout -isa -timer -bus -intc -sv -gmac -ntrace
-memsize -optee -pmc`)的名称与默认值大多保留,`-cpu` 仍由
`data/cpu-isa-configs/*.conf` 校验 —— **旧命令基本可原样运行**。差异集中在:

| 项 | master(旧 sed 脚本) | 当前分支 |
|----|----------------------|----------|
| `<soc_platform>` 位置参数 | 指基础 DTS 文件名 `dts/<PLATFORM>.dts`(`zsb_rv64` / `zsb_rv32` / `xiaohui`),驱动 `sed` + `ln -sr` 拼装 | 指平台 JSON 名 `data/platforms/<name>.json`(`xiaohui_v1` / `xiaohui_v2` / `xiaohui_v1_rv32`);不匹配 json 时按 CPU 自动选 |
| `-bus` 取值 | `none / stlk / stlk2 / ncore` | 新增 **`stlk3`**(XuanTie Link 300 / xl300) |
| `-pa`(PA48 高物理窗口) | `-pa PA48` 把 RAM/initrd/OpenSBI 重定位到 `0xfffe_0000_0000` | **已移除**:当前 FPGA bit 的内存地址已固化,无法用参数改写,故删除该开关(内核页表级数改用 `-sv`) |
| `PLATFORM_CONFIG` 环境变量 | 无 | 新增,平台覆盖(优先级高于位置参数) |
| `-ntrace` 取值 | 配置名(`v1` / cfg 文件名),**拓扑也由该 cfg 描述** | `false` / `true`(按 `-cpu`/平台自动选 `ntrace_<家族>_v<版本>`)/ `<ntrace-json 名>`(直接用 `data/ntrace/<名>.json`,如 `ntrace_xiaohui_v1`/`ntrace_xiaohui_v2` 或 apb/base 变体);**拓扑改由 `-layout`(8-window)推导**,不再来自 cfg |
| `-trace-sba` 开关 | 无(sba 藏在 cfg 的 `sba_dmi_addr` 字段) | 新增独立开关(v2 sink 走 system-bus-access 寻址) |
| 底层引擎(非参数) | 脚本内 `sed` + 对 `dts/*.dtsi` 做 `ln -sr` | 薄封装,转调 `generate_dts.py` / `render_gdbinit.py` / `render_socinit.py`(模板 + 平台 JSON) |

**迁移注意:**

1. **位置参数含义变了**。master 传 `zsb_rv64` / `zsb_rv32` 会去找
   `dts/zsb_rv64.dts`;当前分支这些名字没有对应的 `data/platforms/*.json`,
   会被忽略并**回退按 CPU 自动选**平台(见「工作原理」的平台自动选择规则)。
   要精确指定 xiaohui 版本,直接传 `xiaohui_v1` / `xiaohui_v2` /
   `xiaohui_v1_rv32`。
2. 平台覆盖优先级:`PLATFORM_CONFIG` 环境变量 > `<soc_platform>` 位置参数
   (需匹配到 json) > 按 CPU 自动选。
3. **`-ntrace` 取值语义变了**。`false`/`true` 之外,`-ntrace <名>` 现在**指定一个
   常量 json**——直接用 `data/ntrace/<名>.json`(如 `-ntrace ntrace_xiaohui_v1` /
   `-ntrace ntrace_xiaohui_v2`,或 apb/base 变体),绕过 CSV;`true` 则按 `-cpu`/平台
   自动选 `ntrace_<家族>_v<版本>`。与 master 不同的是:master 的 cfg **还描述拓扑**,
   现在**拓扑一律由 `-layout` 按 8-window 推导**,json 只提供硬件常量并用 `topology_model`
   声明拓扑算法。因此 **apb/base 地址随 SoC 家族变化 = 在 `data/ntrace/` 新增
   `ntrace_<新家族>_v<版本>.json`(改 `dmi_apb_base` 等、`topology_model` 指向已有拓扑)**,
   自动选择即命中,无需改 Python。
   `trace-configs/*.json`(master 旧格式)已删;sba 从 cfg 字段提升为独立开关
   `-trace-sba`。未知的 `<名>` 会 fail-fast 并列出可用配置。
