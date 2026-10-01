# libdeflate 1.19（新建，backport）

[linux-sdk-ai-expert]

## 为什么引入

工单 84841963（vllm deb 源补包）要求 `libdeflate0`（libdeflate.so.0）。
树内无该 recipe；从 OE 上游 backport 并按 langdale 基线裁剪。

## 配置

- SRC_URI：github ebiggers/libdeflate v1.19 归档
- `inherit cmake`：
  - `-DLIBDEFLATE_BUILD_SHARED_LIB=ON`（上游默认关）
  - `-DLIBDEFLATE_BUILD_STATIC_LIB=OFF`（只需共享库）
- 产出 libdeflate.so.0（上游 SOVERSION 0）

## 编译参数

走 Yocto 默认 flags（ABI 稳定运行时库，不做 per-mcpu 出包）。
