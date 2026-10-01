# jbig 2.1（jbigkit，新建）

[linux-sdk-ai-expert]

## 为什么引入

工单 84841963（vllm deb 源补包）要求 `libjbig0`（libjbig.so.0）。树内无该
recipe；同时 tiff 4.6.0 的 PACKAGECONFIG[jbig] 需要它。从 meta-oe 上游
backport 并按交叉编译裁剪。

## 关键处理

- 上游只产静态库（Makefile 直接 ar），recipe 内自定义 do_compile 用
  `${CC} ${CFLAGS} -fPIC` 编出共享库并显式指定 soname
  （libjbig.so.0 / libjbig85.so.0），与 Debian libjbig0 对齐
- SRC_URI 用上游原始文件名 `jbigkit-${PV}.tar.gz`（不能用 ${BP}，BPN 是 jbig）；
  `S = ${WORKDIR}/jbigkit-${PV}` 同理
- LICENSE = GPL-2.0-only：注意它只作为 tiff 的可选项（PACKAGECONFIG jbig）
  与本 feed 的独立库存在，链接进应用时的 GPL 传染面与 Debian 发行口径一致

## 编译参数

走 Yocto 默认 flags（ABI 稳定运行时库，不做 per-mcpu 出包）。
