# libwebp 1.4.0（backport）

[linux-sdk-ai-expert]

## 为什么引入

工单 84841963（vllm deb 源补包）要求 `libwebp7`（libwebp.so.7）与
`libsharpyuv0`（libsharpyuv.so.0）。oe-core 树内 1.2.4 无 libsharpyuv
独立拆分（1.3+ 才有），故在 meta-xuantie backport 1.4.0，并以
`PREFERRED_VERSION_libwebp = "1.4.0"`（xuantie-base.inc）覆盖。

## 关键点

- `PACKAGE_BEFORE_PN =+ "libsharpyuv"`：必须用 PACKAGE_BEFORE_PN 而非
  `PACKAGES =+`，否则 libsharpyuv.so.* 会被贪婪的 ${PN} FILES 抢走
- PACKAGECONFIG 默认开 `jpeg png`（源内已有 libjpeg-turbo / libpng），
  与 Debian libwebp7 的依赖面对齐

## 编译参数

走 Yocto 默认 flags（ABI 稳定运行时库，不做 per-mcpu 出包）。

## 打包

- `libwebp`（lib_package 语义下的库本体）：libwebp/libwebpdemux/libwebpmux
- `libsharpyuv`：libsharpyuv.so.0
- PACKAGE_ARCH = riscv64，随 rva23/common feed 发布
