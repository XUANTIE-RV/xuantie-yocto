# lerc 4.0.0（新建）

[linux-sdk-ai-expert]

## 为什么引入

工单 84841963（vllm deb 源补包）要求 `liblerc4`（libLerc.so.4，注意大写 L）。
树内无该 recipe；OE 各层在 langdale 基线亦无，故新建。

## 来源与配置

- SRC_URI：github Esri/lerc v4.0.0 release 归档（downloadfilename 固定为
  ${BP}.tar.gz，避免与 github 归档命名漂移）
- `inherit cmake`，`EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON"`：上游 CMake
  默认静态，需显式开共享库；产出 libLerc.so.4（上游 SOVERSION 4）

## 编译参数

走 Yocto 默认 flags（ABI 稳定运行时库，不做 per-mcpu 出包）。
