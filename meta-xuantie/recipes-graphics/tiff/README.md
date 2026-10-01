# tiff 4.6.0（backport）

[linux-sdk-ai-expert]

## 为什么引入

工单 84841963（vllm deb 源补包）要求 `libtiff6`（libtiff.so.6）。oe-core 树内
4.4.0 只产 libtiff.so.5，故在 meta-xuantie backport 4.6.0（SOVERSION 6），
并以 `PREFERRED_VERSION_tiff = "4.6.0"`（xuantie-base.inc）覆盖。

## 来源与裁剪

以树内 `openembedded-core/meta/recipes-multimedia/libtiff/tiff_4.4.0.bb` 为
模板，bump 至 4.6.0：

- 移除 4.4.0 专属补丁（上游已修复）与对应 CVE_CHECK_IGNORE
- `LIC_FILES_CHKSUM` 改为 `LICENSE.md`（4.6.0 起替代 COPYRIGHT 文件）
- PACKAGECONFIG 默认项在 4.4.0 基础上增加 `jbig`（libjbig 由本层新增
  jbig 2.1 recipe 提供），并新增 lerc/webp/zstd 可选项

## 编译参数

走 Yocto 默认 flags（本包为 ABI 稳定的运行时共享库，不继承
xuantie-set-package-arch-with-mcpu，不做 per-mcpu 出包）。

## 打包

- `tiff`：libtiff.so.6 主库
- `tiffxx`：libtiffxx.so.*
- `tiff-utils`：${bindir} 工具
- PACKAGE_ARCH = riscv64，落 `tmp/deploy/deb/riscv64/`，随 rva23/common feed 发布
